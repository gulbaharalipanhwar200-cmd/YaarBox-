package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarOnlineGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun ProfileScreen(
  myName: String,
  myBio: String,
  myPhone: String,
  isOnline: Boolean,
  soundEnabled: Boolean,
  onToggleOnline: () -> Unit,
  onToggleSound: (Boolean) -> Unit,
  onTestIncomingCall: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(YaarDarkBg)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. User Profile Card
    item {
      Surface(
        color = YaarDarkCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarDarkBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          YaarAvatar(
            name = myName,
            size = 80.dp,
            showOnlineDot = true,
            isOnline = isOnline
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = myName,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary,
              fontSize = 20.sp
            )
          )

          Text(
            text = myPhone,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = YaarGreen,
              fontWeight = FontWeight.Medium
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = myBio,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = YaarTextSecondary
            )
          )
        }
      }
    }

    // 2. Interactive Feature Tester Section
    item {
      Surface(
        color = YaarDarkCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarDarkBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Experience YaarBox Features",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "آنے والی کال اور آوازیں ٹیسٹ کریں (Sara Khan سے کال)",
            style = MaterialTheme.typography.bodySmall.copy(
              color = YaarTextSecondary
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          ElevatedButton(
            onClick = onTestIncomingCall,
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = YaarGreen,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("test_incoming_call_button")
          ) {
            Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Test Incoming Call (سارہ خان کی کال)",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 3. Settings Toggles
    item {
      Surface(
        color = YaarDarkCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarDarkBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Settings & Preferences",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Online Status
          SettingsRow(
            icon = Icons.Outlined.CheckCircle,
            title = "Online Status",
            subtitle = if (isOnline) "Visible as Online to friends" else "Appear Offline",
            trailing = {
              Switch(
                checked = isOnline,
                onCheckedChange = { onToggleOnline() },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.Black,
                  checkedTrackColor = YaarGreen
                )
              )
            }
          )

          HorizontalDivider(color = YaarDarkBorder, modifier = Modifier.padding(vertical = 10.dp))

          // Sound Effects
          SettingsRow(
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            title = "Sound Effects",
            subtitle = "Open chime, message pop & call rings",
            trailing = {
              Switch(
                checked = soundEnabled,
                onCheckedChange = { onToggleSound(it) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.Black,
                  checkedTrackColor = YaarGreen
                )
              )
            }
          )
        }
      }
    }

    // 4. About YaarBox
    item {
      Surface(
        color = YaarDarkCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarDarkBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = YaarGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "End-to-End Encrypted",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = YaarTextPrimary
              )
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "YaarBox protects your conversations and calls with end-to-end encryption. Your yaari stays private and secure.",
            style = MaterialTheme.typography.bodySmall.copy(color = YaarTextSecondary)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "YaarBox v1.0.0 • Made with ❤️ for friends",
            style = MaterialTheme.typography.labelSmall.copy(color = YaarTextSecondary.copy(alpha = 0.7f))
          )
        }
      }
    }
  }
}

@Composable
private fun SettingsRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  trailing: @Composable () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(YaarGreen.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = YaarGreen, modifier = Modifier.size(20.dp))
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.SemiBold,
          color = YaarTextPrimary
        )
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = YaarTextSecondary
        )
      )
    }

    trailing()
  }
}
