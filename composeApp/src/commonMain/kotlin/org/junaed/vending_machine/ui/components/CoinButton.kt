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
import org.junaed.vending_machine.model.Coin
import org.junaed.vending_machine.ui.theme.parseColorFromHex

/**
 * UI component that displays a coin button in the vending machine interface
 */
@Composable
fun CoinButton(
    coin: Coin,
    colorHex: String, // UI-specific property
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(coin) {
        launch {
            scale.snapTo(1f)
        }
    }

    // Calculate size based on coin diameter, with scaling for UI purposes
    val sizeDp = (coin.diameter * 2.0).coerceIn(60.0, 80.0).dp

    Card(
        shape = CircleShape,
        modifier = modifier
            .size(sizeDp)
            .scale(scale.value)
            .clickable {
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
            containerColor = parseColorFromHex(colorHex)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(sizeDp)
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
