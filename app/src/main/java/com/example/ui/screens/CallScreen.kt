package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActiveCall
import com.example.data.CallState
import com.example.data.CallType
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarCallGreen
import com.example.ui.theme.YaarCallRed
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary
import java.util.Locale

@Composable
fun CallScreen(
  activeCall: ActiveCall,
  onAccept: () -> Unit,
  onDecline: () -> Unit,
  onEndCall: () -> Unit,
  onToggleMute: () -> Unit,
  onToggleSpeaker: () -> Unit,
  onToggleVideo: () -> Unit,
  onFlipCamera: () -> Unit,
  onQuickReply: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    if (activeCall.state == CallState.CONNECTED) {
      onEndCall()
    } else {
      onDecline()
    }
  }

  var showQuickReplies by remember { mutableStateOf(false) }

  // Pulsing ring animation for avatar while ringing
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  val durationText = remember(activeCall.durationSeconds) {
    val mins = activeCall.durationSeconds / 60
    val secs = activeCall.durationSeconds % 60
    String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
  }

  val isVideoCall = activeCall.callType == CallType.VIDEO || activeCall.isVideoEnabled

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0F151C),
            Color(0xFF0A0D12),
            Color(0xFF05070A)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("call_screen")
  ) {
    // Top status indicator
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      val callStatusTitle = when (activeCall.state) {
        CallState.INCOMING -> if (isVideoCall) "Incoming Video Call..." else "Incoming Call..."
        CallState.OUTGOING -> if (isVideoCall) "Calling (Video)..." else "Calling..."
        CallState.CONNECTED -> "YaarBox Call • $durationText"
        CallState.ENDED -> "Call Ended"
      }

      Text(
        text = callStatusTitle,
        style = MaterialTheme.typography.bodyLarge.copy(
          color = YaarTextSecondary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium
        )
      )
    }

    // Center Avatar & Friend details (matches Image 1 Right)
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(bottom = 60.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Big Circular Avatar
      Box(
        modifier = Modifier
          .size(175.dp)
          .scale(if (activeCall.state != CallState.CONNECTED) pulseScale else 1f),
        contentAlignment = Alignment.Center
      ) {
        // Glowing halo
        Box(
          modifier = Modifier
            .size(175.dp)
            .border(
              width = 3.dp,
              brush = Brush.radialGradient(
                colors = listOf(
                  YaarGreen.copy(alpha = 0.5f),
                  Color.Transparent
                )
              ),
              shape = CircleShape
            )
        )

        YaarAvatar(
          name = activeCall.friend.name,
          avatarRes = activeCall.friend.avatarRes,
          avatarUrl = activeCall.friend.avatarUrl,
          size = 160.dp
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Friend Name (bold 32sp)
      Text(
        text = activeCall.friend.name,
        style = MaterialTheme.typography.displayLarge.copy(
          fontWeight = FontWeight.Bold,
          color = YaarTextPrimary,
          fontSize = 32.sp
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Subtitle: "Mobile • YaarBox"
      Text(
        text = "Mobile • YaarBox",
        style = MaterialTheme.typography.bodyLarge.copy(
          color = YaarTextSecondary,
          fontSize = 15.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Ringing or connection status in Green
      val statusSubtext = when (activeCall.state) {
        CallState.INCOMING -> "Dialing via YaarBox..."
        CallState.OUTGOING -> "Ringing..."
        CallState.CONNECTED -> "Encrypted Voice Connection 🔒"
        CallState.ENDED -> "Call Terminated"
      }

      Text(
        text = statusSubtext,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = YaarGreen,
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp
        )
      )
    }

    // Bottom Action Controls
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (activeCall.state == CallState.INCOMING) {
        // INCOMING CALL CONTROLS: Red Decline & Green Accept (Image 1 Right)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Decline Button
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(YaarCallRed)
                .clickable { onDecline() }
                .testTag("decline_call_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = "Decline Call",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Decline",
              color = YaarTextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Accept Button
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(YaarCallGreen)
                .clickable { onAccept() }
                .testTag("accept_call_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Accept Call",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Accept",
              color = YaarTextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // "Swipe up for more options" / Quick message pill
        Surface(
          color = Color.White.copy(alpha = 0.08f),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .clickable { showQuickReplies = !showQuickReplies }
            .testTag("quick_reply_toggle")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Chat,
              contentDescription = "Reply",
              tint = YaarTextSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Quick reply with message",
              color = YaarTextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Normal
            )
          }
        }

        if (showQuickReplies) {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              color = YaarDarkCard,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1f)
                .clickable {
                  onQuickReply("ابھی مصروف ہوں، تھوڑی دیر میں کال کرتا ہوں۔")
                }
            ) {
              Text(
                text = "ابھی مصروف ہوں...",
                color = YaarTextPrimary,
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
              )
            }
            Surface(
              color = YaarDarkCard,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1f)
                .clickable {
                  onQuickReply("Can't talk now. What's up?")
                }
            ) {
              Text(
                text = "Can't talk now",
                color = YaarTextPrimary,
                fontSize = 12.sp,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
              )
            }
          }
        }
      } else {
        // CONNECTED / OUTGOING CALL CONTROLS
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mute toggle
          CallControlButton(
            icon = if (activeCall.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
            label = if (activeCall.isMuted) "Unmute" else "Mute",
            isActive = activeCall.isMuted,
            onClick = onToggleMute,
            tag = "call_mute_button"
          )

          // Speaker toggle
          CallControlButton(
            icon = if (activeCall.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
            label = "Speaker",
            isActive = activeCall.isSpeakerOn,
            onClick = onToggleSpeaker,
            tag = "call_speaker_button"
          )

          // Video toggle
          CallControlButton(
            icon = if (activeCall.isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
            label = "Video",
            isActive = activeCall.isVideoEnabled,
            onClick = onToggleVideo,
            tag = "call_video_button"
          )

          // Flip camera
          CallControlButton(
            icon = Icons.Default.Cameraswitch,
            label = "Flip",
            isActive = false,
            onClick = onFlipCamera,
            tag = "call_flip_button"
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Red End Call Button
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(YaarCallRed)
            .clickable { onEndCall() }
            .testTag("end_call_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CallEnd,
            contentDescription = "End Call",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun CallControlButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isActive: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }.testTag(tag)
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(CircleShape)
        .background(if (isActive) YaarGreen else Color.White.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isActive) Color.Black else YaarTextPrimary,
        modifier = Modifier.size(24.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      color = YaarTextSecondary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
