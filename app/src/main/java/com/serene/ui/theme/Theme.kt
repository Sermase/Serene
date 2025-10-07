package com.serene.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = SerenitySoftText,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = SerenityLilac,
    onPrimaryContainer = SerenitySoftText,
    secondary = SerenitySky,
    onSecondary = SerenitySoftText,
    secondaryContainer = SerenityLavender,
    onSecondaryContainer = SerenitySoftText,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = SerenitySoftText,
    background = SerenityLavender,
    onBackground = SerenitySoftText
)

private val DarkColors = darkColorScheme(
    primary = SerenityLilac,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = SerenitySoftText,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = SerenityRose,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    background = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onBackground = androidx.compose.ui.graphics.Color(0xFFEDE7F6),
    surface = androidx.compose.ui.graphics.Color(0xFF2A2930),
    onSurface = androidx.compose.ui.graphics.Color(0xFFEDE7F6)
)

@Composable
fun SereneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
