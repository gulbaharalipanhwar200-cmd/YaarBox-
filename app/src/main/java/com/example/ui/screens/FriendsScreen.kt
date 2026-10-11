package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CallType
import com.example.data.Friend
import com.example.data.Story
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarGreenBright
import com.example.ui.theme.YaarOnlineGreen
import com.example.ui.theme.YaarTextMuted
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun FriendsScreen(
  friends: List<Friend>,
  stories: List<Story>,
  onFriendClick: (Friend) -> Unit,
  onCallClick: (Friend, CallType) -> Unit,
  onStoryClick: (Story) -> Unit,
  onAddStoryClick: () -> Unit,
  onAddFriendClick: () -> Unit,
  modifier: Modifier = Modifier,
  onToggleOnlineStatus: ((String) -> Unit)? = null,
  filterOnlyOnline: Boolean = false,
  onToggleFilterOnline: (() -> Unit)? = null
) {
  val onlineCount = friends.count { it.isOnline }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(YaarDarkBg)
      .testTag("friends_screen"),
    contentPadding = PaddingValues(bottom = 16.dp)
  ) {
    // 1. Stories Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Stories",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = YaarTextPrimary,
            fontSize = 17.sp
          )
        )
        Text(
          text = "See all",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = YaarGreen,
            fontSize = 14.sp
          ),
          modifier = Modifier
            .clickable { onAddStoryClick() }
            .testTag("see_all_stories")
        )
      }
    }

    // 2. Stories Carousel
    item {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // "Your Story" with Add badge
        item {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable { onAddStoryClick() }
              .testTag("add_story_item")
          ) {
            Box(
              modifier = Modifier.size(68.dp),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(62.dp)
                  .clip(CircleShape)
                  .background(YaarDarkCard)
                  .border(2.dp, YaarDarkBorder, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Your Story",
                  tint = YaarTextSecondary,
                  modifier = Modifier.size(28.dp)
                )
              }
              // Green "+" badge
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .offset(x = (-2).dp, y = (-2).dp)
                  .size(22.dp)
                  .clip(CircleShape)
                  .background(YaarGreen)
                  .border(2.dp, YaarDarkBg, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Add",
                  tint = Color.Black,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Your Story",
              style = MaterialTheme.typography.labelSmall.copy(
                color = YaarTextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              )
            )
          }
        }

        // Friends Stories
        items(stories, key = { it.id }) { story ->
          val storyGradient = listOf(
            Color(story.gradientStart),
            Color(story.gradientEnd)
          )

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable { onStoryClick(story) }
              .testTag("story_item_${story.id}")
          ) {
            YaarAvatar(
              name = story.friendName,
              avatarUrl = story.avatarUrl,
              avatarRes = story.avatarRes,
              size = 62.dp,
              storyBorderGradient = if (!story.isViewed) storyGradient else null
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = story.friendName,
              style = MaterialTheme.typography.labelSmall.copy(
                color = YaarTextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    // Divider between stories and friends list
    item {
      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(
        color = YaarDarkBorder.copy(alpha = 0.6f),
        thickness = 0.8.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // 3. Friends Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .then(
              if (onToggleFilterOnline != null) Modifier.clickable { onToggleFilterOnline() }
              else Modifier
            )
            .testTag("filter_online_toggle")
        ) {
          Text(
            text = "Friends — $onlineCount Online",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary,
              fontSize = 16.sp
            )
          )

          if (filterOnlyOnline) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(YaarGreen.copy(alpha = 0.2f))
                .border(1.dp, YaarGreen, RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Online Only",
                color = YaarGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        IconButton(
          onClick = onAddFriendClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("add_friend_header_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.PersonAdd,
            contentDescription = "Add Friend",
            tint = YaarGreen,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }

    // 4. Friends List Rows
    items(friends, key = { it.id }) { friend ->
      FriendRowItem(
        friend = friend,
        onChatClick = { onFriendClick(friend) },
        onCallClick = { onCallClick(friend, CallType.VOICE) },
        onToggleOnline = { onToggleOnlineStatus?.invoke(friend.id) }
      )
    }
  }
}

@Composable
private fun FriendRowItem(
  friend: Friend,
  onChatClick: () -> Unit,
  onCallClick: () -> Unit,
  onToggleOnline: () -> Unit = {}
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onChatClick() }
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .testTag("friend_row_${friend.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Avatar with green online dot (tap toggles online status)
    YaarAvatar(
      name = friend.name,
      avatarRes = friend.avatarRes,
      avatarUrl = friend.avatarUrl,
      size = 54.dp,
      showOnlineDot = true,
      isOnline = friend.isOnline,
      onClick = onToggleOnline
    )

    Spacer(modifier = Modifier.width(14.dp))

    // Name and Status
    Column(
      modifier = Modifier.weight(1f)
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

      Spacer(modifier = Modifier.height(2.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        val statusColor = when {
          friend.statusText.contains("Spotify") -> YaarGreenBright
          friend.isOnline -> YaarOnlineGreen
          else -> YaarTextSecondary
        }

        Text(
          text = friend.statusText,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = statusColor,
            fontSize = 13.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // Actions: Call button & Chat bubble button
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = onCallClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("call_friend_${friend.id}")
      ) {
        Icon(
          imageVector = Icons.Outlined.Phone,
          contentDescription = "Call ${friend.name}",
          tint = YaarGreen,
          modifier = Modifier.size(22.dp)
        )
      }

      IconButton(
        onClick = onChatClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("chat_friend_${friend.id}")
      ) {
        Icon(
          imageVector = Icons.Outlined.ChatBubbleOutline,
          contentDescription = "Chat with ${friend.name}",
          tint = YaarTextSecondary,
          modifier = Modifier.size(22.dp)
        )
      }
    }
  }
}
