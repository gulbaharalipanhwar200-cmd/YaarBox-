package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CallType
import com.example.data.ChatMessage
import com.example.data.Friend
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarChatIncoming
import com.example.ui.theme.YaarChatOutgoing
import com.example.ui.theme.YaarCyan
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarOnlineGreen
import com.example.ui.theme.YaarTextMuted
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun ChatDetailScreen(
  friend: Friend,
  messages: List<ChatMessage>,
  onBackClick: () -> Unit,
  onSendMessage: (String) -> Unit,
  onVoiceCallClick: () -> Unit,
  onVideoCallClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  var inputText by remember { mutableStateOf("") }
  var showMenu by remember { mutableStateOf(false) }
  var showAttachmentSheet by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  // Auto-scroll when new messages arrive
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(YaarDarkBg)
      .imePadding()
      .testTag("chat_detail_screen")
  ) {
    // 1. Top Bar matching Image 1
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(YaarDarkBg)
        .statusBarsPadding()
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("chat_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = YaarTextPrimary
          )
        }

        // YaarBox logo + branding
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_yaarbox_logo),
            contentDescription = "YaarBox",
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(6.dp))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "YaarBox",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary,
              fontSize = 20.sp
            )
          )
        }

        // Call Action
        IconButton(
          onClick = onVoiceCallClick,
          modifier = Modifier.testTag("chat_voice_call_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Call,
            contentDescription = "Voice Call",
            tint = YaarGreen,
            modifier = Modifier.size(24.dp)
          )
        }

        // Video Call Action
        IconButton(
          onClick = onVideoCallClick,
          modifier = Modifier.testTag("chat_video_call_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Videocam,
            contentDescription = "Video Call",
            tint = YaarGreen,
            modifier = Modifier.size(26.dp)
          )
        }

        // More options
        Box {
          IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.testTag("chat_menu_button")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = YaarTextSecondary
            )
          }

          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("View Profile") },
              onClick = { showMenu = false }
            )
            DropdownMenuItem(
              text = { Text("Mute Notifications") },
              onClick = { showMenu = false }
            )
            DropdownMenuItem(
              text = { Text("Clear Chat") },
              onClick = { showMenu = false }
            )
          }
        }
      }
    }

    // 2. Friend Header: Avatar, Name, Online status (matches Image 1)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      YaarAvatar(
        name = friend.name,
        avatarRes = friend.avatarRes,
        avatarUrl = friend.avatarUrl,
        size = 46.dp,
        showOnlineDot = true,
        isOnline = friend.isOnline
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = friend.name,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = YaarTextPrimary,
            fontSize = 18.sp
          )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Online",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = YaarGreen,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• Active now",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = YaarTextSecondary,
              fontSize = 13.sp
            )
          )
        }
      }
    }

    // 3. Message Thread
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      state = listState,
      contentPadding = PaddingValues(vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Date Pill "Today, 8:32 PM"
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Surface(
            color = YaarDarkCard.copy(alpha = 0.8f),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text(
              text = "Today, 8:32 PM",
              color = YaarTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Messages
      items(messages, key = { it.id }) { msg ->
        MessageBubble(message = msg)
      }
    }

    // 4. Bottom Message Bar (Matching Image 1 Left)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Rounded input bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(32.dp))
          .background(YaarDarkCard)
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Plus button
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { showAttachmentSheet = !showAttachmentSheet }
            .testTag("attachment_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Attach",
            tint = YaarTextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Message text input
        TextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = {
            Text(
              text = "Message...",
              color = YaarTextMuted,
              fontSize = 15.sp
            )
          },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          maxLines = 4,
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field")
        )

        // Mic audio note
        IconButton(
          onClick = {
            onSendMessage("🎤 [Voice Note 0:14]")
          },
          modifier = Modifier
            .size(36.dp)
            .testTag("voice_note_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Mic,
            contentDescription = "Record Voice",
            tint = YaarCyan,
            modifier = Modifier.size(22.dp)
          )
        }

        // Image / Gallery
        IconButton(
          onClick = {
            onSendMessage("📸 [Photo shared from gallery]")
          },
          modifier = Modifier
            .size(36.dp)
            .testTag("gallery_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = "Send Photo",
            tint = YaarCyan,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(2.dp))

        // Send Button (plane icon)
        IconButton(
          onClick = {
            if (inputText.isNotBlank()) {
              onSendMessage(inputText)
              inputText = ""
            }
          },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (inputText.isNotBlank()) YaarGreen else Color.Transparent)
            .testTag("send_message_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = if (inputText.isNotBlank()) Color.Black else YaarGreen,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
  val isMe = message.isMe
  val bubbleColor = if (isMe) YaarChatOutgoing else YaarChatIncoming
  val alignment = if (isMe) Alignment.End else Alignment.Start

  val bubbleShape = if (isMe) {
    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
  } else {
    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = alignment
  ) {
    Surface(
      color = bubbleColor,
      shape = bubbleShape,
      modifier = Modifier
        .widthIn(max = 280.dp)
        .testTag("message_bubble_${message.id}")
    ) {
      Column(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Text(
          text = message.text,
          style = MaterialTheme.typography.bodyLarge.copy(
            color = YaarTextPrimary,
            fontSize = 15.sp,
            lineHeight = 21.sp
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = message.timestamp,
            style = MaterialTheme.typography.labelSmall.copy(
              color = YaarTextSecondary.copy(alpha = 0.85f),
              fontSize = 11.sp
            )
          )

          if (isMe) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Read",
              tint = YaarCyan,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }
    }
  }
}
