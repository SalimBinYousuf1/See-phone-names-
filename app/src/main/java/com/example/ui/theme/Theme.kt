package com.example.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = SalimBlue,
    onPrimary = SalimWhite,
    primaryContainer = SalimBlueLight,
    onPrimaryContainer = SalimBlue,
    secondary = SalimTextSecondary,
    onSecondary = SalimWhite,
    secondaryContainer = SalimSurfaceSecondary,
    onSecondaryContainer = SalimTextPrimary,
    background = SalimBackground,
    onBackground = SalimTextPrimary,
    surface = SalimBackground,
    onSurface = SalimTextPrimary,
    surfaceVariant = SalimSurfaceSecondary,
    onSurfaceVariant = SalimTextSecondary,
    outline = SalimDivider,
    outlineVariant = SalimCardBorder,
    error = SalimRed,
    onError = SalimWhite,
    errorContainer = SalimRedLight,
    onErrorContainer = SalimRed
)

private val DarkColorScheme = darkColorScheme(
    primary = SalimBlue,
    onPrimary = SalimWhite,
    primaryContainer = SalimBlue.copy(alpha = 0.2f),
    onPrimaryContainer = SalimBlue,
    secondary = SalimDarkTextSecondary,
    onSecondary = SalimDarkBackground,
    secondaryContainer = SalimDarkSurfaceSecondary,
    onSecondaryContainer = SalimDarkTextPrimary,
    background = SalimDarkBackground,
    onBackground = SalimDarkTextPrimary,
    surface = SalimDarkSurface,
    onSurface = SalimDarkTextPrimary,
    surfaceVariant = SalimDarkSurfaceSecondary,
    onSurfaceVariant = SalimDarkTextSecondary,
    outline = SalimDarkDivider,
    error = SalimRed,
    onError = SalimWhite
)

@Composable
fun SalimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
