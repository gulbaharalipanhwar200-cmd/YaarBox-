package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun AddStoryDialog(
  onDismiss: () -> Unit,
  onAddStory: (text: String, startColor: Long, endColor: Long) -> Unit
) {
  var text by remember { mutableStateOf("") }
  val gradientPresets = listOf(
    Pair(0xFF0D6352, 0xFF10B981), // Emerald Teal
    Pair(0xFF7C3AED, 0xFFEC4899), // Purple Pink
    Pair(0xFF2563EB, 0xFF06B6D4), // Blue Cyan
    Pair(0xFFDC2626, 0xFFF97316), // Red Orange
    Pair(0xFFD97706, 0xFFEAB308)  // Amber Yellow
  )
  var selectedPreset by remember { mutableStateOf(0) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = YaarDarkCard,
    title = {
      Text(
        text = "Add to Your Story (اسٹیٹس لگائیں)",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = YaarTextPrimary,
          fontSize = 18.sp
        )
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = text,
          onValueChange = { text = it },
          label = { Text("What's on your mind?") },
          placeholder = { Text("مثال: یا تو جیتو گے یا سیکھو گے! 🔥") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_story_text_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = YaarGreen,
            unfocusedBorderColor = YaarDarkBorder,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Choose Story Theme Color",
          style = MaterialTheme.typography.bodySmall.copy(
            color = YaarTextSecondary,
            fontWeight = FontWeight.Medium
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          gradientPresets.forEachIndexed { index, pair ->
            val isSelected = index == selectedPreset
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(pair.first))
                .border(
                  width = if (isSelected) 3.dp else 1.dp,
                  color = if (isSelected) YaarGreen else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedPreset = index }
            )
          }
        }
      }
    },
    confirmButton = {
      ElevatedButton(
        onClick = {
          if (text.isNotBlank()) {
            val (c1, c2) = gradientPresets[selectedPreset]
            onAddStory(text, c1, c2)
          }
        },
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = YaarGreen,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("confirm_add_story_button")
      ) {
        Text("Share Story", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_add_story_button")
      ) {
        Text("Cancel", color = YaarTextSecondary)
      }
    }
  )
}
