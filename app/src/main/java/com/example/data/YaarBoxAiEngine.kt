package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class AiActionResult {
  data class CallAction(val friendName: String, val isVideo: Boolean = false, val spokenConfirmation: String) : AiActionResult()
  data class MessageAction(val friendName: String, val messageText: String, val spokenConfirmation: String) : AiActionResult()
  data class OpenScreenAction(val screenName: String, val spokenConfirmation: String) : AiActionResult()
  data class TranslationAction(val translatedText: String, val targetLanguage: String, val spokenConfirmation: String) : AiActionResult()
  data class GeneralResponse(val replyText: String, val spokenText: String) : AiActionResult()
}

class YaarBoxAiEngine {
  private val tag = "YaarBoxAiEngine"

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  /**
   * Process a voice or text command from the user.
   * Uses local fast regex matching first, and falls back to Gemini 3.5 Flash for natural language.
   */
  suspend fun processCommand(
    command: String,
    availableFriends: List<Friend>
  ): AiActionResult = withContext(Dispatchers.IO) {
    val cleanCommand = command.trim()
    val lower = cleanCommand.lowercase()

    // 1. FAST LOCAL INTENT MATCHER (instant response)
    // CALL INTENT: "علی کو کال کرو", "call sara", "سارہ کو ویڈیو کال لگاؤ"
    if (lower.contains("کال") || lower.contains("call")) {
      val isVideo = lower.contains("ویڈیو") || lower.contains("video")
      val matchedFriend = findFriendInQuery(cleanCommand, availableFriends)
      val friendName = matchedFriend?.name ?: extractTargetName(cleanCommand, "کال")
      val confirmation = if (isVideo) "$friendName کو ویڈیو کال لگا رہا ہوں" else "$friendName کو کال لگا رہا ہوں"
      return@withContext AiActionResult.CallAction(
        friendName = friendName,
        isVideo = isVideo,
        spokenConfirmation = confirmation
      )
    }

    // MESSAGE INTENT: "سارہ کو میسج کرو میں آ رہا ہوں", "message ali hey"
    if (lower.contains("میسج") || lower.contains("message") || lower.contains("msg")) {
      val matchedFriend = findFriendInQuery(cleanCommand, availableFriends)
      val friendName = matchedFriend?.name ?: extractTargetName(cleanCommand, "میسج")
      val msgContent = extractMessageBody(cleanCommand)
      val confirmation = "$friendName کو میسج بھیج رہا ہوں: $msgContent"
      return@withContext AiActionResult.MessageAction(
        friendName = friendName,
        messageText = msgContent,
        spokenConfirmation = confirmation
      )
    }

    // OPEN SCREEN INTENT: "اسٹوریز کھولو", "open chats", "calls کھولو"
    if (lower.contains("کھولو") || lower.contains("open") || lower.contains("دکھاؤ")) {
      val screen = when {
        lower.contains("اسٹوری") || lower.contains("story") || lower.contains("سٹیٹس") -> "stories"
        lower.contains("ٹک ٹاک") || lower.contains("tiktok") || lower.contains("yaartok") || lower.contains("ویڈیو") || lower.contains("reels") -> "yaartok"
        lower.contains("چیٹ") || lower.contains("chat") -> "chats"
        lower.contains("کال") || lower.contains("call") -> "calls"
        lower.contains("پروفائل") || lower.contains("profile") -> "profile"
        else -> "friends"
      }
      val spoken = if (screen == "yaartok") "YaarTok ویڈیوز کھول رہا ہوں" else "کھول رہا ہوں"
      return@withContext AiActionResult.OpenScreenAction(
        screenName = screen,
        spokenConfirmation = spoken
      )
    }

    // 2. GEMINI 3.5 FLASH FOR COMPLEX AI OR TRANSLATIONS
    try {
      val geminiResult = queryGemini(cleanCommand)
      if (geminiResult != null) {
        return@withContext geminiResult
      }
    } catch (e: Exception) {
      Log.e(tag, "Gemini query error", e)
    }

    // Local default friendly reply
    AiActionResult.GeneralResponse(
      replyText = "جی میرے پیارے یار! میں آپ کا حکم سننے کے لیے تیار ہوں۔ آپ کسی کو کال یا میسج کرنے کے لیے کہ سکتے ہیں!",
      spokenText = "جی میرے یار! میں تیار ہوں"
    )
  }

