package org.junaed.vending_machine.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT, // Phone
    MEDIUM,  // Tablet
    EXPANDED // Desktop
}

/**
 * This composable determines the current window size and provides appropriate layouting guidance
 * for responsive design across mobile and desktop platforms
 */
@Composable
fun rememberWindowSize(): WindowSize {
    // On desktop we can use LocalConfiguration.current, but for KMP we need a simpler approach
    val density = LocalDensity.current

    // This is a simplification - ideally we'd detect the actual window size
    // For now we determine based on density - mobile devices have higher density
    val isHighDensity = density.density > 2.0f

    // For demonstration, we'll use a simple approach
    // In a real app, you might want to use platform-specific APIs to get the actual window size
    val windowSize by remember {
        if (isHighDensity) {
            mutableStateOf(WindowSize.COMPACT) // likely mobile
        } else {
            mutableStateOf(WindowSize.EXPANDED) // likely desktop
        }
    }

    return windowSize
}
