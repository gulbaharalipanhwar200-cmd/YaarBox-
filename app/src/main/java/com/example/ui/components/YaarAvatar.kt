package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarOnlineGreen

@Composable
fun YaarAvatar(
  name: String,
  modifier: Modifier = Modifier,
  avatarRes: Int? = null,
  avatarUrl: String? = null,
  size: Dp = 56.dp,
  showOnlineDot: Boolean = false,
  isOnline: Boolean = true,
  storyBorderGradient: List<Color>? = null,
  onClick: (() -> Unit)? = null
) {
  val borderModifier = if (storyBorderGradient != null) {
    Modifier
      .size(size + 6.dp)
      .border(
        width = 2.5.dp,
        brush = Brush.sweepGradient(storyBorderGradient),
        shape = CircleShape
      )
  } else {
    Modifier.size(size)
  }

  Box(
    modifier = modifier
      .then(borderModifier)
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      when {
        avatarRes != null -> {
          Image(
            painter = painterResource(id = avatarRes),
            contentDescription = name,
            modifier = Modifier
              .size(size)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }
        !avatarUrl.isNullOrEmpty() -> {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(avatarUrl)
              .crossfade(true)
              .build(),
            contentDescription = name,
            modifier = Modifier
              .size(size)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }
        else -> {
          // Initials or Icon fallback
          val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
          Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium.copy(
              color = YaarGreen,
              fontSize = (size.value * 0.4f).sp
            )
          )
        }
      }
    }

    if (showOnlineDot && isOnline) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = (-1).dp, y = (-1).dp)
          .size(size * 0.28f)
          .border(2.dp, YaarDarkBg, CircleShape)
          .background(YaarOnlineGreen, CircleShape)
      )
    }
  }
}
