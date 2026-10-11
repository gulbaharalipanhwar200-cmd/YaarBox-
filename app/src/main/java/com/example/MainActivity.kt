package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CallType
import com.example.data.NavigationTab
import com.example.ui.components.YaarBoxBottomBar
import com.example.ui.components.YaarBoxTopBar
import com.example.ui.screens.AddFriendDialog
import com.example.ui.screens.AddStoryDialog
import com.example.ui.screens.AiAssistantSheet
import com.example.ui.screens.CallScreen
import com.example.ui.screens.CallsHistoryScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StoryViewerScreen
import com.example.ui.screens.YaarTokScreen
import com.example.ui.theme.YaarBoxTheme
import com.example.ui.theme.YaarCyan
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarGreen
import com.example.viewmodel.FriendListViewModel
import com.example.viewmodel.YaarBoxViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      YaarBoxTheme {
        YaarBoxApp()
      }
    }
  }
}

@Composable
fun YaarBoxApp(
  viewModel: YaarBoxViewModel = viewModel(),
  friendListViewModel: FriendListViewModel = viewModel()
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val friendListState by friendListViewModel.uiState.collectAsStateWithLifecycle()
  val friendsListFromVm by friendListViewModel.filteredFriends.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.initializeTts(context)
  }

  // Full screen Call overlay (Voice & Video)
  if (uiState.activeCall != null) {
    CallScreen(
      activeCall = uiState.activeCall!!,
      onAccept = { viewModel.acceptCall() },
      onDecline = { viewModel.declineCall() },
      onEndCall = { viewModel.endCall() },
      onToggleMute = { viewModel.toggleMute() },
      onToggleSpeaker = { viewModel.toggleSpeaker() },
      onToggleVideo = { viewModel.toggleVideo() },
      onFlipCamera = { viewModel.flipCamera() },
      onQuickReply = { reply ->
        viewModel.declineCall()
        uiState.activeCall?.let { call ->
          viewModel.sendMessage(call.friend.id, reply)
        }
      }
    )
    return
  }

  // Full screen Story Viewer
  if (uiState.viewingStory != null) {
    StoryViewerScreen(
      story = uiState.viewingStory!!,
      onClose = { viewModel.closeStory() },
      onSendReply = { reply ->
        viewModel.sendMessage(uiState.viewingStory!!.friendId, reply)
      }
    )
    return
  }

  // Full screen Chat Screen (when a friend chat is opened)
  if (uiState.activeChatFriend != null) {
    val friend = uiState.activeChatFriend!!
    val messages = uiState.chatMessages[friend.id] ?: emptyList()

    ChatDetailScreen(
      friend = friend,
      messages = messages,
      onBackClick = { viewModel.closeChat() },
      onSendMessage = { text -> viewModel.sendMessage(friend.id, text) },
      onVoiceCallClick = { viewModel.startCall(friend, CallType.VOICE) },
      onVideoCallClick = { viewModel.startCall(friend, CallType.VIDEO) }
    )
    return
  }

  // Main Container with TopBar and BottomBar
  val filteredFriends = if (uiState.searchQuery.isBlank()) {
    uiState.friends
  } else {
    uiState.friends.filter {
      it.name.contains(uiState.searchQuery, ignoreCase = true) ||
        it.statusText.contains(uiState.searchQuery, ignoreCase = true)
    }
  }

  val totalUnread = uiState.friends.sumOf { it.unreadCount }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(YaarDarkBg),
    containerColor = YaarDarkBg,
    topBar = {
      YaarBoxTopBar(
        isSearching = uiState.isSearchActive,
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = {
          viewModel.setSearchQuery(it)
          friendListViewModel.setSearchQuery(it)
        },
        onToggleSearch = {
          viewModel.toggleSearch(it)
          if (!it) friendListViewModel.setSearchQuery("")
        },
        onAiClick = { viewModel.openAiAssistant(greeting = true) },
        onProfileClick = { viewModel.setTab(NavigationTab.PROFILE) }
      )
    },
    bottomBar = {
      YaarBoxBottomBar(
        currentTab = uiState.currentTab,
        onTabSelected = { viewModel.setTab(it) },
        unreadChatsCount = totalUnread
      )
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (uiState.currentTab) {
        NavigationTab.FRIENDS -> {
          FriendsScreen(
            friends = friendsListFromVm,
            stories = uiState.stories,
            onFriendClick = { viewModel.openChat(it) },
            onCallClick = { friend, type -> viewModel.startCall(friend, type) },
            onStoryClick = { viewModel.openStory(it) },
            onAddStoryClick = { viewModel.openAddStoryDialog() },
            onAddFriendClick = { viewModel.openAddFriendDialog() },
            onToggleOnlineStatus = { friendId ->
              friendListViewModel.toggleFriendOnlineStatus(friendId)
            },
            filterOnlyOnline = friendListState.filterOnlyOnline,
            onToggleFilterOnline = {
              friendListViewModel.toggleFilterOnlyOnline()
            }
          )

          // Floating AI button for quick voice command on Friends screen
          FloatingActionButton(
            onClick = { viewModel.openAiAssistant(greeting = true) },
            containerColor = YaarGreen,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(20.dp)
              .testTag("floating_ai_assistant_fab")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Voice AI",
              modifier = Modifier.size(24.dp)
            )
          }
        }

        NavigationTab.CHATS -> {
          ChatsScreen(
            friends = filteredFriends,
            chatMessages = uiState.chatMessages,
            onChatClick = { viewModel.openChat(it) },
            onNewChatClick = { viewModel.openAddFriendDialog() }
          )
        }

        NavigationTab.YAARTOK -> {
          YaarTokScreen(
            videos = uiState.videos,
            commentsMap = uiState.videoComments,
            friendsList = uiState.friends,
            onToggleLike = { viewModel.toggleLikeVideo(it) },
            onToggleFollow = { viewModel.toggleFollowCreator(it) },
            onAddComment = { videoId, comment -> viewModel.addVideoComment(videoId, comment) }
          )
        }

        NavigationTab.CALLS -> {
          CallsHistoryScreen(
            callHistory = uiState.callHistory,
            onStartCall = { friend, type -> viewModel.startCall(friend, type) }
          )
        }

        NavigationTab.PROFILE -> {
          ProfileScreen(
            myName = uiState.myName,
            myBio = uiState.myBio,
            myPhone = uiState.myPhone,
            isOnline = uiState.isMyOnline,
            soundEnabled = uiState.soundEnabled,
            onToggleOnline = { viewModel.toggleMyOnlineStatus() },
            onToggleSound = { viewModel.toggleSound(it) },
            onTestIncomingCall = { viewModel.triggerSimulatedIncomingCall() }
          )
        }
      }
    }
  }

  // Voice AI Assistant BottomSheet
  AiAssistantSheet(
    isOpen = uiState.isAiSheetOpen,
    aiResponse = uiState.aiResponse,
    isListening = uiState.isAiListening,
    isThinking = uiState.isAiThinking,
    onDismiss = { viewModel.closeAiAssistant() },
    onStartListening = { viewModel.startAiVoiceListening() },
    onStopListening = { viewModel.stopAiVoiceListening() },
    onSubmitCommand = { viewModel.submitAiCommand(it) },
    onSpeakAgain = { viewModel.speakCurrentAiResponse() }
  )

  // Dialogs
  if (uiState.showAddFriendDialog) {
    AddFriendDialog(
      onDismiss = { viewModel.closeAddFriendDialog() },
      onAddFriend = { name, phone, status ->
        viewModel.addFriend(name, phone, status)
        friendListViewModel.addFriend(name, phone, status)
      }
    )
  }

  if (uiState.showAddStoryDialog) {
    AddStoryDialog(
      onDismiss = { viewModel.closeAddStoryDialog() },
      onAddStory = { text, c1, c2 ->
        viewModel.addStory(text, c1, c2)
      }
    )
  }
}
