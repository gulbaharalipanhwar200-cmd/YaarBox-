package com.example.data

enum class NavigationTab(val labelUrdu: String, val labelEn: String) {
  CHATS("چیٹس", "Chats"),
  FRIENDS("دوست", "Friends"),
  YAARTOK("یا ٹوک", "YaarTok"),
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

data class YaarTokVideo(
  val id: String,
  val videoUrl: String,
  val creatorName: String,
  val username: String,
  val creatorAvatarRes: Int? = null,
  val creatorAvatarUrl: String? = null,
  val caption: String,
  val songTitle: String = "Original Sound - YaarBox",
  val likesCount: Int = 12500,
  val commentsCount: Int = 1200,
  val sharesCount: Int = 450,
  val isLiked: Boolean = false,
  val isFollowed: Boolean = false,
  val bgGradientStart: Long = 0xFF0F172A,
  val bgGradientEnd: Long = 0xFF020617
)

data class YaarTokComment(
  val id: String,
  val authorName: String,
  val authorAvatarRes: Int? = null,
  val text: String,
  val timeAgo: String,
  val likes: Int = 12
)
