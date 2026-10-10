package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Story
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun StoryViewerScreen(
  story: Story,
  onClose: () -> Unit,
  onSendReply: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onClose() }

  val progress = remember { Animatable(0f) }
  var replyText by remember { mutableStateOf("") }
  var isLiked by remember { mutableStateOf(false) }

  // Auto-progress bar 5 seconds
  LaunchedEffect(story.id) {
    progress.snapTo(0f)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
    )
    onClose()
  }

  val storyBg = Brush.verticalGradient(
    colors = listOf(
      Color(story.gradientStart),
      Color(story.gradientEnd).copy(alpha = 0.8f),
      Color(0xFF0A0E13)
    )
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(storyBg)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .testTag("story_viewer_screen")
  ) {
    // Top Bar: Progress Indicator & User info
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Progress line
      LinearProgressIndicator(
        progress = { progress.value },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.5.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = Color.White,
        trackColor = Color.White.copy(alpha = 0.3f)
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        YaarAvatar(
          name = story.friendName,
          avatarUrl = story.avatarUrl,
          avatarRes = story.avatarRes,
          size = 40.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = story.friendName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White,
              fontSize = 15.sp
            )
          )
          Text(
            text = story.timeAgo,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color.White.copy(alpha = 0.75f),
              fontSize = 12.sp
            )
          )
        }

        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("close_story_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White
          )
        }
      }
    }

    // Story Center Content
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp, vertical = 80.dp)
        .pointerInput(Unit) {
          detectTapGestures(
            onTap = { offset ->
              if (offset.x < size.width * 0.35f) {
                // Tap left: close or reset
                onClose()
              } else {
                // Tap right: finish
                onClose()
              }
            }
          )
        },
      contentAlignment = Alignment.Center
    ) {
      Surface(
        color = Color.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(24.dp)
      ) {
        Text(
          text = story.contentText,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 22.sp,
            lineHeight = 32.sp
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(28.dp)
        )
      }
    }

    // Bottom Reply Bar with Reactions
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Quick reaction chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        listOf("❤️", "🔥", "😂", "👏", "😍").forEach { emoji ->
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .clickable {
                onSendReply(emoji)
                onClose()
              },
            contentAlignment = Alignment.Center
          ) {
            Text(text = emoji, fontSize = 20.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextField(
          value = replyText,
          onValueChange = { replyText = it },
          placeholder = { Text("Reply to ${story.friendName}...", color = Color.White.copy(alpha = 0.6f)) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.15f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("story_reply_input")
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Heart like button
        IconButton(
          onClick = {
            isLiked = !isLiked
            if (isLiked) onSendReply("❤️")
          },
          modifier = Modifier.testTag("story_like_button")
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Like",
            tint = if (isLiked) Color.Red else Color.White
          )
        }

        if (replyText.isNotBlank()) {
          IconButton(
            onClick = {
              onSendReply(replyText)
              replyText = ""
              onClose()
            },
            modifier = Modifier.testTag("story_send_reply")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = YaarGreen
            )
          }
        }
      }
    }
  }
}
