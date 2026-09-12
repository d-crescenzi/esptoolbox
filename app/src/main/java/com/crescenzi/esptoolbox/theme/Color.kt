package com.crescenzi.esptoolbox.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

fun appDarkColorScheme(): ColorScheme =
    darkColorScheme(
        primary = Color(0xFFFFFFFF),
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF303030),
        onPrimaryContainer = Color(0xFFE3E3E3),
        secondary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF303030),
        onSecondaryContainer = Color(0xFFE3E3E3),
        background = Color(0xFF000000),
        onBackground = Color(0xFFE3E3E3),
        surface = Color(0xFF000000),
        onSurface = Color(0xFFE3E3E3),
        surfaceVariant = Color(0xFF2C2C2E),
        surfaceDim = Color(0xFF000000),
        surfaceBright = Color(0xFF2C2C2E),
        surfaceContainerLowest = Color(0xFF1C1C1E),
        surfaceContainerLow = Color(0xFF1C1C1E),
        surfaceContainer = Color(0xFF1C1C1E),
        surfaceContainerHigh = Color(0xFF1C1C1E),
        surfaceContainerHighest = Color(0xFF1C1C1E),
        onSurfaceVariant = Color(0xFF8D8D93),
        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFE3E3E3),
        outlineVariant = Color(0xFF474747),
    )
