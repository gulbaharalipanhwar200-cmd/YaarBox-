package com.example.data

enum class NavigationTab(val labelUrdu: String, val labelEn: String) {
  CHATS("چیٹس", "Chats"),
  FRIENDS("دوست", "Friends"),
  CALLS("کالز", "Calls"),
  PROFILE("پروفائل", "Profile")
}

data class Friend(
  val id: String,
  val name: String,
  val avatarRes: Int? = null,
  val avatarUrl: String? = null,
  val isOnline: Boolean = true,
  val statusText: String,
  val lastSeen: String = "Active now",
  val phone: String = "+92 300 1234567",
  val bio: String = "Always ready for a chat on YaarBox! 💬",
  val unreadCount: Int = 0
)

data class ChatMessage(
  val id: String,
  val senderId: String,
  val text: String,
  val timestamp: String,
  val isMe: Boolean,
  val isRead: Boolean = true,
  val mediaType: String? = null
)

data class Story(
  val id: String,
  val friendId: String,
  val friendName: String,
  val avatarRes: Int? = null,
  val avatarUrl: String? = null,
  val contentText: String,
  val timeAgo: String,
  val isViewed: Boolean = false,
  val gradientStart: Long = 0xFF0D6352,
  val gradientEnd: Long = 0xFF10B981
)

enum class CallDirection {
  INCOMING,
  OUTGOING,
  MISSED
}

data class CallHistoryItem(
  val id: String,
  val friend: Friend,
  val callType: CallType,
  val direction: CallDirection,
  val timestamp: String,
  val duration: String
)

enum class CallType {
  VOICE,
  VIDEO
}

enum class CallState {
  INCOMING,
  OUTGOING,
  CONNECTED,
  ENDED
}

data class ActiveCall(
  val friend: Friend,
  val callType: CallType,
  val state: CallState,
  val durationSeconds: Int = 0,
  val isMuted: Boolean = false,
  val isSpeakerOn: Boolean = false,
  val isVideoEnabled: Boolean = true,
  val isFrontCamera: Boolean = true
)
