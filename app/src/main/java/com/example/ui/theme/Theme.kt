package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = YaarGreen,
  onPrimary = Color.Black,
  primaryContainer = YaarGreenBright.copy(alpha = 0.2f),
  onPrimaryContainer = YaarGreen,
  secondary = YaarTeal,
  onSecondary = Color.White,
  secondaryContainer = YaarDarkCard,
  onSecondaryContainer = YaarTextPrimary,
  tertiary = YaarCyan,
  background = YaarDarkBg,
  onBackground = YaarTextPrimary,
  surface = YaarDarkSurface,
  onSurface = YaarTextPrimary,
  surfaceVariant = YaarDarkCard,
  onSurfaceVariant = YaarTextSecondary,
  outline = YaarDarkBorder,
  error = YaarCallRed,
  onError = Color.White
)

private val LightColorScheme = darkColorScheme(
  primary = YaarGreen,
  onPrimary = Color.Black,
  primaryContainer = YaarGreenBright.copy(alpha = 0.2f),
  onPrimaryContainer = YaarGreen,
  secondary = YaarTeal,
  onSecondary = Color.White,
  secondaryContainer = YaarDarkCard,
  onSecondaryContainer = YaarTextPrimary,
  tertiary = YaarCyan,
  background = YaarDarkBg,
  onBackground = YaarTextPrimary,
  surface = YaarDarkSurface,
  onSurface = YaarTextPrimary,
  surfaceVariant = YaarDarkCard,
  onSurfaceVariant = YaarTextSecondary,
  outline = YaarDarkBorder,
  error = YaarCallRed,
  onError = Color.White
)

@Composable
fun YaarBoxTheme(
  darkTheme: Boolean = true, // Default to YaarBox signature sleek dark theme
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
