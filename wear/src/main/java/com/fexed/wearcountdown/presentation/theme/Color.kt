package com.fexed.wearcountdown.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val GreyPimary = Color(0xFF65818F)
val GreyVariant = Color(0xFF5A7582)
val GreenSecondary = Color(0xFF658f88)
val Red400 = Color(0xFFCF6679)

internal val wearColorPalette: Colors = Colors(
    primary = GreyPimary,
    primaryVariant = GreyVariant,
    secondary = GreenSecondary,
    secondaryVariant = GreenSecondary,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.Black
)