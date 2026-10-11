package com.example

import com.example.data.CallType
import com.example.data.NavigationTab
import com.example.data.SampleData
import com.example.data.YaarBoxAiEngine
import com.example.data.AiActionResult
import com.example.viewmodel.FriendListViewModel
import com.example.viewmodel.YaarBoxViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testInitialState() {
    val vm = YaarBoxViewModel()
    val state = vm.uiState.value

    assertEquals(NavigationTab.FRIENDS, state.currentTab)
    assertTrue(state.friends.isNotEmpty())
    assertTrue(state.stories.isNotEmpty())
    assertTrue(state.videos.isNotEmpty())
    assertNotNull(state.chatMessages["friend_areeb"])
  }

  @Test
  fun testSendMessage() {
    val vm = YaarBoxViewModel()
    val friendId = "friend_areeb"
    val countBefore = vm.uiState.value.chatMessages[friendId]?.size ?: 0

    vm.sendMessage(friendId, "Hello test from YaarBox")
    val countAfter = vm.uiState.value.chatMessages[friendId]?.size ?: 0
    assertEquals(countBefore + 1, countAfter)
  }

  @Test
  fun testAddFriendAndStory() {
    val vm = YaarBoxViewModel()
    val initialFriendCount = vm.uiState.value.friends.size
    vm.addFriend("Hamza", "+92 300 5551234", "Active on YaarBox")
    assertEquals(initialFriendCount + 1, vm.uiState.value.friends.size)

    val initialStoryCount = vm.uiState.value.stories.size
    vm.addStory("Great day!", 0xFF0D6352, 0xFF10B981)
    assertEquals(initialStoryCount + 1, vm.uiState.value.stories.size)
  }

  @Test
  fun testCallLifecycle() {
    val vm = YaarBoxViewModel()
    vm.startCall(SampleData.saraKhan, CallType.VOICE)
    assertNotNull(vm.uiState.value.activeCall)

    vm.acceptCall()
    assertEquals(com.example.data.CallState.CONNECTED, vm.uiState.value.activeCall?.state)

    vm.endCall()
    assertEquals(null, vm.uiState.value.activeCall)
  }

  @Test
  fun testAiCallIntentParsing() = runBlocking {
    val aiEngine = YaarBoxAiEngine()
    val result = aiEngine.processCommand("علی کو کال لگاؤ", SampleData.friendsList)
    assertTrue(result is AiActionResult.CallAction)
    val callAction = result as AiActionResult.CallAction
    assertTrue(callAction.spokenConfirmation.contains("کال لگا رہا ہوں"))
  }

  @Test
  fun testAiMessageIntentParsing() = runBlocking {
    val aiEngine = YaarBoxAiEngine()
    val result = aiEngine.processCommand("سارہ کو میسج کرو میں آ رہا ہوں", SampleData.friendsList)
    assertTrue(result is AiActionResult.MessageAction)
    val msgAction = result as AiActionResult.MessageAction
    assertTrue(msgAction.spokenConfirmation.contains("میسج بھیج رہا ہوں"))
    assertTrue(msgAction.messageText.contains("میں آ رہا ہوں"))
  }

  @Test
  fun testAiOpenScreenIntent() = runBlocking {
    val aiEngine = YaarBoxAiEngine()
    val result = aiEngine.processCommand("اسٹوریز کھولو", SampleData.friendsList)
    assertTrue(result is AiActionResult.OpenScreenAction)
    val openAction = result as AiActionResult.OpenScreenAction
    assertEquals("stories", openAction.screenName)
  }

  @Test
  fun testAiOpenYaarTokIntent() = runBlocking {
    val aiEngine = YaarBoxAiEngine()
    val result = aiEngine.processCommand("ٹک ٹاک ویڈیوز کھولو", SampleData.friendsList)
    assertTrue(result is AiActionResult.OpenScreenAction)
    val openAction = result as AiActionResult.OpenScreenAction
    assertEquals("yaartok", openAction.screenName)
  }

  @Test
  fun testYaarTokVideoInteractions() {
    val vm = YaarBoxViewModel()
    val videoId = vm.uiState.value.videos.first().id
    val initialLiked = vm.uiState.value.videos.first().isLiked
    val initialLikesCount = vm.uiState.value.videos.first().likesCount

    vm.toggleLikeVideo(videoId)
    val updatedVideo = vm.uiState.value.videos.first { it.id == videoId }
    assertEquals(!initialLiked, updatedVideo.isLiked)
    assertEquals(if (!initialLiked) initialLikesCount + 1 else initialLikesCount - 1, updatedVideo.likesCount)

    // Follow creator
    val initialFollowed = updatedVideo.isFollowed
    vm.toggleFollowCreator(videoId)
    val followedVideo = vm.uiState.value.videos.first { it.id == videoId }
    assertEquals(!initialFollowed, followedVideo.isFollowed)

    // Add comment
    val initialCommentCount = followedVideo.commentsCount
    vm.addVideoComment(videoId, "Super awesome reel on YaarTok! 🔥")
    val commentedVideo = vm.uiState.value.videos.first { it.id == videoId }
    assertEquals(initialCommentCount + 1, commentedVideo.commentsCount)
    assertTrue(vm.uiState.value.videoComments[videoId]?.first()?.text?.contains("Super awesome") == true)
  }

  @Test
  fun testFriendListViewModelOnlineToggle() {
    val vm = FriendListViewModel()
    val friend = vm.uiState.value.friends.first()
    val initialOnline = friend.isOnline

    vm.toggleFriendOnlineStatus(friend.id)
    val updatedFriend = vm.uiState.value.friends.first { it.id == friend.id }
    assertEquals(!initialOnline, updatedFriend.isOnline)

    // Toggle back
    vm.toggleFriendOnlineStatus(friend.id)
    val restoredFriend = vm.uiState.value.friends.first { it.id == friend.id }
    assertEquals(initialOnline, restoredFriend.isOnline)
  }

  @Test
  fun testFriendListViewModelAddAndRemove() {
    val vm = FriendListViewModel()
    val countBefore = vm.uiState.value.friends.size

    vm.addFriend("Test User", "+92 300 1112233", "New user status", isOnline = true)
    assertEquals(countBefore + 1, vm.uiState.value.friends.size)

    val addedFriend = vm.uiState.value.friends.first()
    assertEquals("Test User", addedFriend.name)
    assertTrue(addedFriend.isOnline)

    vm.removeFriend(addedFriend.id)
    assertEquals(countBefore, vm.uiState.value.friends.size)
  }

  @Test
  fun testFriendListViewModelUpdateStatus() {
    val vm = FriendListViewModel()
    val friendId = vm.uiState.value.friends.first().id
    val customStatus = "Playing Spotify • Coke Studio Season 15"

    vm.updateFriendStatusText(friendId, customStatus)
    val updated = vm.uiState.value.friends.first { it.id == friendId }
    assertEquals(customStatus, updated.statusText)
  }
}
