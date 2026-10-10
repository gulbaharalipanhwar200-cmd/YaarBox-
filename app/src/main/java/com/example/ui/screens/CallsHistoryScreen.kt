package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CallDirection
import com.example.data.CallHistoryItem
import com.example.data.CallType
import com.example.data.Friend
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarCallGreen
import com.example.ui.theme.YaarCallRed
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun CallsHistoryScreen(
  callHistory: List<CallHistoryItem>,
  onStartCall: (Friend, CallType) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(YaarDarkBg)
      .testTag("calls_history_screen"),
    contentPadding = PaddingValues(vertical = 12.dp)
  ) {
    item {
      Text(
        text = "Recent Calls",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = YaarTextPrimary,
          fontSize = 16.sp
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
      )
    }

    items(callHistory, key = { it.id }) { callItem ->
      CallHistoryRow(
        call = callItem,
        onCallClick = { onStartCall(callItem.friend, callItem.callType) }
      )
    }
  }
}

@Composable
private fun CallHistoryRow(
  call: CallHistoryItem,
  onCallClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCallClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("call_history_item_${call.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    YaarAvatar(
      name = call.friend.name,
      avatarRes = call.friend.avatarRes,
      avatarUrl = call.friend.avatarUrl,
      size = 50.dp
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = call.friend.name,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = if (call.direction == CallDirection.MISSED) YaarCallRed else YaarTextPrimary,
          fontSize = 16.sp
        )
      )

      Spacer(modifier = Modifier.height(2.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        val (directionIcon, tint) = when (call.direction) {
          CallDirection.INCOMING -> Pair(Icons.AutoMirrored.Filled.CallReceived, YaarCallGreen)
          CallDirection.OUTGOING -> Pair(Icons.AutoMirrored.Filled.CallMade, YaarGreen)
          CallDirection.MISSED -> Pair(Icons.AutoMirrored.Filled.CallMissed, YaarCallRed)
        }

        Icon(
          imageVector = directionIcon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
          text = "${call.timestamp} • ${call.duration}",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = YaarTextSecondary,
            fontSize = 13.sp
          )
        )
      }
    }

    IconButton(
      onClick = onCallClick,
      modifier = Modifier.testTag("call_again_${call.id}")
    ) {
      Icon(
        imageVector = if (call.callType == CallType.VIDEO) Icons.Outlined.Videocam else Icons.Outlined.Call,
        contentDescription = "Call ${call.friend.name}",
        tint = YaarGreen,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}
