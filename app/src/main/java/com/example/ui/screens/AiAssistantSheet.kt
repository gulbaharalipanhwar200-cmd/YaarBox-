package com.example.ui.screens

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.ui.theme.YaarCyan
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextMuted
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiAssistantSheet(
  isOpen: Boolean,
  aiResponse: String,
  isListening: Boolean,
  isThinking: Boolean,
  onDismiss: () -> Unit,
  onStartListening: () -> Unit,
  onStopListening: () -> Unit,
  onSubmitCommand: (String) -> Unit,
  onSpeakAgain: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (!isOpen) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var textInput by remember { mutableStateOf("") }

  // Pulsing animation for AI Orb
  val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
  val orbPulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "orbPulse"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = YaarDarkBg,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = modifier.imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header: Title and Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  colors = listOf(YaarGreen, YaarCyan)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "YaarBox Voice AI (وائس اسسٹنٹ)",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary,
              fontSize = 17.sp
            )
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_ai_sheet")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = YaarTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Glowing Center AI Orb
      Box(
        modifier = Modifier
          .size(110.dp)
          .scale(if (isListening || isThinking) orbPulse else 1f)
          .clickable {
            if (isListening) onStopListening() else onStartListening()
          }
          .testTag("ai_orb_button"),
        contentAlignment = Alignment.Center
      ) {
        // Outer glowing waves
        Box(
          modifier = Modifier
            .size(110.dp)
            .border(
              width = 2.dp,
              brush = Brush.sweepGradient(
                listOf(YaarGreen, YaarCyan, Color(0xFF8B5CF6), YaarGreen)
              ),
              shape = CircleShape
            )
        )

        // Center sphere
        Box(
          modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  YaarGreen,
                  YaarCyan.copy(alpha = 0.8f),
                  Color(0xFF0D6352)
                )
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isThinking) {
            CircularProgressIndicator(
              color = Color.Black,
              strokeWidth = 3.dp,
              modifier = Modifier.size(36.dp)
            )
          } else {
            Icon(
              imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
              contentDescription = "Mic",
              tint = Color.Black,
              modifier = Modifier.size(38.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status chip
      Surface(
        color = YaarDarkCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarDarkBorder)
      ) {
        val statusLabel = when {
          isThinking -> "⚡ سوچ رہا ہوں..."
          isListening -> "🔴 سن رہا ہوں... (حکم بولیں)"
          else -> "🎙️ مائیک دبائیں یا نیچے سے چنیں"
        }
        Text(
          text = statusLabel,
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (isListening) YaarGreen else YaarTextSecondary,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
          ),
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // AI Response Card
      Surface(
        color = YaarDarkCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarGreen.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = YaarGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Yaar AI",
                color = YaarGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }

            IconButton(
              onClick = onSpeakAgain,
              modifier = Modifier.size(28.dp).testTag("speak_again_button")
            ) {
              Icon(Icons.Default.VolumeUp, contentDescription = "Listen Again", tint = YaarCyan, modifier = Modifier.size(18.dp))
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = aiResponse,
            style = MaterialTheme.typography.bodyLarge.copy(
              color = YaarTextPrimary,
              fontSize = 15.sp,
              lineHeight = 22.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Preset Voice Command Chips
      Text(
        text = "فوری احکامات (Quick Voice Commands):",
        style = MaterialTheme.typography.labelSmall.copy(
          color = YaarTextSecondary,
          fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(8.dp))

      val presetCommands = listOf(
        "📞 علی کو کال لگاؤ",
        "📞 سارہ کو کال کرو",
        "💬 سارہ کو میسج کرو میں آ رہا ہوں",
        "🌐 اردو میں ترجمہ کرو",
        "😂 ایک لطیفہ سناؤ",
        "🚀 اسٹوریز کھولو"
      )

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        presetCommands.forEach { cmd ->
          Surface(
            color = YaarDarkCard,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, YaarDarkBorder),
            modifier = Modifier
              .clickable { onSubmitCommand(cmd) }
              .testTag("preset_cmd_${cmd.take(5)}")
          ) {
            Text(
              text = cmd,
              color = YaarTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bottom Command Input Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(26.dp))
          .background(YaarDarkCard)
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextField(
          value = textInput,
          onValueChange = { textInput = it },
          placeholder = { Text("حکم لکھیں یا بولیں...", color = YaarTextMuted, fontSize = 14.sp) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("ai_text_input")
        )

        IconButton(
          onClick = {
            if (isListening) onStopListening() else onStartListening()
          },
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = "Voice Input",
            tint = if (isListening) YaarGreen else YaarCyan,
            modifier = Modifier.size(22.dp)
          )
        }

        IconButton(
          onClick = {
            if (textInput.isNotBlank()) {
              onSubmitCommand(textInput)
              textInput = ""
            }
          },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (textInput.isNotBlank()) YaarGreen else Color.Transparent)
            .testTag("ai_submit_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Submit",
            tint = if (textInput.isNotBlank()) Color.Black else YaarTextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
