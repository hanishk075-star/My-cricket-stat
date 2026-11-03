package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CricketDarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldDeepest,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldSoft,
    secondary = EmeraldTurf,
    onSecondary = TextWhite,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = EmeraldSoft,
    tertiary = CricketGold,
    onTertiary = DarkBg,
    tertiaryContainer = CricketBallDark,
    onTertiaryContainer = CricketGoldSoft,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = DarkBorder,
    error = CricketBallRed
)

private val CricketLightColorScheme = lightColorScheme(
    primary = EmeraldTurf,
    onPrimary = TextWhite,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = EmeraldDeepest,
    secondary = EmeraldDark,
    onSecondary = TextWhite,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = LightTextDark,
    tertiary = CricketGold,
    onTertiary = TextWhite,
    background = LightBg,
    onBackground = LightTextDark,
    surface = LightSurface,
    onSurface = LightTextDark,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextMuted,
    outline = LightBorder,
    error = CricketBallRed
)

@Composable
fun MyCricketStatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CricketDarkColorScheme else CricketLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
