package com.owlai.assistant.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LavenderPrimary,
    onPrimary = DeepNavy,
    primaryContainer = LavenderDeep,
    onPrimaryContainer = LavenderSoft,
    secondary = NeonCyan,
    onSecondary = DeepNavy,
    secondaryContainer = BorderDarkNavy,
    onSecondaryContainer = CyanSoft,
    tertiary = VividPink,
    onTertiary = DeepNavy,
    background = MidnightBackground,
    onBackground = TextPrimaryDark,
    surface = CardDarkNavy,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardDarkNavyElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDarkNavy,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = LavenderDeep,
    onPrimary = LightSurface,
    primaryContainer = LavenderSoft,
    onPrimaryContainer = DeepNavy,
    secondary = CyanAccent,
    onSecondary = DeepNavy,
    secondaryContainer = LightCard,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = VividPink,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = ErrorRed
)

@Composable
fun OwlAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
