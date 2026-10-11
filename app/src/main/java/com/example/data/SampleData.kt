package com.example.data

import com.example.R

object SampleData {
  val saraKhan = Friend(
    id = "friend_sara",
    name = "Sara Khan",
    avatarRes = R.drawable.avatar_sara,
    isOnline = true,
    statusText = "Online • Active now",
    lastSeen = "Active now",
    phone = "+92 300 8765432",
    bio = "Coffee lover ☕ | Always on YaarBox!",
    unreadCount = 1
  )

  val areeb = Friend(
    id = "friend_areeb",
    name = "Areeb",
    avatarRes = R.drawable.avatar_areeb,
    isOnline = true,
    statusText = "Online • Active now",
    lastSeen = "Active now",
    phone = "+92 321 9876543",
    bio = "Mobile UI designer & tech geek 🚀",
    unreadCount = 2
  )

  val friendsList = listOf(
    areeb,
    saraKhan,
    Friend(
      id = "friend_alex",
      name = "Alex Chen",
      avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "Active now •",
      lastSeen = "Active now",
      phone = "+1 415 555 2671"
    ),
    Friend(
      id = "friend_emma",
      name = "Emma Rodriguez",
      avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "Playing Spotify • Dil Diyan Gallan",
      lastSeen = "Active now",
      phone = "+1 415 555 8923"
    ),
    Friend(
      id = "friend_ryan",
      name = "Ryan Lee",
      avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "Online • بات کرنے کو دل کر رہا ہے 😍",
      lastSeen = "Active now",
      phone = "+92 333 4567890"
    ),
    Friend(
      id = "friend_sofia",
      name = "Sofia Martinez",
      avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
      isOnline = false,
      statusText = "Away • 5m ago",
      lastSeen = "5m ago",
      phone = "+1 310 555 4192"
    ),
    Friend(
      id = "friend_arjun",
      name = "Arjun",
      avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "At gym • Yaar workout time 🏋️",
      lastSeen = "Active now",
      phone = "+91 98200 12345"
    ),
    Friend(
      id = "friend_mia",
      name = "Mia",
      avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "Listening to Coke Studio 🎧",
      lastSeen = "Active now",
      phone = "+92 312 3456789"
    ),
    Friend(
      id = "friend_ali",
      name = "Ali",
      avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "Online - یار کہاں ہو سب؟",
      lastSeen = "Active now",
      phone = "+92 345 6789012"
    ),
    Friend(
      id = "friend_zoya",
      name = "Zoya",
      avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
      isOnline = true,
      statusText = "ادھر ہی ہوں YaarBox پر 😍",
      lastSeen = "Active now",
      phone = "+92 301 2345678"
    )
  )

  val initialStories = listOf(
    Story(
      id = "story_1",
      friendId = "friend_emma",
      friendName = "Emma",
      avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
      contentText = "Beautiful sunset evening in Lahore! 🌆✨ #YaarBoxStories",
      timeAgo = "12m ago",
      gradientStart = 0xFF7C3AED,
      gradientEnd = 0xFFEC4899
    ),
    Story(
      id = "story_2",
      friendId = "friend_ryan",
      friendName = "Ryan",
      avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
      contentText = "Coding all night with YaarBox beats 🎧☕",
      timeAgo = "35m ago",
      gradientStart = 0xFF0D6352,
      gradientEnd = 0xFF10B981
    ),
    Story(
      id = "story_3",
      friendId = "friend_sofia",
      friendName = "Sofia",
      avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
      contentText = "New design mockups uploaded! What do you guys think? 🎨",
      timeAgo = "1h ago",
      gradientStart = 0xFF2563EB,
      gradientEnd = 0xFF06B6D4
    ),
    Story(
      id = "story_4",
      friendId = "friend_arjun",
      friendName = "Arjun",
      avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200&auto=format&fit=crop&q=80",
      contentText = "PR day at gym! 120kg squat 💪",
      timeAgo = "2h ago",
      gradientStart = 0xFFDC2626,
      gradientEnd = 0xFFF97316
    ),
    Story(
      id = "story_5",
      friendId = "friend_mia",
      friendName = "Mia",
      avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=200&auto=format&fit=crop&q=80",
      contentText = "Weekend vibes with Yaar gang 🌸☕",
      timeAgo = "4h ago",
      gradientStart = 0xFF9333EA,
      gradientEnd = 0xFF4F46E5
    )
  )

  val initialChats: Map<String, List<ChatMessage>> = mapOf(
    "friend_areeb" to listOf(
      ChatMessage(
        id = "m1",
        senderId = "friend_areeb",
        text = "Hey! Did you check the new project files I sent?",
        timestamp = "8:32 PM",
        isMe = false
      ),
      ChatMessage(
        id = "m2",
        senderId = "me",
        text = "Just opened it now, looks great! Love the design updates",
        timestamp = "8:33 PM",
        isMe = true
      ),
      ChatMessage(
        id = "m3",
        senderId = "friend_areeb",
        text = "Glad you like it! Should we hop on a call to finalize?",
        timestamp = "8:33 PM",
        isMe = false
      )
    ),
    "friend_sara" to listOf(
      ChatMessage(
        id = "s1",
        senderId = "friend_sara",
        text = "اسلام علیکم! YaarBox پر آنے کا شکریہ 👋",
        timestamp = "7:15 PM",
        isMe = false
      ),
      ChatMessage(
        id = "s2",
        senderId = "me",
        text = "وعلیکم السلام سارہ! ایپ کا ڈیزائن کمال کا ہے۔",
        timestamp = "7:18 PM",
        isMe = true
      ),
      ChatMessage(
        id = "s3",
        senderId = "friend_sara",
        text = "یار بات کرنے کو دل کر رہا تھا، کیا اب کال کروں؟ 😊",
        timestamp = "7:20 PM",
        isMe = false
      )
    ),
    "friend_alex" to listOf(
      ChatMessage(
        id = "a1",
        senderId = "friend_alex",
        text = "Yo! Ready for the weekend gaming lobby?",
        timestamp = "6:45 PM",
        isMe = false
      ),
      ChatMessage(
        id = "a2",
        senderId = "me",
        text = "Definitely! Let's connect on YaarBox call later.",
        timestamp = "6:50 PM",
        isMe = true
      )
    )
  )

