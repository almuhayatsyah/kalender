package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlueDark,
  onPrimary = Color(0xFF0F172A),
  primaryContainer = Color(0xFF1E3A8A),
  onPrimaryContainer = Color(0xFFDBEAFE),
  secondary = SecondaryTealDark,
  onSecondary = Color(0xFF042F2E),
  secondaryContainer = Color(0xFF115E59),
  onSecondaryContainer = Color(0xFFCCFBF1),
  tertiary = AccentAmber,
  error = AccentRedLight,
  background = SurfaceDark,
  onBackground = TextPrimaryDark,
  surface = CardSurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDBEAFE),
  onPrimaryContainer = Color(0xFF1E3A8A),
  secondary = SecondaryTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = Color(0xFF115E59),
  tertiary = AccentAmber,
  error = AccentRed,
  background = SurfaceLight,
  onBackground = TextPrimaryLight,
  surface = CardSurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
