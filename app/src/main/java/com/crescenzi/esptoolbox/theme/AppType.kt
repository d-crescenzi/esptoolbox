package com.crescenzi.esptoolbox.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.crescenzi.esptoolbox.R

val GoogleSansFlex =
    FontFamily(
        Font(R.font.google_sans_flex_regular, FontWeight.Normal),
        Font(R.font.google_sans_flex_medium, FontWeight.Medium),
        Font(R.font.google_sans_flex_semibold, FontWeight.SemiBold),
        Font(R.font.google_sans_flex_bold, FontWeight.Bold),
    )

val AppTypography: Typography =
    Typography().run {
        copy(
            displayLarge = displayLarge.copy(fontFamily = GoogleSansFlex),
            displayMedium = displayMedium.copy(fontFamily = GoogleSansFlex),
            displaySmall = displaySmall.copy(fontFamily = GoogleSansFlex),
            headlineLarge = headlineLarge.copy(fontFamily = GoogleSansFlex),
            headlineMedium = headlineMedium.copy(fontFamily = GoogleSansFlex),
            headlineSmall = headlineSmall.copy(fontFamily = GoogleSansFlex),
            titleLarge = titleLarge.copy(fontFamily = GoogleSansFlex),
            titleMedium = titleMedium.copy(fontFamily = GoogleSansFlex),
            titleSmall = titleSmall.copy(fontFamily = GoogleSansFlex),
            bodyLarge = bodyLarge.copy(fontFamily = GoogleSansFlex),
            bodyMedium = bodyMedium.copy(fontFamily = GoogleSansFlex),
            bodySmall = bodySmall.copy(fontFamily = GoogleSansFlex),
            labelLarge = labelLarge.copy(fontFamily = GoogleSansFlex),
            labelMedium = labelMedium.copy(fontFamily = GoogleSansFlex),
            labelSmall = labelSmall.copy(fontFamily = GoogleSansFlex),
        )
    }
