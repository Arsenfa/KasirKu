package com.kasirku.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val LightColorScheme = lightColorScheme(
    primary = md_light_primary,
    onPrimary = md_light_onPrimary,
    primaryContainer = md_light_primaryContainer,
    onPrimaryContainer = md_light_onPrimaryContainer,
    secondary = md_light_secondary,
    onSecondary = md_light_onSecondary,
    secondaryContainer = md_light_secondaryContainer,
    onSecondaryContainer = md_light_onSecondaryContainer,
    tertiary = md_light_tertiary,
    background = md_light_background,
    onBackground = md_light_onBackground,
    surface = md_light_surface,
    onSurface = md_light_onSurface,
    surfaceVariant = md_light_surfaceVariant,
    onSurfaceVariant = md_light_onSurfaceVariant,
    outline = md_light_outline,
    outlineVariant = md_light_outlineVariant,
    error = md_light_error,
    onError = md_light_onError,
    surfaceContainerLowest = md_light_surfaceContainerLowest,
    surfaceContainerLow = md_light_surfaceContainerLow,
    surfaceContainerHigh = md_light_surfaceContainerHigh,
    surfaceContainerHighest = md_light_surfaceContainerHighest,
)

private val DarkColorScheme = darkColorScheme(
    primary = md_dark_primary,
    onPrimary = md_dark_onPrimary,
    primaryContainer = md_dark_primaryContainer,
    onPrimaryContainer = md_dark_onPrimaryContainer,
    secondary = md_dark_secondary,
    onSecondary = md_dark_onSecondary,
    secondaryContainer = md_dark_secondaryContainer,
    onSecondaryContainer = md_dark_onSecondaryContainer,
    tertiary = md_dark_tertiary,
    background = md_dark_background,
    onBackground = md_dark_onBackground,
    surface = md_dark_surface,
    onSurface = md_dark_onSurface,
    surfaceVariant = md_dark_surfaceVariant,
    onSurfaceVariant = md_dark_onSurfaceVariant,
    outline = md_dark_outline,
    outlineVariant = md_dark_outlineVariant,
    error = md_dark_error,
    onError = md_dark_onError,
    surfaceContainerLowest = md_dark_surfaceContainerLowest,
    surfaceContainerLow = md_dark_surfaceContainerLow,
    surfaceContainerHigh = md_dark_surfaceContainerHigh,
    surfaceContainerHighest = md_dark_surfaceContainerHighest,
)

@Composable
fun KasirKuTheme(
    darkMode: Boolean = false,
    useSystemTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = if (useSystemTheme) isSystemInDarkTheme() else darkMode
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (isDark) Color(0xFF0B0F0F).toArgb() else TealDark.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KasirKuTypography,
        content = content
    )
}

private fun Color(value: Long) = androidx.compose.ui.graphics.Color(value)