  private fun findFriendInQuery(query: String, friends: List<Friend>): Friend? {
    val q = query.lowercase()
    return friends.firstOrNull { friend ->
      val name = friend.name.lowercase()
      q.contains(name) ||
        (name.contains("ali") && (q.contains("علی") || q.contains("ali"))) ||
        (name.contains("sara") && (q.contains("سارہ") || q.contains("sara"))) ||
        (name.contains("areeb") && (q.contains("اریب") || q.contains("areeb"))) ||
        (name.contains("ryan") && (q.contains("ریان") || q.contains("ryan"))) ||
        (name.contains("emma") && (q.contains("ایما") || q.contains("emma"))) ||
        (name.contains("sofia") && (q.contains("صوفیہ") || q.contains("sofia"))) ||
        (name.contains("zoya") && (q.contains("زویا") || q.contains("zoya")))
    }
  }

  private fun extractTargetName(cmd: String, keyword: String): String {
    return if (cmd.contains("کو")) {
      cmd.substringBefore("کو").replace("پلیز", "").replace("یار", "").trim()
    } else {
      cmd.replace(keyword, "").replace("call", "", ignoreCase = true)
        .replace("to", "", ignoreCase = true).trim()
    }
  }

  private fun extractMessageBody(cmd: String): String {
    val msgIdx = cmd.indexOf("message", ignoreCase = true)
    return when {
      cmd.contains("میسج کرو") -> cmd.substringAfter("میسج کرو").trim().ifBlank { "ہیلو! میں YaarBox پر ہوں" }
      cmd.contains("میسج") -> cmd.substringAfter("میسج").trim().ifBlank { "ہیلو!" }
      msgIdx >= 0 -> cmd.substring(msgIdx + 7).trim().ifBlank { "Hey!" }
      else -> "ہیلو، کیا حال ہے؟"
    }
  }

  private suspend fun queryGemini(prompt: String): AiActionResult? = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      // Mock / local smart answer
      return@withContext handleLocalAiKnowledge(prompt)
    }

    try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      val systemPrompt = """
        You are YaarBox AI, an ultra-friendly, cool South Asian AI companion inside the YaarBox Android messaging app.
        You speak Urdu, Roman Urdu, English, Punjabi, and Hindi naturally.
        If the user asks to translate something, provide the translation directly.
        Keep answers short, witty, and warmly helpful (1-3 sentences max).
      """.trimIndent()

      val jsonBody = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().put("text", prompt))
            })
          })
        })
        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().put("text", systemPrompt))
          })
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseStr = response.body?.string() ?: ""

      if (response.isSuccessful && responseStr.isNotBlank()) {
        val root = JSONObject(responseStr)
        val candidates = root.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        if (!text.isNullOrBlank()) {
          val cleanText = text.trim()
          return@withContext AiActionResult.GeneralResponse(
            replyText = cleanText,
            spokenText = cleanText.take(120)
          )
        }
      }
    } catch (e: Exception) {
      Log.e(tag, "Gemini REST error", e)
    }

    handleLocalAiKnowledge(prompt)
  }

  private fun handleLocalAiKnowledge(prompt: String): AiActionResult {
    val p = prompt.lowercase()
    val reply = when {
      p.contains("ترجمہ") || p.contains("translate") -> {
        "یہ رہا ترجمہ: 'Life is better with true friends — سچے یاروں کے ساتھ زندگی خوبصورت ہے!'"
      }
      p.contains("کون ہو") || p.contains("who are you") -> {
        "میں YaarBox کا اسمارٹ وائس اسسٹنٹ ہوں! آپ کا سچا ڈیجیٹل یار! ❤️"
      }
      p.contains("کیسے ہو") || p.contains("how are you") -> {
        "الحمدللہ بہترین! بس آپ کے حکم کا انتظار کر رہا تھا۔"
      }
      p.contains("شعر") || p.contains("poetry") -> {
        "تیرے جیسے یار کی تلاش تھی مجھے،\nمل گیا تو زندگی گلزار ہو گئی! 🌸"
      }
      p.contains("لطیفہ") || p.contains("joke") -> {
        "ایک دوست نے دوسرے سے پوچھا: سچا یار کون ہوتا ہے؟\nدوسرا بولا: جو ادھار مانگنے پر فون نہ کاٹے! 😂"
      }
      else -> {
        "جی میرے یار، آپ کے حکم پر عمل ہو رہا ہے۔ YaarBox پر بات چیت ہمیشہ آسان اور محفوظ رہتی ہے!"
      }
    }

    return AiActionResult.GeneralResponse(replyText = reply, spokenText = reply)
  }
}
