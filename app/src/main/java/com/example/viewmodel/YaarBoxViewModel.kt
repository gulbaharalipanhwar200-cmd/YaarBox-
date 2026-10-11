package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ActiveCall
import com.example.data.AiActionResult
import com.example.data.CallDirection
import com.example.data.CallHistoryItem
import com.example.data.CallState
import com.example.data.CallType
import com.example.data.ChatMessage
import com.example.data.Friend
import com.example.data.NavigationTab
import com.example.data.SampleData
import com.example.data.Story
import com.example.data.YaarBoxAiEngine
import com.example.data.YaarBoxSoundService
import com.example.data.YaarBoxTtsEngine
import com.example.data.YaarTokComment
import com.example.data.YaarTokVideo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class YaarBoxUiState(
  val currentTab: NavigationTab = NavigationTab.FRIENDS,
  val friends: List<Friend> = SampleData.friendsList,
  val stories: List<Story> = SampleData.initialStories,
  val chatMessages: Map<String, List<ChatMessage>> = SampleData.initialChats,
  val callHistory: List<CallHistoryItem> = SampleData.initialCallHistory,
  val activeChatFriend: Friend? = null,
  val activeCall: ActiveCall? = null,
  val viewingStory: Story? = null,
  val searchQuery: String = "",
  val isSearchActive: Boolean = false,
  val showAddFriendDialog: Boolean = false,
  val showAddStoryDialog: Boolean = false,
  val soundEnabled: Boolean = true,
  val myName: String = "Gulbahar Ali",
  val myBio: String = "Building awesome Android apps on YaarBox! 🚀",
  val myPhone: String = "+92 300 9876543",
  val isMyOnline: Boolean = true,
  // AI Assistant States
  val isAiSheetOpen: Boolean = false,
  val aiQuery: String = "",
  val aiResponse: String = "جی حکم کریں یار! میں آپ کا ڈیجیٹل یار ہوں، کال، میسج، ترجمہ یا کچھ بھی بولیں۔",
  val isAiThinking: Boolean = false,
  val isAiListening: Boolean = false,
  val autoAiOnStart: Boolean = true,
  // YaarTok Reels State
  val videos: List<YaarTokVideo> = SampleData.initialVideos,
  val videoComments: Map<String, List<YaarTokComment>> = mapOf(
    "video_1" to SampleData.sampleComments,
    "video_2" to SampleData.sampleComments,
    "video_3" to SampleData.sampleComments,
    "video_4" to SampleData.sampleComments
  )
)

class YaarBoxViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(YaarBoxUiState())
  val uiState: StateFlow<YaarBoxUiState> = _uiState.asStateFlow()

  private var callTimerJob: Job? = null
  private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

  private val aiEngine = YaarBoxAiEngine()
  private var ttsEngine: YaarBoxTtsEngine? = null

  init {
    // Play signature open sound on launch
    YaarBoxSoundService.playOpenSound()
  }

  fun initializeTts(context: Context) {
    if (ttsEngine == null) {
      ttsEngine = YaarBoxTtsEngine(context)
    }
  }

  fun openAiAssistant(greeting: Boolean = true) {
    YaarBoxSoundService.playPopSound()
    _uiState.update { it.copy(isAiSheetOpen = true, isAiListening = false) }
    if (greeting) {
      val greetingText = "جی حکم کریں یار؟"
      _uiState.update { it.copy(aiResponse = greetingText) }
      ttsEngine?.speak(greetingText, "ur")
    }
  }

  fun closeAiAssistant() {
    ttsEngine?.stop()
    _uiState.update { it.copy(isAiSheetOpen = false, isAiListening = false) }
  }

  fun startAiVoiceListening() {
    ttsEngine?.stop()
    _uiState.update { it.copy(isAiListening = true, aiQuery = "سن رہا ہوں...") }
    // Simulate real speech detection window if speech input completes
    viewModelScope.launch {
      delay(2800)
      if (_uiState.value.isAiListening) {
        val simulatedSpoken = "علی کو کال لگاؤ"
        submitAiCommand(simulatedSpoken)
      }
    }
  }

  fun stopAiVoiceListening() {
    _uiState.update { it.copy(isAiListening = false) }
  }

  fun submitAiCommand(command: String) {
    if (command.isBlank()) return
    _uiState.update {
      it.copy(
        aiQuery = command,
        isAiListening = false,
        isAiThinking = true
      )
    }

    viewModelScope.launch {
      val result = aiEngine.processCommand(command, _uiState.value.friends)
      _uiState.update { it.copy(isAiThinking = false) }

      when (result) {
        is AiActionResult.CallAction -> {
          _uiState.update { it.copy(aiResponse = result.spokenConfirmation) }
          ttsEngine?.speak(result.spokenConfirmation, "ur")
          delay(1200)
          closeAiAssistant()

          // Find friend or fallback to first matching/Sara
          val targetFriend = _uiState.value.friends.find {
            it.name.contains(result.friendName, ignoreCase = true) ||
              result.friendName.contains(it.name, ignoreCase = true)
          } ?: _uiState.value.friends.first()

          startCall(targetFriend, if (result.isVideo) CallType.VIDEO else CallType.VOICE)
        }

        is AiActionResult.MessageAction -> {
          _uiState.update { it.copy(aiResponse = result.spokenConfirmation) }
          ttsEngine?.speak(result.spokenConfirmation, "ur")
          delay(1200)
          closeAiAssistant()

          val targetFriend = _uiState.value.friends.find {
            it.name.contains(result.friendName, ignoreCase = true) ||
              result.friendName.contains(it.name, ignoreCase = true)
          } ?: _uiState.value.friends.first()

          openChat(targetFriend)
          sendMessage(targetFriend.id, result.messageText)
        }

        is AiActionResult.OpenScreenAction -> {
          _uiState.update { it.copy(aiResponse = result.spokenConfirmation) }
          ttsEngine?.speak(result.spokenConfirmation, "ur")
          delay(800)
          closeAiAssistant()

          when (result.screenName) {
            "stories" -> {
              _uiState.update { it.copy(currentTab = NavigationTab.FRIENDS) }
              _uiState.value.stories.firstOrNull()?.let { openStory(it) }
            }
            "yaartok" -> setTab(NavigationTab.YAARTOK)
            "chats" -> setTab(NavigationTab.CHATS)
            "calls" -> setTab(NavigationTab.CALLS)
            "profile" -> setTab(NavigationTab.PROFILE)
            else -> setTab(NavigationTab.FRIENDS)
          }
        }

        is AiActionResult.TranslationAction -> {
          _uiState.update { it.copy(aiResponse = result.translatedText) }
          ttsEngine?.speak(result.translatedText, result.targetLanguage)
        }

        is AiActionResult.GeneralResponse -> {
          _uiState.update { it.copy(aiResponse = result.replyText) }
          ttsEngine?.speak(result.spokenText, "ur")
        }
      }
    }
  }

  fun speakCurrentAiResponse() {
    val text = _uiState.value.aiResponse
    ttsEngine?.speak(text, "ur")
  }

  fun setTab(tab: NavigationTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  fun openChat(friend: Friend) {
    YaarBoxSoundService.playPopSound()
    _uiState.update {
      it.copy(
        activeChatFriend = friend,
        friends = it.friends.map { f ->
          if (f.id == friend.id) f.copy(unreadCount = 0) else f
        }
      )
    }
  }

  fun closeChat() {
    _uiState.update { it.copy(activeChatFriend = null) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleSearch(active: Boolean) {
    _uiState.update { it.copy(isSearchActive = active, searchQuery = if (active) it.searchQuery else "") }
  }

  fun sendMessage(friendId: String, text: String) {
    if (text.isBlank()) return
    YaarBoxSoundService.playPopSound()

    val currentTimestamp = timeFormat.format(Date())
    val myMessage = ChatMessage(
      id = UUID.randomUUID().toString(),
      senderId = "me",
      text = text.trim(),
      timestamp = currentTimestamp,
      isMe = true
    )

    _uiState.update { state ->
      val existing = state.chatMessages[friendId] ?: emptyList()
      state.copy(chatMessages = state.chatMessages + (friendId to (existing + myMessage)))
    }

    // Schedule smart friendly auto-reply
    val friend = _uiState.value.friends.find { it.id == friendId }
    friend?.let { f ->
      viewModelScope.launch {
        delay(1400)
        generateAutoReply(f, text)
      }
    }
  }

  private fun generateAutoReply(friend: Friend, userMsg: String) {
    val replyText = when {
      userMsg.contains("call", ignoreCase = true) || userMsg.contains("کال") ->
        "ہاں بھائی، کال کرو! میں آن لائن ہوں۔ 📞"
      userMsg.contains("design", ignoreCase = true) || userMsg.contains("project", ignoreCase = true) ->
        "I love the design! Let's ship it right away! 🚀"
      userMsg.contains("کیسے") || userMsg.contains("how", ignoreCase = true) ->
        "الحمدللہ سب زبردست! آپ سناؤ؟ 😊"
      userMsg.contains("یار") ->
        "جی میرے یار، حکم کرو! ❤️"
      else -> {
        val options = listOf(
          "Got it! Thanks for letting me know. 👍",
          "بالکل درست کہا بھائی!",
          "Great! Love talking on YaarBox ✨",
          "چلو شام کو ملتے ہیں یا کال پر بات کرتے ہیں۔"
        )
        options.random()
      }
    }

    val replyMessage = ChatMessage(
      id = UUID.randomUUID().toString(),
      senderId = friend.id,
      text = replyText,
      timestamp = timeFormat.format(Date()),
      isMe = false
    )

    YaarBoxSoundService.playPopSound()

    _uiState.update { state ->
      val existing = state.chatMessages[friend.id] ?: emptyList()
      state.copy(
        chatMessages = state.chatMessages + (friend.id to (existing + replyMessage))
      )
    }
  }

  // CALL ACTIONS
  fun startCall(friend: Friend, callType: CallType) {
    YaarBoxSoundService.startCallRingtone()
    _uiState.update {
      it.copy(
        activeCall = ActiveCall(
          friend = friend,
          callType = callType,
          state = CallState.OUTGOING,
          isVideoEnabled = callType == CallType.VIDEO
        )
      )
    }

    viewModelScope.launch {
      delay(3500)
      if (_uiState.value.activeCall?.state == CallState.OUTGOING) {
        onCallConnected()
      }
    }
  }

  fun triggerSimulatedIncomingCall(friend: Friend = SampleData.saraKhan, callType: CallType = CallType.VOICE) {
    YaarBoxSoundService.startCallRingtone()
    _uiState.update {
      it.copy(
        activeCall = ActiveCall(
          friend = friend,
          callType = callType,
          state = CallState.INCOMING,
          isVideoEnabled = callType == CallType.VIDEO
        )
      )
    }
  }

  fun acceptCall() {
    onCallConnected()
  }

  private fun onCallConnected() {
    YaarBoxSoundService.stopCallRingtone()
    YaarBoxSoundService.playPopSound()
    _uiState.update { state ->
      state.activeCall?.let { call ->
        state.copy(activeCall = call.copy(state = CallState.CONNECTED))
      } ?: state
    }

    callTimerJob?.cancel()
    callTimerJob = viewModelScope.launch {
      while (isActive) {
        delay(1000)
        _uiState.update { state ->
          state.activeCall?.let { call ->
            if (call.state == CallState.CONNECTED) {
              state.copy(activeCall = call.copy(durationSeconds = call.durationSeconds + 1))
            } else state
          } ?: state
        }
      }
    }
  }

  fun declineCall() {
    callTimerJob?.cancel()
    YaarBoxSoundService.playEndCallSound()
    val call = _uiState.value.activeCall
    if (call != null) {
      val callLog = CallHistoryItem(
        id = UUID.randomUUID().toString(),
        friend = call.friend,
        callType = call.callType,
        direction = if (call.state == CallState.INCOMING) CallDirection.MISSED else CallDirection.OUTGOING,
        timestamp = "Just now",
        duration = if (call.durationSeconds > 0) formatDuration(call.durationSeconds) else "Declined"
      )
      _uiState.update {
        it.copy(
          activeCall = null,
          callHistory = listOf(callLog) + it.callHistory
        )
      }
    } else {
      _uiState.update { it.copy(activeCall = null) }
    }
  }

  fun endCall() {
    declineCall()
  }

  fun toggleMute() {
    _uiState.update { state ->
      state.activeCall?.let { call ->
        state.copy(activeCall = call.copy(isMuted = !call.isMuted))
      } ?: state
    }
  }

  fun toggleSpeaker() {
    _uiState.update { state ->
      state.activeCall?.let { call ->
        state.copy(activeCall = call.copy(isSpeakerOn = !call.isSpeakerOn))
      } ?: state
    }
  }

  fun toggleVideo() {
    _uiState.update { state ->
      state.activeCall?.let { call ->
        state.copy(activeCall = call.copy(isVideoEnabled = !call.isVideoEnabled))
      } ?: state
    }
  }

  fun flipCamera() {
    _uiState.update { state ->
      state.activeCall?.let { call ->
        state.copy(activeCall = call.copy(isFrontCamera = !call.isFrontCamera))
      } ?: state
    }
  }

  // STORIES
  fun openStory(story: Story) {
    YaarBoxSoundService.playPopSound()
    _uiState.update { state ->
      state.copy(
        viewingStory = story,
        stories = state.stories.map { s ->
          if (s.id == story.id) s.copy(isViewed = true) else s
        }
      )
    }
  }

  fun closeStory() {
    _uiState.update { it.copy(viewingStory = null) }
  }

  fun openAddStoryDialog() {
    _uiState.update { it.copy(showAddStoryDialog = true) }
  }

  fun closeAddStoryDialog() {
    _uiState.update { it.copy(showAddStoryDialog = false) }
  }

  fun addStory(text: String, startColor: Long, endColor: Long) {
    if (text.isBlank()) return
    YaarBoxSoundService.playPopSound()
    val newStory = Story(
      id = UUID.randomUUID().toString(),
      friendId = "me",
      friendName = _uiState.value.myName,
      contentText = text.trim(),
      timeAgo = "Just now",
      isViewed = false,
      gradientStart = startColor,
      gradientEnd = endColor
    )
    _uiState.update {
      it.copy(
        stories = listOf(newStory) + it.stories,
        showAddStoryDialog = false
      )
    }
  }

  // FRIENDS
  fun openAddFriendDialog() {
    _uiState.update { it.copy(showAddFriendDialog = true) }
  }

  fun closeAddFriendDialog() {
    _uiState.update { it.copy(showAddFriendDialog = false) }
  }

  fun addFriend(name: String, phone: String, status: String) {
    if (name.isBlank()) return
    YaarBoxSoundService.playPopSound()
    val newFriend = Friend(
      id = "friend_${UUID.randomUUID().toString().take(6)}",
      name = name.trim(),
      phone = if (phone.isBlank()) "+92 300 0000000" else phone.trim(),
      statusText = if (status.isBlank()) "Online • Active on YaarBox" else status.trim(),
      isOnline = true
    )
    _uiState.update {
      it.copy(
        friends = listOf(newFriend) + it.friends,
        showAddFriendDialog = false
      )
    }
  }

  fun toggleSound(enabled: Boolean) {
    YaarBoxSoundService.setSoundEnabled(enabled)
    _uiState.update { it.copy(soundEnabled = enabled) }
  }

  // YaarTok Video Feed Interactions
  fun toggleLikeVideo(videoId: String) {
    YaarBoxSoundService.playPopSound()
    _uiState.update { state ->
      val updated = state.videos.map { vid ->
        if (vid.id == videoId) {
          val nextLiked = !vid.isLiked
          vid.copy(
            isLiked = nextLiked,
            likesCount = if (nextLiked) vid.likesCount + 1 else vid.likesCount - 1
          )
        } else vid
      }
      state.copy(videos = updated)
    }
  }

  fun toggleFollowCreator(videoId: String) {
    YaarBoxSoundService.playPopSound()
    _uiState.update { state ->
      val updated = state.videos.map { vid ->
        if (vid.id == videoId) {
          vid.copy(isFollowed = !vid.isFollowed)
        } else vid
      }
      state.copy(videos = updated)
    }
  }

  fun addVideoComment(videoId: String, text: String) {
    if (text.isBlank()) return
    YaarBoxSoundService.playPopSound()
    val newComment = YaarTokComment(
      id = UUID.randomUUID().toString(),
      authorName = _uiState.value.myName,
      authorAvatarRes = null,
      text = text.trim(),
      timeAgo = "Just now",
      likes = 0
    )
    _uiState.update { state ->
      val existing = state.videoComments[videoId] ?: emptyList()
      val updatedVideos = state.videos.map { vid ->
        if (vid.id == videoId) vid.copy(commentsCount = vid.commentsCount + 1) else vid
      }
      state.copy(
        videos = updatedVideos,
        videoComments = state.videoComments + (videoId to (listOf(newComment) + existing))
      )
    }
  }

  fun toggleMyOnlineStatus() {
    _uiState.update { it.copy(isMyOnline = !it.isMyOnline) }
  }

  private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", m, s)
  }

  override fun onCleared() {
    super.onCleared()
    callTimerJob?.cancel()
    YaarBoxSoundService.stopCallRingtone()
    ttsEngine?.shutdown()
  }
}
