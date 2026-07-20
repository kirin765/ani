package com.kiwankim.kiwankim.myapplication3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val default = Typography()

val AniTypography = Typography(
    headlineMedium = default.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
    headlineSmall = default.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = default.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp),
)
