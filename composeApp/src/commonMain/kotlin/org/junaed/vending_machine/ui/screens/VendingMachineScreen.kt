package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.junaed.vending_machine.ui.components.DrinkItem

/**
 * Vending Machine Screen
 * This screen displays the vending machine interface where users can:
 * - Insert coins
 * - Select drinks
 * - Collect change and purchased items
 */
class VendingMachineScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // List of available drinks
        val drinksList = listOf(
            DrinkItem("Sparkle Pop", "1.50"),
            DrinkItem("Fizz Cola", "1.25"),
            DrinkItem("Citrus Splash", "1.75"),
            DrinkItem("Aqua Pure", "1.00"),
            DrinkItem("Berry Blast", "1.50"),
            DrinkItem("Tropical Twist", "1.25"),
            DrinkItem("Energy Surge", "1.75"),
            DrinkItem("Iced Tea", "1.00")
        )

        // State variables for the UI
        var coinInput by remember { mutableStateOf("") }
        var totalInserted by remember { mutableStateOf("0.00") }

        // Get navigator reference for handling back navigation
        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Vending Machine", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Handle back navigation to return to the main menu
                            navigator?.pop()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // SECTION: Coin Input
                Text("INSERT COIN HERE", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(
                    value = coinInput,
                    onValueChange = {
                        // TODO: Add validation for coin input (only accept valid denominations)
                        coinInput = it
                    },
                    placeholder = { Text("Insert Coin") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = MaterialTheme.shapes.medium
                )

                // TODO: Replace with conditional logic that only shows this when invalid coins are entered
                Text("COINS NOT VALID", color = Color(0xFFB00020), fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION: Total Money Display
                Text("TOTAL MONEY INSERTED", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(
                    value = totalInserted,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION: Drink Selection
                Text("SELECT DRINKS BRAND BELOW", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                drinksList.forEach { drink ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(drink.name, fontWeight = FontWeight.Medium)
                            Text("$${drink.price}", color = Color.Red)
                        }
                        Button(
                            onClick = {
                                // TODO: Implement drink selection logic
                                // 1. Check if enough money is inserted
                                // 2. Process the selection
                                // 3. Update the totalInserted amount
                                // 4. Show appropriate message
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6F0F0))
                        ) {
                            Text("PRESS TO SELECT", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TODO: Add conditional logic to display this message only when change isn't available
                Text("NO CHANGE AVAILABLE", color = Color(0xFFB00020), fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION: Cash Return
                Text(
                    "PRESS HERE TO RETURN CASH AND TERMINATE TRANSACTION",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        // TODO: Implement cash return logic
                        // 1. Reset the totalInserted amount
                        // 2. Show appropriate message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Return Cash", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // SECTION: Output Slots
                Text("COLLECT CHANGE/RETURNED CASH HERE", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = "Cash Slot",  // TODO: Replace with actual change return value
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("COLLECT CAN HERE", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = "Can Slot",  // TODO: Replace with actual selected drink
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
    }
}
