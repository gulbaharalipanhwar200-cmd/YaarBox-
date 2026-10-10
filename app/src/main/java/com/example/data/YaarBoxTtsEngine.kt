package com.example.data

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class YaarBoxTtsEngine(context: Context) {
  private val tag = "YaarBoxTtsEngine"
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        // Try Urdu first, fallback to Hindi or English
        val urduLocale = Locale.forLanguageTag("ur-PK")
        val result = tts?.setLanguage(urduLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          val hiLocale = Locale.forLanguageTag("hi-IN")
          val hiResult = tts?.setLanguage(hiLocale)
          if (hiResult == TextToSpeech.LANG_MISSING_DATA || hiResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.language = Locale.US
          }
        }
        tts?.setPitch(1.05f)
        tts?.setSpeechRate(0.95f)
      } else {
        Log.e(tag, "TTS initialization failed: $status")
      }
    }

    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        _isSpeaking.value = true
      }

      override fun onDone(utteranceId: String?) {
        _isSpeaking.value = false
      }

      @Deprecated("Deprecated in Java")
      override fun onError(utteranceId: String?) {
        _isSpeaking.value = false
      }
    })
  }

  fun speak(text: String, lang: String = "ur") {
    if (!isInitialized || tts == null) return
    try {
      when (lang.lowercase()) {
        "ur", "urdu" -> {
          val locale = Locale.forLanguageTag("ur-PK")
          val res = tts?.setLanguage(locale)
          if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.forLanguageTag("hi-IN"))
          }
        }
        "en", "english" -> tts?.language = Locale.US
        "hi", "hindi" -> tts?.language = Locale.forLanguageTag("hi-IN")
        "ar", "arabic" -> tts?.language = Locale.forLanguageTag("ar")
        else -> tts?.language = Locale.US
      }

      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_${System.currentTimeMillis()}")
    } catch (e: Exception) {
      Log.e(tag, "Error speaking text", e)
      _isSpeaking.value = false
    }
  }

  fun stop() {
    try {
      tts?.stop()
      _isSpeaking.value = false
    } catch (_: Exception) {}
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
    } catch (_: Exception) {}
  }
}
