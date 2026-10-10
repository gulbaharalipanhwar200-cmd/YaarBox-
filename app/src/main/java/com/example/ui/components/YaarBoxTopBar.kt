package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.YaarCyan
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary

@Composable
fun YaarBoxTopBar(
  isSearching: Boolean,
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  onToggleSearch: (Boolean) -> Unit,
  onAiClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(YaarDarkBg)
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    if (isSearching) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextField(
          value = searchQuery,
          onValueChange = onSearchQueryChanged,
          placeholder = { Text("Search friends or messages...", color = YaarTextSecondary) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = YaarDarkCard,
            unfocusedContainerColor = YaarDarkCard,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          shape = RoundedCornerShape(24.dp),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("search_input_field"),
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = YaarGreen)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChanged("") }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = YaarTextSecondary)
              }
            }
          }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = { onToggleSearch(false) },
          modifier = Modifier.testTag("close_search_button")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close Search", tint = YaarTextPrimary)
        }
      }
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo + App Name
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_yaarbox_logo),
            contentDescription = "YaarBox Logo",
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "YaarBox",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = YaarTextPrimary,
              fontSize = 24.sp
            )
          )
        }

        // AI Assistant Button (Glowing Pill)
        Surface(
          color = Color(0xFF13322B),
          shape = RoundedCornerShape(18.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, YaarGreen.copy(alpha = 0.6f)),
          modifier = Modifier
            .clickable { onAiClick() }
            .testTag("ai_assistant_top_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "AI",
              tint = YaarGreen,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "AI یار",
              color = YaarGreen,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Action Icons: Search & Profile
        IconButton(
          onClick = { onToggleSearch(true) },
          modifier = Modifier.testTag("search_action_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search",
            tint = YaarTextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }

        IconButton(
          onClick = onProfileClick,
          modifier = Modifier.testTag("profile_action_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Profile",
            tint = YaarTextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
