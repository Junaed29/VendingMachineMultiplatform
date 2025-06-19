package org.junaed.vending_machine.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Adding a KMP-compatible hex color parsing function
private fun parseColorFromHex(colorString: String): Color {
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

@Composable
fun CoinButton(
    coin: MalaysianCoin,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(coin) {
        // This is already within a coroutine scope provided by LaunchedEffect
        launch {
            // Reset scale to ensure animation plays correctly on recomposition
            scale.snapTo(1f)
        }
    }

    Card(
        shape = CircleShape,
        modifier = modifier
            .size(70.dp)
            .scale(scale.value)
            .clickable {
                // Use the properly scoped coroutine for animation
                coroutineScope.launch {
                    scale.animateTo(
                        targetValue = 0.8f,
                        animationSpec = tween(100)
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(100)
                    )
                }
                onClick()
            },
        border = BorderStroke(2.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = parseColorFromHex(coin.colorHex)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(70.dp)
        ) {
            Text(
                text = coin.displayName,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
