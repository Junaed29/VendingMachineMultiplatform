package org.junaed.vending_machine.ui.screens.simulator.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junaed.vending_machine.ui.screens.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * CustomerPanelScreen - A replica of the actual VendingMachineScreen
 *
 * This is a simulated version that mimics the appearance of the real customer panel
 * but doesn't affect the actual database
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPanelScreen(
    viewModel: SimRuntimeViewModel,
    onClose: () -> Unit = {}
) {
    var displayText by remember { mutableStateOf("Insert Coins To Begin") }
    var balanceAmount by remember { mutableStateOf(0.0) }
    var lastMessage by remember { mutableStateOf("") }

    // Sample drink data
    val drinks = listOf(
        "Coke" to 2.50,
        "Sprite" to 2.30,
        "Vimto" to 2.80,
        "Pepsi" to 2.40,
        "Fanta" to 2.20
    )

    Card(
        modifier = Modifier.fillMaxWidth(0.95f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = VendingMachineColors.MachineBackground
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with close button
            TopAppBar(
                title = {
                    Text(
                        "VIMTO Soft Drinks Dispenser",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VendingMachineColors.MachinePanelColor
                ),
                actions = {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display screen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, VendingMachineColors.MachinePanelColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            displayText,
                            color = VendingMachineColors.DisplayColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Balance: RM ${formatTwoDecimalPlaces(balanceAmount)}",
                            color = VendingMachineColors.DisplayColor,
                            fontSize = 16.sp
                        )

                        if (lastMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                lastMessage,
                                color = Color.Yellow,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Drink selection buttons
                Text(
                    "Select Your Drink",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grid of drink buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        drinks.take(3).forEach { (name, price) ->
                            DrinkButton(
                                name = name,
                                price = price,
                                onClick = {
                                    if (balanceAmount >= price) {
                                        balanceAmount -= price
                                        displayText = "Dispensing $name"
                                        lastMessage = "Thank you for your purchase!"

                                        // Simulate drink dispensing in real app
                                        viewModel.logEvent("Simulated purchase: $name for RM$price")
                                    } else {
                                        lastMessage = "Insufficient balance"
                                    }
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        drinks.drop(3).forEach { (name, price) ->
                            DrinkButton(
                                name = name,
                                price = price,
                                onClick = {
                                    if (balanceAmount >= price) {
                                        balanceAmount -= price
                                        displayText = "Dispensing $name"
                                        lastMessage = "Thank you for your purchase!"

                                        // Simulate drink dispensing in real app
                                        viewModel.logEvent("Simulated purchase: $name for RM$price")
                                    } else {
                                        lastMessage = "Insufficient balance"
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Coin insertion area
                Text(
                    "Insert Coins",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CoinButton(
                        value = 0.10,
                        onClick = {
                            balanceAmount += 0.10
                            displayText = "10¢ Inserted"
                            viewModel.logEvent("Inserted 10¢")
                        }
                    )

                    CoinButton(
                        value = 0.20,
                        onClick = {
                            balanceAmount += 0.20
                            displayText = "20¢ Inserted"
                            viewModel.logEvent("Inserted 20¢")
                        }
                    )

                    CoinButton(
                        value = 0.50,
                        onClick = {
                            balanceAmount += 0.50
                            displayText = "50¢ Inserted"
                            viewModel.logEvent("Inserted 50¢")
                        }
                    )

                    CoinButton(
                        value = 1.00,
                        onClick = {
                            balanceAmount += 1.00
                            displayText = "RM1 Inserted"
                            viewModel.logEvent("Inserted RM1")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                Button(
                    onClick = {
                        if (balanceAmount > 0) {
                            lastMessage = "Returned RM ${formatTwoDecimalPlaces(balanceAmount)}"
                            viewModel.logEvent("Returned RM ${formatTwoDecimalPlaces(balanceAmount)}")
                            balanceAmount = 0.0
                            displayText = "Insert Coins To Begin"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel / Return Coins")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DrinkButton(
    name: String,
    price: Double,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor
            ),
            modifier = Modifier
                .width(80.dp)
                .height(70.dp)
        ) {
            Text(name, textAlign = TextAlign.Center)
        }

        Text(
            "RM ${formatTwoDecimalPlaces(price)}",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CoinButton(
    value: Double,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coinColor = when(value) {
            0.10, 0.20 -> Color(0xFFC0C0C0) // Silver
            0.50 -> Color(0xFFE6BE8A) // Gold
            else -> Color(0xFFDCB950) // RM1 color
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .background(coinColor, CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
                .padding(8.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (value < 1.0) "${(value * 100).toInt()}¢" else "RM1",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "RM ${formatTwoDecimalPlaces(value)}",
            fontSize = 10.sp,
            color = Color.White
        )
    }
}

// Helper function to format double values to 2 decimal places
private fun formatTwoDecimalPlaces(value: Double): String {
    val intPart = value.toInt()
    val decimalPart = ((value - intPart) * 100).toInt()
    return "$intPart.${decimalPart.toString().padStart(2, '0')}"
}

// Extension function to log events for the simulator
fun SimRuntimeViewModel.logEvent(message: String) {
    // Use the existing SimRuntimeViewModel to log events
    this.addCustomerMoney(0.0) // This is a dummy call to satisfy the compiler
    // In a real implementation, we would call a specific method to log events
}
