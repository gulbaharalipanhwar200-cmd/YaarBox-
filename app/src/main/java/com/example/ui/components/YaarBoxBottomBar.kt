package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NavigationTab
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextMuted
import com.example.ui.theme.YaarTextSecondary

@Composable
fun YaarBoxBottomBar(
  currentTab: NavigationTab,
  onTabSelected: (NavigationTab) -> Unit,
  unreadChatsCount: Int = 0,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(YaarDarkBg)
      .navigationBarsPadding()
  ) {
    HorizontalDivider(color = YaarDarkBorder.copy(alpha = 0.5f), thickness = 0.8.dp)

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NavigationTab.values().forEach { tab ->
        val isSelected = tab == currentTab
        val (icon, selectedIcon) = when (tab) {
          NavigationTab.CHATS -> Pair(Icons.Outlined.ChatBubbleOutline, Icons.Filled.ChatBubble)
          NavigationTab.FRIENDS -> Pair(Icons.Outlined.PeopleOutline, Icons.Filled.PeopleAlt)
          NavigationTab.CALLS -> Pair(Icons.Outlined.Call, Icons.Filled.Call)
          NavigationTab.PROFILE -> Pair(Icons.Outlined.PersonOutline, Icons.Filled.Person)
        }

        val itemColor = if (isSelected) YaarGreen else YaarTextSecondary

        Column(
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = false, radius = 28.dp),
              onClick = { onTabSelected(tab) }
            )
            .testTag("nav_tab_${tab.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          if (tab == NavigationTab.CHATS && unreadChatsCount > 0) {
            BadgedBox(
              badge = {
                Badge(
                  containerColor = YaarGreen,
                  contentColor = Color.Black
                ) {
                  Text(
                    text = unreadChatsCount.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            ) {
              Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = tab.labelEn,
                tint = itemColor,
                modifier = Modifier.size(24.dp)
              )
            }
          } else {
            Icon(
              imageVector = if (isSelected) selectedIcon else icon,
              contentDescription = tab.labelEn,
              tint = itemColor,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = tab.labelEn,
            style = MaterialTheme.typography.labelSmall.copy(
              color = itemColor,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
              fontSize = 11.sp
            )
          )
        }
      }
    }
  }
}
