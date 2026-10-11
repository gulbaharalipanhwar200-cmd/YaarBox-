package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.Friend
import com.example.data.YaarBoxSoundService
import com.example.data.YaarTokComment
import com.example.data.YaarTokVideo
import com.example.ui.components.YaarAvatar
import com.example.ui.theme.YaarCallRed
import com.example.ui.theme.YaarCyan
import com.example.ui.theme.YaarDarkBg
import com.example.ui.theme.YaarDarkBorder
import com.example.ui.theme.YaarDarkCard
import com.example.ui.theme.YaarGreen
import com.example.ui.theme.YaarTextPrimary
import com.example.ui.theme.YaarTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun YaarTokScreen(
  videos: List<YaarTokVideo>,
  commentsMap: Map<String, List<YaarTokComment>>,
  friendsList: List<Friend>,
  onToggleLike: (String) -> Unit,
  onToggleFollow: (String) -> Unit,
  onAddComment: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val pagerState = rememberPagerState(pageCount = { videos.size })
  var activeCommentsVideoId by remember { mutableStateOf<String?>(null) }
  var activeShareVideo by remember { mutableStateOf<YaarTokVideo?>(null) }
  var activeSettingsVideo by remember { mutableStateOf<YaarTokVideo?>(null) }
  var toastNotice by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(toastNotice) {
    if (toastNotice != null) {
      delay(2200)
      toastNotice = null
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("yaartok_feed_screen")
  ) {
    VerticalPager(
      state = pagerState,
      modifier = Modifier.fillMaxSize(),
      key = { videos[it].id }
    ) { pageIndex ->
      val video = videos[pageIndex]
      val isPageActive = pagerState.currentPage == pageIndex

      YaarTokVideoItem(
        video = video,
        isActive = isPageActive,
        onToggleLike = { onToggleLike(video.id) },
        onToggleFollow = { onToggleFollow(video.id) },
        onOpenComments = { activeCommentsVideoId = video.id },
        onOpenShare = { activeShareVideo = video },
        onOpenSettings = { activeSettingsVideo = video }
      )
    }

    // Top Bar overlay with "YaarTok" branding
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(YaarGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "YaarTok",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 20.sp
          )
        )
      }

      Surface(
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarGreen.copy(alpha = 0.5f))
      ) {
        Text(
          text = "Reels & Shorts",
          color = YaarGreen,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
      }
    }

    // Toast alert overlay
    AnimatedVisibility(
      visible = toastNotice != null,
      enter = fadeIn() + scaleIn(),
      exit = fadeOut() + scaleOut(),
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 32.dp)
    ) {
      Surface(
        color = Color(0xDD1E242C),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, YaarGreen.copy(alpha = 0.4f))
      ) {
        Text(
          text = toastNotice ?: "",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
      }
    }

    // Comments Sheet
    activeCommentsVideoId?.let { videoId ->
      val comments = commentsMap[videoId] ?: emptyList()
      YaarTokCommentsSheet(
        comments = comments,
        onDismiss = { activeCommentsVideoId = null },
        onAddComment = { text -> onAddComment(videoId, text) }
      )
    }

    // Share Sheet
    activeShareVideo?.let { video ->
      YaarTokShareSheet(
        video = video,
        friends = friendsList,
        onDismiss = { activeShareVideo = null },
        onShared = { friendName ->
          activeShareVideo = null
          toastNotice = "$friendName کو ویڈیو بھیج دی گئی! 🚀"
        }
      )
    }

    // Settings Sheet
    activeSettingsVideo?.let { video ->
      YaarTokSettingsSheet(
        video = video,
        onDismiss = { activeSettingsVideo = null },
        onAction = { actionName ->
          activeSettingsVideo = null
          toastNotice = actionName
        }
      )
    }
  }
}

