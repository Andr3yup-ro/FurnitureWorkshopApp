package com.predandrei.atelier.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
 

@Composable
fun ManagerPrestanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Static color scheme to match website style (easily adjustable)
    val light = lightColorScheme(
        primary = Color(0xFF4F46E5), // Indigo 600
        onPrimary = Color.White,
        secondary = Color(0xFF10B981), // Emerald 500
        onSecondary = Color.White,
        tertiary = Color(0xFFF59E0B), // Amber 500
        onTertiary = Color.Black,
        background = Color(0xFFF8FAFC), // slate-50
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0F172A) // slate-900
    )
    val dark = darkColorScheme(
        primary = Color(0xFF818CF8),
        onPrimary = Color.Black,
        secondary = Color(0xFF34D399),
        onSecondary = Color.Black,
        tertiary = Color(0xFFFBBF24),
        onTertiary = Color.Black,
        background = Color(0xFF0B1220),
        surface = Color(0xFF111827),
        onSurface = Color(0xFFE5E7EB)
    )
    MaterialTheme(
        colorScheme = if (darkTheme) dark else light,
        typography = modernTypography(),
        content = content
    )
}

@Composable
private fun modernTypography(): Typography {
    val inter = FontFamily.SansSerif
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = inter),
        displayMedium = base.displayMedium.copy(fontFamily = inter),
        displaySmall = base.displaySmall.copy(fontFamily = inter),
        headlineLarge = base.headlineLarge.copy(fontFamily = inter),
        headlineMedium = base.headlineMedium.copy(fontFamily = inter),
        headlineSmall = base.headlineSmall.copy(fontFamily = inter),
        titleLarge = base.titleLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        titleSmall = base.titleSmall.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        bodyLarge = base.bodyLarge.copy(fontFamily = inter),
        bodyMedium = base.bodyMedium.copy(fontFamily = inter),
        bodySmall = base.bodySmall.copy(fontFamily = inter),
        labelLarge = base.labelLarge.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        labelSmall = base.labelSmall.copy(fontFamily = inter, fontWeight = FontWeight.Medium)
    )
}
