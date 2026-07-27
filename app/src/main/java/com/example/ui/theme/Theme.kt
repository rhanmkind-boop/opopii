package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryContainerBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = OnPrimaryContainerBlue,
    secondary = SecondaryCyan,
    secondaryContainer = SecondaryContainerCyan,
    onSecondaryContainer = OnSecondaryContainerCyan,
    tertiary = PremiumOrange,
    tertiaryContainer = PremiumContainerOrange,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = VpnErrorRed,
    errorContainer = Color(0xFF93000A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF005BC0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = Color(0xFF006C71),
    secondaryContainer = Color(0xFF00F4FE),
    onSecondaryContainer = Color(0xFF002021),
    tertiary = Color(0xFF8B4200),
    tertiaryContainer = Color(0xFFFFDCCB),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0E1322),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E1322),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun SecureVpnTheme(
    darkTheme: Boolean = true, // Default to sleek dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
