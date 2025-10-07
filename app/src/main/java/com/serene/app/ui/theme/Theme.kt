package com.serene.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColorScheme(
    primary = LavenderMist,
    onPrimary = TextPrimary,
    secondary = GentleSky,
    onSecondary = TextPrimary,
    tertiary = BlushPetal,
    onTertiary = TextPrimary,
    background = SandHaze,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary
)

private val DarkColorPalette = darkColorScheme(
    primary = LavenderMist,
    onPrimary = Color.Black,
    secondary = GentleSky,
    onSecondary = Color.Black,
    tertiary = BlushPetal,
    onTertiary = Color.Black,
    background = Color(0xFF1C1B20),
    onBackground = SurfaceLight,
    surface = Color(0xFF232228),
    onSurface = SurfaceLight
)

@Composable
fun SereneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
