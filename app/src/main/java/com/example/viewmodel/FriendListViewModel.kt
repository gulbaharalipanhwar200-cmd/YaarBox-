package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Friend
import com.example.data.SampleData
import com.example.data.YaarBoxSoundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID

data class FriendListUiState(
  val friends: List<Friend> = SampleData.friendsList,
  val searchQuery: String = "",
  val filterOnlyOnline: Boolean = false,
  val isLoading: Boolean = false
)

class FriendListViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(FriendListUiState())
  val uiState: StateFlow<FriendListUiState> = _uiState.asStateFlow()

  // Filtered friends list based on query and online-only toggle
  val filteredFriends: StateFlow<List<Friend>> = _uiState
    .combine(_uiState) { state, _ ->
      var list = state.friends
      if (state.filterOnlyOnline) {
        list = list.filter { it.isOnline }
      }
      if (state.searchQuery.isNotBlank()) {
        val q = state.searchQuery.trim().lowercase()
        list = list.filter { friend ->
          friend.name.lowercase().contains(q) ||
            friend.statusText.lowercase().contains(q) ||
            friend.phone.contains(q)
        }
      }
      list
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = SampleData.friendsList
    )

  // Live count of friends currently online
  val onlineCount: StateFlow<Int> = _uiState
    .combine(_uiState) { state, _ -> state.friends.count { it.isOnline } }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = SampleData.friendsList.count { it.isOnline }
    )

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleFilterOnlyOnline() {
    _uiState.update { it.copy(filterOnlyOnline = !it.filterOnlyOnline) }
  }

  fun setFilterOnlyOnline(onlyOnline: Boolean) {
    _uiState.update { it.copy(filterOnlyOnline = onlyOnline) }
  }

  /**
   * Toggles the online/offline presence for a specific friend.
   */
  fun toggleFriendOnlineStatus(friendId: String) {
    _uiState.update { state ->
      val updated = state.friends.map { friend ->
        if (friend.id == friendId) {
          val newOnline = !friend.isOnline
          val newStatus = if (newOnline) "Active now •" else "Away • just now"
          friend.copy(
            isOnline = newOnline,
            statusText = newStatus,
            lastSeen = if (newOnline) "Active now" else "Just now"
          )
        } else friend
      }
      state.copy(friends = updated)
    }
  }

  /**
   * Explicitly updates the online status and status message of a friend.
   */
  fun setFriendOnlineStatus(friendId: String, isOnline: Boolean, customStatus: String? = null) {
    _uiState.update { state ->
      val updated = state.friends.map { friend ->
        if (friend.id == friendId) {
          friend.copy(
            isOnline = isOnline,
            statusText = customStatus ?: if (isOnline) "Active now •" else "Away",
            lastSeen = if (isOnline) "Active now" else "Recently"
          )
        } else friend
      }
      state.copy(friends = updated)
    }
  }

  /**
   * Updates a friend's status text (e.g., "Playing Spotify • Dil Diyan Gallan").
   */
  fun updateFriendStatusText(friendId: String, newStatus: String) {
    _uiState.update { state ->
      val updated = state.friends.map { friend ->
        if (friend.id == friendId) {
          friend.copy(statusText = newStatus)
        } else friend
      }
      state.copy(friends = updated)
    }
  }

  /**
   * Clears the unread count badge when opening a conversation.
   */
  fun clearUnreadCount(friendId: String) {
    _uiState.update { state ->
      val updated = state.friends.map { friend ->
        if (friend.id == friendId) {
          friend.copy(unreadCount = 0)
        } else friend
      }
      state.copy(friends = updated)
    }
  }

  /**
   * Adds a new friend to the YaarBox friend list.
   */
  fun addFriend(name: String, phone: String, statusText: String, isOnline: Boolean = true) {
    if (name.isBlank()) return
    YaarBoxSoundService.playPopSound()

    val newFriend = Friend(
      id = "friend_${UUID.randomUUID().toString().take(6)}",
      name = name.trim(),
      phone = if (phone.isBlank()) "+92 300 0000000" else phone.trim(),
      statusText = if (statusText.isBlank()) "Online • Active on YaarBox" else statusText.trim(),
      isOnline = isOnline,
      lastSeen = if (isOnline) "Active now" else "Just now"
    )

    _uiState.update { state ->
      state.copy(friends = listOf(newFriend) + state.friends)
    }
  }

  /**
   * Removes a friend by ID.
   */
  fun removeFriend(friendId: String) {
    _uiState.update { state ->
      state.copy(friends = state.friends.filterNot { it.id == friendId })
    }
  }

  /**
   * Resets the friends list to initial sample data.
   */
  fun resetToSampleData() {
    _uiState.update {
      it.copy(
        friends = SampleData.friendsList,
        searchQuery = "",
        filterOnlyOnline = false
      )
    }
  }
}
