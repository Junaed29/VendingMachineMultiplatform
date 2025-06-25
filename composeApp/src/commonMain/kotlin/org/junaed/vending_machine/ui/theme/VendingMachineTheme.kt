package org.junaed.vending_machine.ui.theme

import androidx.compose.ui.graphics.Color

// Helper function for parsing hex colors in a KMP-compatible way
fun parseColorFromHex(colorString: String): Color {
    val hexColor = colorString.removePrefix("#")
    return try {
        Color(
            red = hexColor.substring(0, 2).toInt(16) / 255f,
            green = hexColor.substring(2, 4).toInt(16) / 255f,
            blue = hexColor.substring(4, 6).toInt(16) / 255f,
            alpha = if (hexColor.length >= 8) hexColor.substring(6, 8).toInt(16) / 255f else 1f
        )
    } catch (e: Exception) {
        Color.Gray // Fallback color
    }
}

// Vending Machine Theme Colors
object VendingMachineColors {
    val MachineBackground = Color(0xFF321633) // Deep purple for VIMTO theme
    val MachinePanelColor = Color(0xFF1D0E1E) // Darker purple for panels
    val AccentColor = Color(0xFFE63B8C) // VIMTO pink for accents
    val ButtonColor = Color(0xFF5992A5) // Blue for buttons
    val DisplayColor = Color(0xFF2ECC71) // Green for display text
    val AccessGrantedColor = Color(0xFF27AE60) // Green for maintenance access

    // Coin Colors
    val SilverCoin = "#C0C0C0"
    val GoldCoin = "#E6BE8A"
    val EuroCoin = "#DCB950"
}
