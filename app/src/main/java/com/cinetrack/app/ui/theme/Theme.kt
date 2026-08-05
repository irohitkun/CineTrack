package com.cinetrack.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = OnPrimaryDark,
    primaryContainer = AccentBlueDark,
    onPrimaryContainer = White,
    secondary = LightGray,
    onSecondary = White,
    background = Black,
    onBackground = OnBackground,
    surface = DarkGray,
    onSurface = OnSurface,
    surfaceVariant = MediumGray,
    onSurfaceVariant = White.copy(alpha = 0.7f),
    outline = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = White,
    primaryContainer = AccentBlueDark,
    onPrimaryContainer = White,
    secondary = LightGray,
    onSecondary = OnBackgroundLight,
    background = OffWhite,
    onBackground = OnBackgroundLight,
    surface = White,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFE7E7E7),
    onSurfaceVariant = OnBackgroundLight.copy(alpha = 0.7f),
    outline = Color(0xFFBDBDBD)
)

/** Defaults to dark mode per product spec; overridden by user preference in Settings (Phase 7). */
val LocalDarkTheme = staticCompositionLocalOf { true }

@Composable
fun CineTrackTheme(
    darkTheme: Boolean = LocalDarkTheme.current,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CineTrackTypography,
        content = content
    )
}

/** Preview helper that respects system theme. */
@Composable
fun CineTrackThemePreview(content: @Composable () -> Unit) {
    CineTrackTheme(darkTheme = isSystemInDarkTheme(), content = content)
}
