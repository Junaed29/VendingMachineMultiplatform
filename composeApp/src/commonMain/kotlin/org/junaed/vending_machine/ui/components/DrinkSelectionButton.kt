package org.junaed.vending_machine.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junaed.vending_machine.model.DrinkItem

@Composable
fun DrinkSelectionButton(
    drinkItem: DrinkItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectable: Boolean = true
) {
    val inStock = drinkItem.inStock
    val isEnabled = inStock && isSelectable

    // Change background color if selected
    val backgroundColor = when {
        isSelected -> Color(0xFF2C4B8E) // Highlighted blue when selected
        inStock -> Color(0xFF1A2232)
        else -> Color(0xFF0A1622)
    }

    // Change button color based on selection and availability
    val buttonColor = when {
        isSelected -> Color(0xFF4CAF50) // Green when selected
        !isEnabled -> Color.Gray
        inStock -> Color(0xFFF39C12)
        else -> Color.Gray
    }

    // Status text for drink availability
    val statusText = when {
        !inStock -> "OUT OF STOCK"
        isSelected -> "SELECTED"
        !isSelectable -> "NOT AVAILABLE"
        else -> ""
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF4CAF50) else Color.DarkGray
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = drinkItem.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "RM ${drinkItem.price}",
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (statusText.isNotEmpty()) {
                        Text(
                            text = statusText,
                            color = when {
                                !inStock -> Color.Red
                                isSelected -> Color(0xFF4CAF50)
                                else -> Color.Yellow
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = onClick,
                enabled = isEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = Color.DarkGray
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (isSelected) "SELECTED" else "PRESS TO SELECT",
                    color = if (isEnabled || isSelected) Color.White else Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
