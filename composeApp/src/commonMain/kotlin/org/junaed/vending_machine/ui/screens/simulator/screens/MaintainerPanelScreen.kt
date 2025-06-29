package org.junaed.vending_machine.ui.screens.simulator.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junaed.vending_machine.ui.screens.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * MaintainerPanelScreen - A replica of the actual MaintenanceScreen
 *
 * This is a simulated version that mimics the appearance of the real maintainer panel
 * but doesn't affect the actual database
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintainerPanelScreen(
    viewModel: SimRuntimeViewModel,
    onClose: () -> Unit = {}
) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // The correct password is "vimto123" in this simulation
    val correctPassword = "123456"

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
                        "Maintenance Panel",
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
            if (!isLoggedIn) {
                // Login screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1D0E1E)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Maintenance Access",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = VendingMachineColors.DisplayColor,
                                    unfocusedLabelColor = VendingMachineColors.DisplayColor,
                                    focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                                    unfocusedContainerColor = Color.Black.copy(alpha = 0.5f)
                                )
                            )

                            if (errorMessage.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    errorMessage,
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (password == correctPassword) {
                                        isLoggedIn = true
                                        errorMessage = ""
                                        viewModel.logEvent("Maintainer logged in (simulated)")
                                    } else {
                                        errorMessage = "Invalid password"
                                        viewModel.logEvent("Failed login attempt (simulated)")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VendingMachineColors.ButtonColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Login")
                            }
                        }
                    }

                    Text(
                        "Hint: The password is '123456'",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                // Maintenance panel content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Tab bar for different maintenance functions
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = VendingMachineColors.MachinePanelColor
                    ) {
                        listOf("Stock", "Prices", "Cash", "Status").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, color = Color.White) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content based on selected tab
                    when (selectedTabIndex) {
                        0 -> StockTab(viewModel)
                        1 -> PricesTab(viewModel)
                        2 -> CashTab(viewModel)
                        3 -> StatusTab(viewModel)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout button
                    Button(
                        onClick = {
                            if (viewModel.doorLocked) {
                                isLoggedIn = false
                                password = ""
                                viewModel.logEvent("Maintainer logged out (simulated)")
                            } else {
                                viewModel.logEvent("Logout attempted but door is unlocked (simulated)")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = viewModel.doorLocked // Important: only enabled when door is locked
                    ) {
                        Text("Logout / Exit")
                    }

                    // Door status warning
                    if (!viewModel.doorLocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Door is unlocked. Please lock the door before logging out.",
                            color = Color.Red,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StockTab(viewModel: SimRuntimeViewModel) {
    // Get brands from the Enum rather than hardcoding
    val brands = SimRuntimeViewModel.Brand.values().map { it.name.lowercase().capitalize() }

    Column {
        Text(
            "Stock Management",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        brands.forEach { drink ->
            val brand = SimRuntimeViewModel.Brand.valueOf(drink.uppercase())
            var count = viewModel.canCounts[brand] ?: 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$drink:",
                    color = Color.White
                )

                Row {
                    Button(
                        onClick = {
                            if (count > 0) {
                                viewModel.updateCanCount(brand, count - 1)
                                count--
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.width(40.dp)
                    ) {
                        Text("-")
                    }

                    Text(
                        count.toString(),
                        color = Color.White,
                        modifier = Modifier
                            .width(40.dp)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            if (count < 20) {
                                viewModel.updateCanCount(brand, count + 1)
                                count++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green
                        ),
                        modifier = Modifier.width(40.dp)
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PricesTab(viewModel: SimRuntimeViewModel) {
    // Sample drink data
    val drinks = listOf("Coke" to 2.50, "Sprite" to 2.30, "Vimto" to 2.80, "Pepsi" to 2.40, "Fanta" to 2.20)

    Column {
        Text(
            "Price Management",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        drinks.forEach { (drink, price) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$drink:",
                    color = Color.White
                )

                OutlinedTextField(
                    value = formatTwoDecimalPlaces(price),
                    onValueChange = { /* Simulated action */ },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text("RM", color = Color.White) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Simulated action */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Price Changes")
        }
    }
}

@Composable
private fun CashTab(viewModel: SimRuntimeViewModel) {
    // Sample coin data
    val coins = listOf(
        "10¢" to (viewModel.coinCounts[SimRuntimeViewModel.Denom.CENT_10] ?: 0),
        "20¢" to (viewModel.coinCounts[SimRuntimeViewModel.Denom.CENT_20] ?: 0),
        "50¢" to (viewModel.coinCounts[SimRuntimeViewModel.Denom.CENT_50] ?: 0),
        "RM1" to (viewModel.coinCounts[SimRuntimeViewModel.Denom.RM_1] ?: 0)
    )

    val totalCash = viewModel.getTotalCoinValue()

    Column {
        Text(
            "Cash Management",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                coins.forEach { (denomination, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            denomination,
                            color = Color.White
                        )

                        Text(
                            "$count coins",
                            color = Color.White
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color.Gray
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Cash:",
                        fontWeight = FontWeight.Bold,
                        color = VendingMachineColors.DisplayColor
                    )

                    Text(
                        "RM ${formatTwoDecimalPlaces(totalCash)}",
                        fontWeight = FontWeight.Bold,
                        color = VendingMachineColors.DisplayColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Simulated action */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Collect All Cash")
        }
    }
}

@Composable
private fun StatusTab(viewModel: SimRuntimeViewModel) {
    Column {
        Text(
            "Machine Status",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatusItem(
            label = "Door Lock",
            value = if (viewModel.doorLocked) "Locked" else "Unlocked",
            color = if (viewModel.doorLocked) Color.Green else Color.Red
        )

        StatusItem(
            label = "Stock Level",
            value = "${viewModel.getTotalCans()} cans",
            color = when {
                viewModel.getTotalCans() > 10 -> Color.Green
                viewModel.getTotalCans() > 5 -> Color.Yellow
                else -> Color.Red
            }
        )

        StatusItem(
            label = "Cash Level",
            value = "RM ${formatTwoDecimalPlaces(viewModel.getTotalCoinValue())}",
            color = VendingMachineColors.DisplayColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Simulated action */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE63B8C) // VIMTO pink
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Run Diagnostics")
        }
    }
}

@Composable
private fun StatusItem(
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                color = Color.White
            )

            Text(
                value,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helper function to format double values to 2 decimal places
private fun formatTwoDecimalPlaces(value: Double): String {
    val intPart = value.toInt()
    val decimalPart = ((value - intPart) * 100).toInt()
    return "$intPart.${decimalPart.toString().padStart(2, '0')}"
}
