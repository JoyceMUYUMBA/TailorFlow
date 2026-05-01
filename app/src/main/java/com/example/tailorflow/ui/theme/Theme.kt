package com.example.tailorflow.ui.theme

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
    primary = TailorGold,
    onPrimary = TailorDarkBg,
    primaryContainer = TailorSurface,
    onPrimaryContainer = TailorGold,
    secondary = TailorGold,
    onSecondary = TailorDarkBg,
    background = TailorDarkBg,
    onBackground = TailorTextPrimary,
    surface = TailorSurface,
    onSurface = TailorTextPrimary,
    surfaceVariant = TailorSurface,
    onSurfaceVariant = TailorTextSecondary,
    outline = TailorCardBorder
)

// For this specific design, we'll use the same color scheme for light and dark 
// or at least prioritize the dark aesthetic shown in the image.
private val LightColorScheme = DarkColorScheme 

@Composable
fun TailorFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to maintain the specific brand identity from the image
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always use the dark theme from the image

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
