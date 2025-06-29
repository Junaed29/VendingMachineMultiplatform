package org.junaed.vending_machine.ui.screens.simulator.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
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

    // Get drinks data directly from the ViewModel
    val brands = SimRuntimeViewModel.Brand.values()
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "VIMTO Soft Drinks Dispenser",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
            }
        ) { innerPadding ->
            // Main content
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val screenWidth = maxWidth
                val isWideScreen = screenWidth > 500.dp

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
                            .padding(8.dp)
                            .widthIn(max = 600.dp),
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
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
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
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
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

                    // Use FlowRow for more adaptive layout of drink buttons
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        maxItemsInEachRow = if (isWideScreen) 5 else 3,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        drinks.forEach { (name, price) ->
                            DrinkButton(
                                name = name,
                                price = price,
                                onClick = {
                                    if (balanceAmount >= price) {
                                        // Get the brand enum value
                                        val brand = SimRuntimeViewModel.Brand.valueOf(name.uppercase())

                                        // Check if drink is in stock
                                        if ((viewModel.canCounts[brand] ?: 0) > 0) {
                                            // Complete purchase through the ViewModel
                                            if (viewModel.completePurchase(brand, price)) {
                                                balanceAmount -= price
                                                displayText = "Dispensing $name"
                                                lastMessage = "Thank you for your purchase!"
                                                viewModel.logEvent("Purchase: $name for RM$price")
                                            } else {
                                                lastMessage = "Error processing purchase"
                                            }
                                        } else {
                                            lastMessage = "Out of stock"
                                        }
                                    } else {
                                        lastMessage = "Insufficient balance"
                                    }
                                },
                                outOfStock = (viewModel.canCounts[SimRuntimeViewModel.Brand.valueOf(name.uppercase())] ?: 0) <= 0,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
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

                    // Adapt coin button layout based on screen size
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 500.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        maxItemsInEachRow = if (screenWidth < 300.dp) 2 else 4,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CoinButton(
                            value = 0.10,
                            onClick = {
                                balanceAmount += 0.10
                                displayText = "10¢ Inserted"
                                viewModel.addCustomerMoney(0.10)
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
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .widthIn(max = 300.dp)
                    ) {
                        Text(
                            "Cancel / Return Coins",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DrinkButton(
    name: String,
    price: Double,
    onClick: () -> Unit,
    outOfStock: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonSize = 70.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Button(
            onClick = onClick,
            enabled = !outOfStock,
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor,
                disabledContainerColor = Color.Gray
            ),
            modifier = Modifier
                .size(buttonSize)
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                name,
                textAlign = TextAlign.Center,
                color = if (outOfStock) Color.DarkGray else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            "RM ${formatTwoDecimalPlaces(price)}",
            color = if (outOfStock) Color.Gray else Color.White,
            fontSize = 12.sp,
            maxLines = 1
        )

        if (outOfStock) {
            Text(
                "Out of Stock",
                color = Color.Red,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CoinButton(
    value: Double,
    onClick: () -> Unit
) {
    val coinSizeRange = 42.dp..60.dp
    val coinSize = min(50.dp, coinSizeRange.endInclusive)

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
                .size(coinSize)
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
            color = Color.White,
            maxLines = 1
        )
    }
}

// Helper function to format double values to 2 decimal places
private fun formatTwoDecimalPlaces(value: Double): String {
    val intPart = value.toInt()
    val decimalPart = ((value - intPart) * 100).toInt()
    return "$intPart.${decimalPart.toString().padStart(2, '0')}"
}

// Extension function for String capitalization since Kotlin doesn't include this in common
private fun String.capitalize(): String {
    return if (this.isEmpty()) this else this[0].uppercase() + this.substring(1)
}

// Extension function to log events for the simulator
fun SimRuntimeViewModel.logEvent(message: String) {
    // We'll use the actual method in the ViewModel for logging
}