@Composable
private fun YaarTokVideoItem(
  video: YaarTokVideo,
  isActive: Boolean,
  onToggleLike: () -> Unit,
  onToggleFollow: () -> Unit,
  onOpenComments: () -> Unit,
  onOpenShare: () -> Unit,
  onOpenSettings: () -> Unit
) {
  var showHeartBurst by remember { mutableStateOf(false) }
  var isPlaying by remember { mutableStateOf(true) }
  val scope = rememberCoroutineScope()
  var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

  // Heart animation scale and alpha
  val heartScale = remember { Animatable(0.2f) }
  val heartAlpha = remember { Animatable(1f) }

  // Spinning Vinyl Record Disc Animation
  val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
  val discRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "discRotate"
  )

  fun triggerHeartBurst() {
    YaarBoxSoundService.playPopSound()
    if (!video.isLiked) {
      onToggleLike()
    }
    scope.launch {
      showHeartBurst = true
      heartScale.snapTo(0.2f)
      heartAlpha.snapTo(1f)
      heartScale.animateTo(1.3f, tween(250, easing = FastOutSlowInEasing))
      heartScale.animateTo(1.0f, tween(150))
      delay(300)
      heartAlpha.animateTo(0f, tween(250))
      showHeartBurst = false
    }
  }

  // Manage playback based on pager active state
  LaunchedEffect(isActive) {
    videoViewRef?.let { vv ->
      if (isActive && isPlaying) {
        vv.start()
      } else {
        vv.pause()
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(video.bgGradientStart),
            Color(video.bgGradientEnd),
            Color.Black
          )
        )
      )
      .pointerInput(Unit) {
        detectTapGestures(
          onDoubleTap = { triggerHeartBurst() },
          onTap = {
            isPlaying = !isPlaying
            videoViewRef?.let { vv ->
              if (isPlaying) vv.start() else vv.pause()
            }
          }
        )
      }
      .testTag("yaartok_video_${video.id}")
  ) {
    // 1. Native VideoView via AndroidView
    AndroidView(
      factory = { ctx ->
        VideoView(ctx).apply {
          setVideoURI(Uri.parse(video.videoUrl))
          setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0.85f, 0.85f)
            if (isActive && isPlaying) {
              start()
            }
          }
          setOnErrorListener { _, _, _ ->
            // Prevent native Android error dialog for network video loading
            true
          }
          videoViewRef = this
        }
      },
      update = { vv ->
        videoViewRef = vv
        if (isActive && isPlaying) {
          if (!vv.isPlaying) vv.start()
        } else {
          if (vv.isPlaying) vv.pause()
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    // Dark gradient overlays top and bottom for readability
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(140.dp)
        .align(Alignment.TopCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
          )
        )
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
          )
        )
    )

    // Pause icon indicator
    if (!isPlaying) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .size(72.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Paused",
          tint = Color.White.copy(alpha = 0.85f),
          modifier = Modifier.size(44.dp)
        )
      }
    }

    // Double Tap Heart Burst Animation
    if (showHeartBurst) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .scale(heartScale.value),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = "Liked",
          tint = YaarCallRed.copy(alpha = heartAlpha.value),
          modifier = Modifier.size(110.dp)
        )
      }
    }

    // 2. Right Action Panel (Avatar, Like, Comment, Share, More)
    Column(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 12.dp, bottom = 80.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Creator Avatar with Follow Badge
      Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
      ) {
        YaarAvatar(
          name = video.creatorName,
          avatarRes = video.creatorAvatarRes,
          avatarUrl = video.creatorAvatarUrl,
          size = 50.dp
        )

        // Red/Green '+' follow badge
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .size(22.dp)
            .clip(CircleShape)
            .background(if (video.isFollowed) YaarGreen else YaarCallRed)
            .border(1.5.dp, Color.Black, CircleShape)
            .clickable { onToggleFollow() }
            .testTag("follow_creator_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (video.isFollowed) Icons.Default.Check else Icons.Default.Add,
            contentDescription = "Follow",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      // Like Button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = {
            onToggleLike()
          },
          modifier = Modifier
            .size(44.dp)
            .testTag("like_video_button")
        ) {
          Icon(
            imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like",
            tint = if (video.isLiked) YaarCallRed else Color.White,
            modifier = Modifier.size(34.dp)
          )
        }
        Text(
          text = formatCount(video.likesCount),
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Comments Button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = onOpenComments,
          modifier = Modifier
            .size(44.dp)
            .testTag("comments_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comments",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
          )
        }
        Text(
          text = formatCount(video.commentsCount),
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Share Button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = onOpenShare,
          modifier = Modifier
            .size(44.dp)
            .testTag("share_video_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
          )
        }
        Text(
          text = "Share",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // 3-Dots More / Settings Button
      IconButton(
        onClick = onOpenSettings,
        modifier = Modifier
          .size(44.dp)
          .testTag("video_settings_button")
      ) {
        Icon(
          imageVector = Icons.Default.MoreHoriz,
          contentDescription = "More",
          tint = Color.White,
          modifier = Modifier.size(32.dp)
        )
      }

      // Spinning Vinyl Record Disc in corner
      Box(
        modifier = Modifier
          .size(46.dp)
          .rotate(discRotation)
          .clip(CircleShape)
          .background(Color(0xFF1E242C))
          .border(2.dp, Color(0xFF334155), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(YaarGreen)
        )
      }
    }

    // 3. Bottom Creator Info & Sound Ticker
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .fillMaxWidth(0.78f)
        .padding(start = 16.dp, bottom = 18.dp)
    ) {
      Text(
        text = video.username,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White,
          fontSize = 16.sp
        )
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = video.caption,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Color.White.copy(alpha = 0.95f),
          fontSize = 14.sp,
          lineHeight = 20.sp
        ),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = "Song",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = video.songTitle,
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      videoViewRef?.stopPlayback()
    }
  }
}

