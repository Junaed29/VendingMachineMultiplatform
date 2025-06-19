package org.junaed.vending_machine.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.junaed.vending_machine.ui.components.Coin
import org.junaed.vending_machine.ui.components.CoinButton
import org.junaed.vending_machine.ui.components.DrinkItem
import org.junaed.vending_machine.ui.components.DrinkSelectionButton
import org.junaed.vending_machine.ui.utils.VendingMachineHelper
import org.junaed.vending_machine.ui.utils.WindowSize
import org.junaed.vending_machine.ui.utils.rememberWindowSize

/**
 * VIMTO Soft Drinks Dispenser Screen
 * This screen displays the vending machine interface modeled after a VIMTO vending machine where users can:
 * - Insert Malaysian coins (10 sen, 20 sen, 50 sen, RM1)
 * - Select drinks (with stock status indication)
 * - Collect change and purchased items
 */
class VendingMachineScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Define vending machine colors based on VIMTO theme
        val machineBackgroundColor = Color(0xFF321633) // Deep purple for VIMTO theme
        val machinePanelColor = Color(0xFF1D0E1E) // Darker purple for panels
        val accentColor = Color(0xFFE63B8C) // VIMTO pink for accents
        val buttonColor = Color(0xFF5992A5) // Blue for buttons
        val displayColor = Color(0xFF2ECC71) // Green for display text

        // List of VIMTO drinks (with stock status as requested)
        val drinksList = listOf(
            DrinkItem("BRAND 1", "0.70", false),
            DrinkItem("BRAND 2", "0.70", false),
            DrinkItem("BRAND 3", "0.70", false),
            DrinkItem("BRAND 4", "0.60", false),
            DrinkItem("BRAND 5", "0.60", false)
        )

        // State variables for the UI
        var totalInserted by remember { mutableStateOf("0.00") }
        var selectedDrink by remember { mutableStateOf<DrinkItem?>(null) }
        var changeAmount by remember { mutableStateOf("0.00") }
        var showInvalidCoinMessage by remember { mutableStateOf(false) }
        var invalidCoinMessage by remember { mutableStateOf("") }
        var showNoChangeMessage by remember { mutableStateOf(true) } // Always showing as per requirement
        var dispensedDrink by remember { mutableStateOf("") }

        // Keep track of inserted coins
        val insertedCoins = remember { mutableStateListOf<Coin>() }

        // Get navigator reference for handling back navigation
        val navigator = LocalNavigator.current

        // Determine if we're on mobile or desktop
        val windowSize = rememberWindowSize()
        val isDesktop = windowSize == WindowSize.EXPANDED

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "VIMTO Soft Drinks Dispenser",
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
                // Desktop layout vs Mobile layout
                if (isDesktop) {
                    // Desktop Layout - Side by side panels
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left panel: Coin insertion and machine info
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Machine header and coin insertion
                            VimtoMachineHeader(
                                displayColor = displayColor,
                                machinePanelColor = machinePanelColor
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Coin insertion section
                            CoinInsertionSection(
                                insertedCoins = insertedCoins,
                                buttonColor = buttonColor,
                                showInvalidCoinMessage = showInvalidCoinMessage,
                                invalidCoinMessage = invalidCoinMessage,
                                accentColor = accentColor,
                                onInsertCoin = { coin ->
                                    // Validate the coin using its physical properties
                                    if (VendingMachineHelper.isValidMalaysianCoin(coin)) {
                                        // Valid Malaysian coin
                                        insertedCoins.add(coin)
                                        totalInserted = VendingMachineHelper.calculateTotal(insertedCoins)
                                        showInvalidCoinMessage = false
                                    } else {
                                        // Foreign or invalid coin
                                        val reason = VendingMachineHelper.getCoinRejectionReason(coin)
                                        invalidCoinMessage = reason ?: "Invalid coin"
                                        showInvalidCoinMessage = true
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Total money display
                            MoneyDisplaySection(
                                totalInserted = totalInserted,
                                displayColor = displayColor
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Return cash button
                            ReturnCashButton(
                                accentColor = accentColor,
                                onReturnCash = {
                                    if (insertedCoins.isNotEmpty()) {
                                        changeAmount = totalInserted
                                        totalInserted = "0.00"
                                        insertedCoins.clear()
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Right panel: Drink selection and collection
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Drink selection section
                            DrinkSelectionSection(
                                drinksList = drinksList,
                                totalInserted = totalInserted,
                                machinePanelColor = machinePanelColor,
                                onSelectDrink = { drink ->
                                    if (VendingMachineHelper.hasEnoughMoney(totalInserted, drink.price)) {
                                        changeAmount = VendingMachineHelper.calculateChange(
                                            totalInserted,
                                            drink.price
                                        )
                                        selectedDrink = drink
                                        dispensedDrink = drink.name
                                        totalInserted = "0.00"
                                        insertedCoins.clear()
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // No change message
                            NoChangeSection(showNoChangeMessage = showNoChangeMessage, accentColor = accentColor)

                            Spacer(modifier = Modifier.height(24.dp))

                            // Collection slots
                            CollectionSlotsSection(
                                changeAmount = changeAmount,
                                dispensedDrink = dispensedDrink,
                                displayColor = displayColor
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    // Mobile Layout - Stacked panels
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Machine header
                        VimtoMachineHeader(
                            displayColor = displayColor,
                            machinePanelColor = machinePanelColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Coin insertion section
                        CoinInsertionSection(
                            insertedCoins = insertedCoins,
                            buttonColor = buttonColor,
                            showInvalidCoinMessage = showInvalidCoinMessage,
                            invalidCoinMessage = invalidCoinMessage,
                            accentColor = accentColor,
                            onInsertCoin = { coin ->
                                // Validate the coin using its physical properties
                                if (VendingMachineHelper.isValidMalaysianCoin(coin)) {
                                    // Valid Malaysian coin
                                    insertedCoins.add(coin)
                                    totalInserted = VendingMachineHelper.calculateTotal(insertedCoins)
                                    showInvalidCoinMessage = false
                                } else {
                                    // Foreign or invalid coin
                                    val reason = VendingMachineHelper.getCoinRejectionReason(coin)
                                    invalidCoinMessage = reason ?: "Invalid coin"
                                    showInvalidCoinMessage = true
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Total money display
                        MoneyDisplaySection(
                            totalInserted = totalInserted,
                            displayColor = displayColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Drink selection section
                        DrinkSelectionSection(
                            drinksList = drinksList,
                            totalInserted = totalInserted,
                            machinePanelColor = machinePanelColor,
                            onSelectDrink = { drink ->
                                if (VendingMachineHelper.hasEnoughMoney(totalInserted, drink.price)) {
                                    changeAmount = VendingMachineHelper.calculateChange(
                                        totalInserted,
                                        drink.price
                                    )
                                    selectedDrink = drink
                                    dispensedDrink = drink.name
                                    totalInserted = "0.00"
                                    insertedCoins.clear()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // No change message
                        NoChangeSection(showNoChangeMessage = showNoChangeMessage, accentColor = accentColor)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Return cash button
                        ReturnCashButton(
                            accentColor = accentColor,
                            onReturnCash = {
                                if (insertedCoins.isNotEmpty()) {
                                    changeAmount = totalInserted
                                    totalInserted = "0.00"
                                    insertedCoins.clear()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Collection slots
                        CollectionSlotsSection(
                            changeAmount = changeAmount,
                            dispensedDrink = dispensedDrink,
                            displayColor = displayColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun VimtoMachineHeader(displayColor: Color, machinePanelColor: Color) {
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
                    "VIMTO SOFT DRINKS",
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
    }

    @Composable
    private fun CoinInsertionSection(
        insertedCoins: List<Coin>,
        buttonColor: Color,
        showInvalidCoinMessage: Boolean,
        invalidCoinMessage: String,
        accentColor: Color,
        onInsertCoin: (Coin) -> Unit
    ) {
        Text(
            "INSERT COIN HERE",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )

        // Malaysian Coin Buttons - Regular coin section
        Text(
            "Malaysian Coins:",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Coin.MALAYSIAN_COINS.forEach { coin ->
                CoinButton(
                    coin = coin,
                    onClick = { onInsertCoin(coin) }
                )
            }
        }

        // Foreign coin section - For demonstration
        Text(
            "Foreign Coins (Will Be Rejected):",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Add US Quarter and Euro coin as examples
            CoinButton(
                coin = Coin.US_QUARTER,
                onClick = { onInsertCoin(Coin.US_QUARTER) }
            )

            CoinButton(
                coin = Coin.EURO_1,
                onClick = { onInsertCoin(Coin.EURO_1) }
            )
        }

        // Show coin technical details (educational section)
        CoinPhysicalDetails(accentColor)

        // Show invalid coin message only when needed
        AnimatedVisibility(
            visible = showInvalidCoinMessage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.2f)
                ),
                border = BorderStroke(1.dp, accentColor)
            ) {
                Text(
                    "COIN NOT VALID - $invalidCoinMessage",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun CoinPhysicalDetails(accentColor: Color) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            border = BorderStroke(1.dp, accentColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "VIMTO Machine validates coins by physical properties:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "• Diameter (mm): Must match Malaysian coins ±${Coin.DIAMETER_TOLERANCE_MM}mm",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "• Thickness (mm): Must match Malaysian coins ±${Coin.THICKNESS_TOLERANCE_MM}mm",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "• Weight (g): Must match Malaysian coins ±${Coin.WEIGHT_TOLERANCE_G}g",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }
    }

    @Composable
    private fun MoneyDisplaySection(totalInserted: String, displayColor: Color) {
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
            ),
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }

    @Composable
    private fun DrinkSelectionSection(
        drinksList: List<DrinkItem>,
        totalInserted: String,
        machinePanelColor: Color,
        onSelectDrink: (DrinkItem) -> Unit
    ) {
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
                containerColor = machinePanelColor.copy(alpha = 0.8f)
            ),
            border = BorderStroke(2.dp, Color.DarkGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                drinksList.forEach { drink ->
                    DrinkSelectionButton(
                        brandName = drink.name,
                        price = drink.price,
                        inStock = drink.inStock,
                        onClick = { onSelectDrink(drink) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    @Composable
    private fun NoChangeSection(showNoChangeMessage: Boolean, accentColor: Color) {
        if (showNoChangeMessage) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, accentColor)
            ) {
                Text(
                    text = "NO CHANGE AVAILABLE",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
    }

    @Composable
    private fun ReturnCashButton(accentColor: Color, onReturnCash: () -> Unit) {
        Text(
            "PRESS HERE TO RETURN CASH AND TERMINATE TRANSACTION HERE",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onReturnCash,
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("RETURN CASH", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun CollectionSlotsSection(changeAmount: String, dispensedDrink: String, displayColor: Color) {
        // Change collection slot
        Text(
            "COLLECT CHANGE / RETURNED CASH HERE",
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
                disabledTextColor = displayColor,
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.DarkGray,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Product collection slot - hexagon-shaped for VIMTO style
        Text(
            "COLLECT CAN HERE",
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Created a simple slot with a diamond-like shape as requested
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Black),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(2.dp, Color.DarkGray)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = if (dispensedDrink.isEmpty()) "Empty" else dispensedDrink,
                    color = displayColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }
        }
    }
}
