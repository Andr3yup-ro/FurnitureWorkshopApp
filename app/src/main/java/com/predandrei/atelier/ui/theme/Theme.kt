package com.predandrei.atelier.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.predandrei.atelier.R
 

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
    // Downloadable Google Font: Inter
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val inter = GoogleFont("Inter")
    val interFamily = FontFamily(
        Font(googleFont = inter, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = inter, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = inter, fontProvider = provider, weight = FontWeight.SemiBold),
        Font(googleFont = inter, fontProvider = provider, weight = FontWeight.Bold)
    )

    val base = Typography()
    val typography = base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = interFamily),
        displayMedium = base.displayMedium.copy(fontFamily = interFamily),
        displaySmall = base.displaySmall.copy(fontFamily = interFamily),
        headlineLarge = base.headlineLarge.copy(fontFamily = interFamily),
        headlineMedium = base.headlineMedium.copy(fontFamily = interFamily),
        headlineSmall = base.headlineSmall.copy(fontFamily = interFamily),
        titleLarge = base.titleLarge.copy(fontFamily = interFamily, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = interFamily, fontWeight = FontWeight.Medium),
        titleSmall = base.titleSmall.copy(fontFamily = interFamily, fontWeight = FontWeight.Medium),
        bodyLarge = base.bodyLarge.copy(fontFamily = interFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = interFamily),
        bodySmall = base.bodySmall.copy(fontFamily = interFamily),
        labelLarge = base.labelLarge.copy(fontFamily = interFamily, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = interFamily, fontWeight = FontWeight.Medium),
        labelSmall = base.labelSmall.copy(fontFamily = interFamily, fontWeight = FontWeight.Medium),
    )

    MaterialTheme(colorScheme = if (darkTheme) dark else light, typography = typography, content = content)
}
