package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Badge
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.Friend
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarCyan
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextMuted
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun ChatsScreen(
  friends: List<Friend>,
  chatMessages: Map<String, List<ChatMessage>>,
  onChatClick: (Friend) -> Unit,
  onNewChatClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(YaarDarkBg)
      .testTag("chats_screen")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(vertical = 8.dp)
    ) {
      items(friends, key = { it.id }) { friend ->
        val messages = chatMessages[friend.id] ?: emptyList()
        val lastMessage = messages.lastOrNull()

        ChatConversationRow(
          friend = friend,
          lastMessage = lastMessage,
          onClick = { onChatClick(friend) }
        )
      }
    }

    // Floating Action Button to start new chat
    FloatingActionButton(
      onClick = onNewChatClick,
      containerColor = YaarGreen,
      contentColor = Color.Black,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("new_chat_fab")
    ) {
      Icon(
        imageVector = Icons.Default.AddComment,
        contentDescription = "New Chat",
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun ChatConversationRow(
  friend: Friend,
  lastMessage: ChatMessage?,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("chat_conversation_${friend.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    YaarAvatar(
      name = friend.name,
      avatarRes = friend.avatarRes,
      avatarUrl = friend.avatarUrl,
      size = 54.dp,
      showOnlineDot = true,
      isOnline = friend.isOnline
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = friend.name,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = YaarTextPrimary,
            fontSize = 16.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = lastMessage?.timestamp ?: friend.lastSeen,
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (friend.unreadCount > 0) YaarGreen else YaarTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (friend.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
          )
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (lastMessage?.isMe == true) {
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Sent",
              tint = YaarCyan,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
          }

          Text(
            text = lastMessage?.text ?: friend.statusText,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = if (friend.unreadCount > 0) YaarTextPrimary else YaarTextSecondary,
              fontWeight = if (friend.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
              fontSize = 14.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (friend.unreadCount > 0) {
          Spacer(modifier = Modifier.width(8.dp))
          Badge(
            containerColor = YaarGreen,
            contentColor = Color.Black
          ) {
            Text(
              text = friend.unreadCount.toString(),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 4.dp)
            )
          }
        }
      }
    }
  }
}
