package com.jadugar.calculator.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Dark Color Scheme ────────────────────────────────────────────────────────
private val darkColorScheme = darkColorScheme(
    primary          = DarkAccentPrimary,
    secondary        = DarkAccentSecondary,
    tertiary         = DarkAccentTertiary,
    background       = DarkBackground,
    surface          = DarkSurface,
    surfaceVariant   = DarkSurfaceVariant,
    onPrimary        = Color.White,
    onSecondary      = Color.White,
    onBackground     = DarkOnBackground,
    onSurface        = DarkOnSurface,
    outline          = DarkOutline,
)

// ── Light Color Scheme ───────────────────────────────────────────────────────
private val lightColorScheme = lightColorScheme(
    primary          = LightAccentPrimary,
    secondary        = LightAccentSecondary,
    tertiary         = LightAccentTertiary,
    background       = LightBackground,
    surface          = LightSurface,
    surfaceVariant   = LightSurfaceVariant,
    onPrimary        = Color.White,
    onSecondary      = Color.White,
    onBackground     = LightOnBackground,
    onSurface        = LightOnSurface,
    outline          = LightOutline,
)

// ── Typography ───────────────────────────────────────────────────────────────
val JadugarTypography = Typography()

// ── App Theme composable ─────────────────────────────────────────────────────
@Composable
fun JadugarTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = JadugarTypography,
        content     = content,
    )
}

// ── Convenience accessors for custom tokens ──────────────────────────────────
val MaterialTheme.sliderTrackColor: Color
    @Composable get() = if (colorScheme.background == DarkBackground) DarkSliderTrack else LightSliderTrack

val MaterialTheme.resultCardColor: Color
    @Composable get() = if (colorScheme.background == DarkBackground) DarkResultCard else LightResultCard
