package org.junaed.vending_machine.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.junaed.vending_machine.ui.components.CoinButton
import org.junaed.vending_machine.ui.components.DrinkItem
import org.junaed.vending_machine.ui.components.MalaysianCoin
import org.junaed.vending_machine.ui.utils.VendingMachineHelper

/**
 * Vending Machine Screen
 * This screen displays the vending machine interface where users can:
 * - Insert Malaysian coins (10 sen, 20 sen, 50 sen, RM1)
 * - Select drinks
 * - Collect change and purchased items
 */
class VendingMachineScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Define vending machine colors
        val machineBackgroundColor = Color(0xFF2E3B4E) // Dark blue-gray for machine body
        val machinePanelColor = Color(0xFF1A2232) // Darker blue for control panel
        val accentColor = Color(0xFFE74C3C) // Bright red for accents
        val buttonColor = Color(0xFFF39C12) // Orange for buttons
        val displayColor = Color(0xFF2ECC71) // Green for display text

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
        var selectedDrink by remember { mutableStateOf<DrinkItem?>(null) }
        var changeAmount by remember { mutableStateOf("0.00") }
        var showInvalidCoinMessage by remember { mutableStateOf(false) }
        var showNoChangeMessage by remember { mutableStateOf(false) }
        var dispensedDrink by remember { mutableStateOf("") }

        // Keep track of inserted coins
        val insertedCoins = remember { mutableStateListOf<MalaysianCoin>() }

        // Get navigator reference for handling back navigation
        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Vending Machine",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Handle back navigation to return to the main menu
                            navigator?.pop()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = machinePanelColor
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                machineBackgroundColor,
                                machineBackgroundColor.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    // SECTION: Machine Panel Header - styled like a vending machine display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = machinePanelColor
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "MALAYSIAN VENDING",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = displayColor
                            )
                            Text(
                                "INSERT COINS • SELECT DRINK • ENJOY!",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION: Malaysian Coin Selection
                    Text(
                        "INSERT COIN HERE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    // Malaysian Coin Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MalaysianCoin.AVAILABLE_COINS.forEach { coin ->
                            CoinButton(
                                coin = coin,
                                onClick = {
                                    // Add coin to list of inserted coins
                                    insertedCoins.add(coin)
                                    // Update total inserted amount
                                    totalInserted = VendingMachineHelper.calculateTotal(insertedCoins)
                                    showInvalidCoinMessage = false
                                }
                            )
                        }
                    }

                    // Manual coin input field (still available as alternative)
                    OutlinedTextField(
                        value = coinInput,
                        onValueChange = { input ->
                            coinInput = input
                        },
                        placeholder = { Text("Or type coin value (10, 20, 50, 100)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                           // textColor = Color.White,
                            cursorColor = displayColor,
                            focusedIndicatorColor = displayColor,
                            unfocusedIndicatorColor = Color.Gray,
                           // backgroundColor = Color.Transparent
                        ),
                        trailingIcon = {
                            Button(
                                onClick = {
                                    val validCoin = VendingMachineHelper.validateCoinInput(coinInput)
                                    if (validCoin != null) {
                                        insertedCoins.add(validCoin)
                                        totalInserted = VendingMachineHelper.calculateTotal(insertedCoins)
                                        coinInput = ""
                                        showInvalidCoinMessage = false
                                    } else {
                                        showInvalidCoinMessage = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = buttonColor
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Insert", color = Color.White)
                            }
                        }
                    )

                    // Show invalid coin message only when needed
                    AnimatedVisibility(
                        visible = showInvalidCoinMessage,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            "COINS NOT VALID - USE 10, 20, 50, 100 (SEN)",
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION: Total Money Display
                    Text(
                        "TOTAL MONEY INSERTED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    OutlinedTextField(
                        value = "RM ${totalInserted}",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = displayColor,
                            focusedIndicatorColor = displayColor,
                            unfocusedIndicatorColor = displayColor,
                            disabledIndicatorColor = displayColor,

//                            textColor = displayColor,
//                            disabledTextColor = displayColor,
//                            focusedIndicatorColor = displayColor,
//                            unfocusedIndicatorColor = displayColor,
//                            disabledIndicatorColor = displayColor,
//                            backgroundColor = Color.Black.copy(alpha = 0.7f)
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION: Drink Selection
                    Text(
                        "SELECT DRINKS BRAND BELOW",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Display drinks in a vending machine style
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(2.dp, Color.DarkGray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            drinksList.forEach { drink ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color.DarkGray,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .background(Color(0xFF0A1622), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            drink.name,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                        Text(
                                            "RM ${drink.price}",
                                            color = accentColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            // Implement drink selection logic
                                            if (VendingMachineHelper.hasEnoughMoney(totalInserted, drink.price)) {
                                                // Calculate change
                                                changeAmount = VendingMachineHelper.calculateChange(
                                                    totalInserted,
                                                    drink.price
                                                )
                                                // Set selected drink
                                                selectedDrink = drink
                                                dispensedDrink = drink.name
                                                // Reset inserted coins after purchase
                                                totalInserted = "0.00"
                                                insertedCoins.clear()
                                                showNoChangeMessage = false
                                            } else {
                                                showNoChangeMessage = true
                                            }
                                        },
                                        shape = MaterialTheme.shapes.medium,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = buttonColor
                                        )
                                    ) {
                                        Text(
                                            "SELECT",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show "no change" message only when needed
                    AnimatedVisibility(
                        visible = showNoChangeMessage,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                        exit = fadeOut()
                    ) {
                        Text(
                            "INSUFFICIENT FUNDS - PLEASE INSERT MORE COINS",
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION: Cash Return
                    Text(
                        "PRESS HERE TO RETURN CASH AND TERMINATE",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            // Return all inserted cash
                            if (insertedCoins.isNotEmpty()) {
                                changeAmount = totalInserted
                                totalInserted = "0.00"
                                insertedCoins.clear()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("RETURN CASH", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // SECTION: Output Slots - Styled to look like actual vending machine slots
                    Text(
                        "COLLECT CHANGE/RETURNED CASH HERE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = if (changeAmount == "0.00") "Empty" else "RM $changeAmount",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(4.dp))
                            .border(2.dp, Color.DarkGray, RoundedCornerShape(4.dp)),
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                          //  textColor = displayColor,
                            disabledTextColor = displayColor,
                            focusedIndicatorColor = Color.DarkGray,
                            unfocusedIndicatorColor = Color.DarkGray,
                          //  backgroundColor = Color.Black
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "COLLECT CAN HERE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = if (dispensedDrink.isEmpty()) "Empty" else dispensedDrink,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(4.dp))
                            .border(2.dp, Color.DarkGray, RoundedCornerShape(4.dp)),
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.colors(
                         //   textColor = displayColor,
                            disabledTextColor = displayColor,
                            focusedIndicatorColor = Color.DarkGray,
                            unfocusedIndicatorColor = Color.DarkGray,
                        //    backgroundColor = Color.Black
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    // Add some bottom padding
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