  val initialCallHistory = listOf(
    CallHistoryItem(
      id = "call_1",
      friend = saraKhan,
      callType = CallType.VOICE,
      direction = CallDirection.INCOMING,
      timestamp = "Today, 8:40 PM",
      duration = "4 min 12 sec"
    ),
    CallHistoryItem(
      id = "call_2",
      friend = areeb,
      callType = CallType.VIDEO,
      direction = CallDirection.OUTGOING,
      timestamp = "Today, 7:15 PM",
      duration = "12 min 30 sec"
    ),
    CallHistoryItem(
      id = "call_3",
      friend = friendsList[3], // Emma
      callType = CallType.VOICE,
      direction = CallDirection.MISSED,
      timestamp = "Yesterday, 10:20 PM",
      duration = "Missed"
    ),
    CallHistoryItem(
      id = "call_4",
      friend = friendsList[4], // Ryan
      callType = CallType.VOICE,
      direction = CallDirection.INCOMING,
      timestamp = "Yesterday, 5:10 PM",
      duration = "1 min 45 sec"
    )
  )

  val initialVideos = listOf(
    YaarTokVideo(
      id = "video_1",
      videoUrl = "https://flutter.github.io/assets-for-api-docs/assets/videos/bee.mp4",
      creatorName = "Sara Khan",
      username = "@sara_khan",
      creatorAvatarRes = R.drawable.avatar_sara,
      caption = "میری پہلی YaarBox ویڈیو ❤️ دوستوں کے ساتھ نیا تجربہ! #YaarBox #YaarTok #GoodVibes",
      songTitle = "Original Sound - Dil Diyan Gallan (YaarBox Remix)",
      likesCount = 14200,
      commentsCount = 1320,
      sharesCount = 540,
      isLiked = false,
      bgGradientStart = 0xFF0D6352,
      bgGradientEnd = 0xFF10B981
    ),
    YaarTokVideo(
      id = "video_2",
      videoUrl = "https://flutter.github.io/assets-for-api-docs/assets/videos/butterfly.mp4",
      creatorName = "Areeb",
      username = "@areeb_official",
      creatorAvatarRes = R.drawable.avatar_areeb,
      caption = "سفر اور فطرت کے حسین رنگ 🦋🌿 YaarBox squad trip! #YaarTok #Travel #NatureVibes",
      songTitle = "Original Sound - Pasoori Lo-Fi Mix",
      likesCount = 28500,
      commentsCount = 2410,
      sharesCount = 1120,
      isLiked = true,
      bgGradientStart = 0xFF7C3AED,
      bgGradientEnd = 0xFFEC4899
    ),
    YaarTokVideo(
      id = "video_3",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
      creatorName = "Alex Chen",
      username = "@alex_chen",
      creatorAvatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200&auto=format&fit=crop&q=80",
      caption = "Weekend gaming and coding party with the yaars 🔥🎮 Who wants to squad up? #Gaming #Friends",
      songTitle = "Original Sound - YaarBox Electronic Beats",
      likesCount = 9800,
      commentsCount = 890,
      sharesCount = 310,
      isLiked = false,
      bgGradientStart = 0xFF2563EB,
      bgGradientEnd = 0xFF06B6D4
    ),
    YaarTokVideo(
      id = "video_4",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
      creatorName = "Ryan Lee",
      username = "@ryan_lee",
      creatorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
      caption = "Night drive vibes in Lahore 🌙🚗 کبھی کبھی تنہائی بھی خوبصورت ہوتی ہے۔ #NightDrive #YaarBox",
      songTitle = "Original Sound - Coke Studio Vibes",
      likesCount = 34100,
      commentsCount = 3980,
      sharesCount = 1890,
      isLiked = false,
      bgGradientStart = 0xFFDC2626,
      bgGradientEnd = 0xFFF97316
    )
  )

  val sampleComments = listOf(
    YaarTokComment("c1", "Ali", R.drawable.avatar_areeb, "واہ یار! بہت ہی زبردست ویڈیو ہے 🔥❤️", "2m ago", 34),
    YaarTokComment("c2", "Sara Khan", R.drawable.avatar_sara, "Next video meri taraf se aayegi! 😍", "5m ago", 82),
    YaarTokComment("c3", "Emma", null, "Loving the YaarBox update! So clean 🚀", "12m ago", 15),
    YaarTokComment("c4", "Zoya", null, "سچی دوستی ہمیشہ قائم رہتی ہے ❤️✨", "25m ago", 48)
  )
}
