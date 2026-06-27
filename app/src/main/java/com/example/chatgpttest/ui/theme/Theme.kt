package com.example.chatgpttest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Lighter indigo
    secondary = Color(0xFF94A3B8),
    tertiary = Pink80,
    background = Color(0xFF0F172A), // Dark slate
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1), // Indigo 500
    secondary = Color(0xFF64748B),
    tertiary = Pink40,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B)
)

@Composable
fun ChatGPTTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Check if user has overridden the theme via AppCompatDelegate
    val context = androidx.compose.ui.platform.LocalContext.current
    val nightMode = androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode()
    val isDark = when (nightMode) {
        androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES -> true
        androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO -> false
        else -> darkTheme
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}