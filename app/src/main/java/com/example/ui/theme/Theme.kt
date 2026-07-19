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

// 1. Classic Glassmorphism Theme (Default)
private val ClassicGlassmorphismLight = lightColorScheme(
    primary = Color(0xFF2563EB), // Vivid Royal Blue (Slate & Blue accent)
    secondary = Color(0xFF475569), // Slate Blue Slate 600
    tertiary = Color(0xFF0EA5E9), // Vivid Sky/Ocean Blue
    background = Color(0xFFF1F5F9), // Frosted Slate 100 base
    surface = Color(0xFFFFFFFF), // Glass base translucent white (alpha handled by component)
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A), // Tailwind Slate 900
    onSurface = Color(0xFF0F172A), // Tailwind Slate 900
    primaryContainer = Color(0xFFDBEAFE), // Tailwind Blue 100
    secondaryContainer = Color(0xFFE2E8F0), // Tailwind Slate 200
    surfaceVariant = Color(0xFFE2E8F0), // Tailwind Slate 200
    onSurfaceVariant = Color(0xFF475569) // Tailwind Slate 600
)

private val ClassicGlassmorphismDark = darkColorScheme(
    primary = Color(0xFF3B82F6), // Cool Blue Accent (Tailwind Blue 500)
    secondary = Color(0xFF94A3B8), // Steel Slate Blue (Tailwind Slate 400)
    tertiary = Color(0xFF38BDF8), // Vivid Light Blue (Tailwind Sky 400)
    background = Color(0xFF0F172A), // Deep Slate 900
    surface = Color(0xFF1E293B), // Tailwind Slate 800 (translucent slate base handled by copy)
    onPrimary = Color(0xFF020617), // Tailwind Slate 950 deep dark
    onSecondary = Color(0xFF020617),
    onTertiary = Color(0xFF020617),
    onBackground = Color(0xFFF8FAFC), // Tailwind Slate 50
    onSurface = Color(0xFFF1F5F9), // Tailwind Slate 100
    primaryContainer = Color(0xFF1E3A8A), // Tailwind Blue 900
    secondaryContainer = Color(0xFF334155), // Tailwind Slate 700
    surfaceVariant = Color(0xFF1E293B), // Tailwind Slate 800
    onSurfaceVariant = Color(0xFF94A3B8) // Tailwind Slate 400
)

// 2. Midnight Emerald Theme (Aura of Truth - Luxury Forest Dark)
private val MidnightEmeraldColors = darkColorScheme(
    primary = Color(0xFF2ECC71), // Vibrant Jade Green
    secondary = Color(0xFF1ABC9C), // Turquoise Accent
    tertiary = Color(0xFF27AE60), // Emerald
    background = Color(0xFF0B140F), // Very deep forest black
    surface = Color(0xFF112217), // Forest velvet surface
    onPrimary = Color(0xFF051608),
    onSecondary = Color(0xFF031410),
    onTertiary = Color(0xFF031206),
    onBackground = Color(0xFFE2F4E6),
    onSurface = Color(0xFFE2F4E6),
    primaryContainer = Color(0xFF133C21),
    secondaryContainer = Color(0xFF10362E),
    surfaceVariant = Color(0xFF1D3B27)
)

// 3. Warm Terra Theme (Organic Earthy Minimalist Light)
private val WarmTerraColors = lightColorScheme(
    primary = Color(0xFFD35400), // Terracotta clay red
    secondary = Color(0xFFE67E22), // Warm Ochre
    tertiary = Color(0xFF2C3E50), // Solid Charcoal
    background = Color(0xFFF9F6F0), // Oat fiber paper
    surface = Color(0xFFFFFDF9), // Milk surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2C241F),
    onSurface = Color(0xFF2C241F),
    primaryContainer = Color(0xFFFCEBE1),
    secondaryContainer = Color(0xFFFDEDEC),
    surfaceVariant = Color(0xFFEFE8DE)
)

// 4. Retro Cyberpunk Theme (High Contrast Neon Dark)
private val RetroCyberpunkColors = darkColorScheme(
    primary = Color(0xFF00E5FF), // Electric Neon Cyan
    secondary = Color(0xFFFFD600), // Laser Neon Yellow
    tertiary = Color(0xFFFF007F), // Hot Neon Pink
    background = Color(0xFF000000), // Pitch Black
    surface = Color(0xFF0C0C0E), // Cyber Deck dark grey
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFFCCFFFA),
    onSurface = Color(0xFFCCFFFA),
    primaryContainer = Color(0xFF00373F),
    secondaryContainer = Color(0xFF3F3500),
    surfaceVariant = Color(0xFF1F1F24)
)

// 5. Royal Amethyst Theme (Regal Orchid & Deep Velvet Dark)
private val RoyalAmethystColors = darkColorScheme(
    primary = Color(0xFFE1BEE7), // Soft Lavender Orchid
    secondary = Color(0xFF9575CD), // Amethyst Purple
    tertiary = Color(0xFFD500F9), // Fuchsia Laser
    background = Color(0xFF0D071E), // Velvet Space Black
    surface = Color(0xFF191032), // Space Royal Violet
    onPrimary = Color(0xFF0E0420),
    onSecondary = Color(0xFF0F0426),
    onTertiary = Color.White,
    onBackground = Color(0xFFF1EDFA),
    onSurface = Color(0xFFF1EDFA),
    primaryContainer = Color(0xFF341A5C),
    secondaryContainer = Color(0xFF281149),
    surfaceVariant = Color(0xFF291E47)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    templateName: String = "Classic Glassmorphism",
    dynamicColor: Boolean = false, // Set to false to prioritize custom templates!
    content: @Composable () -> Unit,
) {
    val colorScheme = when (templateName) {
        "Midnight Emerald" -> MidnightEmeraldColors
        "Warm Terra" -> WarmTerraColors
        "Retro Cyberpunk" -> RetroCyberpunkColors
        "Royal Amethyst" -> RoyalAmethystColors
        else -> {
            // Classic Glassmorphism fallback
            if (darkTheme) ClassicGlassmorphismDark else ClassicGlassmorphismLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