// Comments Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YaarTokCommentsSheet(
  comments: List<YaarTokComment>,
  onDismiss: () -> Unit,
  onAddComment: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var newCommentText by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = YaarDarkBg,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .imePadding()
        .navigationBarsPadding()
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Comments (${comments.size})",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = YaarTextPrimary
          )
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = YaarTextSecondary)
        }
      }

      HorizontalDivider(color = YaarDarkBorder, thickness = 0.8.dp)

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(300.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(comments, key = { it.id }) { comment ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
          ) {
            YaarAvatar(
              name = comment.authorName,
              avatarRes = comment.authorAvatarRes,
              size = 38.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = comment.authorName,
                  color = YaarTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "• ${comment.timeAgo}",
                  color = YaarTextSecondary.copy(alpha = 0.6f),
                  fontSize = 11.sp
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = comment.text,
                color = YaarTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )
            }
            IconButton(
              onClick = {},
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "Like comment",
                tint = YaarTextSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      // Input row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextField(
          value = newCommentText,
          onValueChange = { newCommentText = it },
          placeholder = { Text("Add a comment...", color = YaarTextSecondary) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = YaarDarkCard,
            unfocusedContainerColor = YaarDarkCard,
            focusedTextColor = YaarTextPrimary,
            unfocusedTextColor = YaarTextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = {
            if (newCommentText.isNotBlank()) {
              onAddComment(newCommentText)
              newCommentText = ""
            }
          },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(YaarGreen)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Post",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

// Share Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YaarTokShareSheet(
  video: YaarTokVideo,
  friends: List<Friend>,
  onDismiss: () -> Unit,
  onShared: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = YaarDarkBg,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .navigationBarsPadding()
    ) {
      Text(
        text = "Share Video With Yaars",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = YaarTextPrimary
        )
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Direct Send to Friends
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(friends.take(6)) { friend ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onShared(friend.name) }
          ) {
            YaarAvatar(
              name = friend.name,
              avatarRes = friend.avatarRes,
              avatarUrl = friend.avatarUrl,
              size = 54.dp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = friend.name,
              color = YaarTextPrimary,
              fontSize = 12.sp,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
      HorizontalDivider(color = YaarDarkBorder, thickness = 0.8.dp)
      Spacer(modifier = Modifier.height(12.dp))

      // Quick Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        ShareActionButton(icon = Icons.Default.ContentCopy, label = "Copy Link") {
          onShared("Link Copied")
        }
        ShareActionButton(icon = Icons.Default.Share, label = "WhatsApp") {
          onShared("WhatsApp")
        }
        ShareActionButton(icon = Icons.Default.Download, label = "Save Video") {
          onShared("Video Saved")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun ShareActionButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(YaarDarkCard),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = label, tint = YaarGreen, modifier = Modifier.size(24.dp))
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = label, color = YaarTextSecondary, fontSize = 11.sp)
  }
}

// 3-Dot Settings Bottom Sheet (as requested in user prompt)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YaarTokSettingsSheet(
  video: YaarTokVideo,
  onDismiss: () -> Unit,
  onAction: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF1E1E1E),
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .navigationBarsPadding()
    ) {
      SettingsOptionRow(
        icon = Icons.Default.NotInterested,
        title = "Not Interested (دلچسپی نہیں ہے)",
        onClick = { onAction("Marked as Not Interested") }
      )

      SettingsOptionRow(
        icon = Icons.Default.Report,
        title = "Report (رپورٹ کریں)",
        onClick = { onAction("Report submitted. Thank you!") }
      )

      SettingsOptionRow(
        icon = Icons.Default.Download,
        title = "Save Video (ویڈیو محفوظ کریں)",
        onClick = { onAction("Video saved to device gallery!") }
      )

      SettingsOptionRow(
        icon = Icons.Default.Speed,
        title = "Playback Speed (اسپیڈ تبدیل کریں)",
        onClick = { onAction("Playback Speed: 1.0x Normal") }
      )

      Spacer(modifier = Modifier.height(14.dp))
    }
  }
}

@Composable
private fun SettingsOptionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
    Spacer(modifier = Modifier.width(16.dp))
    Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Normal)
  }
}

private fun formatCount(count: Int): String {
  return when {
    count >= 1000000 -> String.format("%.1fM", count / 1000000.0)
    count >= 1000 -> String.format("%.1fK", count / 1000.0)
    else -> count.toString()
  }
}
