package org.junaed.vending_machine.ui.screens.simulator.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
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
import kotlin.random.Random
import org.junaed.vending_machine.model.Coin
import org.junaed.vending_machine.logic.CoinRepository
import org.junaed.vending_machine.ui.components.CoinButton
import org.junaed.vending_machine.ui.components.DrinkSelectionButton
import org.junaed.vending_machine.ui.screens.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.theme.VendingMachineColors
import org.junaed.vending_machine.ui.utils.WindowSize
import org.junaed.vending_machine.ui.utils.rememberWindowSize
import kotlin.math.roundToInt

// Extension function for string formatting in KMP
private fun formatCurrency(value: Double): String {
    // Round to 2 decimal places
    val rounded = (value * 100).roundToInt() / 100.0

    // Convert to string with proper formatting
    return if (rounded == rounded.toInt().toDouble()) {
        "${rounded.toInt()}.00" // For whole numbers
    } else {
        val str = rounded.toString()
        // Ensure we have 2 decimal places
        if (str.substringAfter('.').length == 1) str else str
    }
}

/**
 * CustomerPanelScreen - A replica of the actual VendingMachineScreen
 *
 * This is a simulated version that mimics the appearance of the real customer panel
 * but doesn't affect the actual database - uses dummy data for simulation purposes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPanelScreen(
    viewModel: SimRuntimeViewModel,
    onClose: () -> Unit = {}
) {
    // Determine if we're on mobile or desktop
    val windowSize = rememberWindowSize()
    val isDesktop = false

    // Power cut simulation state
    var showPowerCut by remember { mutableStateOf(false) }

    // Simulation state variables
    var totalInserted by remember { mutableStateOf("0.00") }
    var uiMessage by remember { mutableStateOf("Welcome to DrinkBot! Select a drink and insert coins.") }
    var isTransactionActive by remember { mutableStateOf(false) }
    var selectedDrink by remember { mutableStateOf<DrinkItem?>(null) }
    var showNoChangeMessage by remember { mutableStateOf(false) }
    var showChangeNotAvailableDialog by remember { mutableStateOf(false) }
    var showInvalidCoinMessage by remember { mutableStateOf(false) }
    var dispensedDrink by remember { mutableStateOf("") }
    var changeAmount by remember { mutableStateOf("0.00") }

    // Dummy drinks data for simulation
    val availableDrinks = remember {
        listOf(
            DrinkItem("BRAND 1", "2.50", 10),
            DrinkItem("BRAND 2", "2.50", 8),
            DrinkItem("BRAND 3", "2.50", 5),
            DrinkItem("BRAND 4", "2.80", 3),
            DrinkItem("BRAND 5", "2.50", 0) // NOT IN STOCK example
        )
    }

    // Dummy coin colors for simulation - using proper map creation
    val coinColors = remember {
        mapOf(
            CoinRepository.MALAYSIAN_10_SEN to "#C87533",
            CoinRepository.MALAYSIAN_20_SEN to "#C87533",
            CoinRepository.MALAYSIAN_50_SEN to "#C87533",
            CoinRepository.MALAYSIAN_1_RINGGIT to "#BFBFBF",
            Coin(
                valueSen = 100,
                displayName = "€1",
                diameter = 23.25,
                thickness = 2.33,
                weight = 7.5,
                material = "Bi-metallic"
            ) to "#DCB950",
            Coin(
                valueSen = 25,
                displayName = "25¢",
                diameter = 24.26,
                thickness = 1.75,
                weight = 5.67,
                material = "Cupronickel"
            ) to "#BFBFBF"
        )
    }

    // Reference coins for validation
    val euroCoin = remember {
        Coin(
            valueSen = 100,
            displayName = "€1",
            diameter = 23.25,
            thickness = 2.33,
            weight = 7.5,
            material = "Bi-metallic"
        )
    }

    val usQuarter = remember {
        Coin(
            valueSen = 25,
            displayName = "25¢",
            diameter = 24.26,
            thickness = 1.75,
            weight = 5.67,
            material = "Cupronickel"
        )
    }

    // Inserted coins for simulation
    val insertedCoins = remember { mutableStateOf(listOf<Coin>()) }

    // Handle power cut simulation
    LaunchedEffect(showPowerCut) {
        if (showPowerCut) {
            // Show power cut for 2 seconds then restore
            kotlinx.coroutines.delay(2000)
            showPowerCut = false
            onClose()
        }
    }

    // Handle invalid coin message auto-dismiss
    LaunchedEffect(showInvalidCoinMessage) {
        if (showInvalidCoinMessage) {
            kotlinx.coroutines.delay(2000)
            showInvalidCoinMessage = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Customer Panel (Simulation)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VendingMachineColors.MachinePanelColor
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                VendingMachineColors.MachineBackground,
                                VendingMachineColors.MachineBackground.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                // Choose layout based on device form factor
                if (isDesktop) {
                    DesktopLayout(
                        innerPadding = innerPadding,
                        showPowerCut = { showPowerCut = true },
                        totalInserted = totalInserted,
                        uiMessage = uiMessage,
                        isTransactionActive = isTransactionActive,
                        selectedDrink = selectedDrink,
                        showNoChangeMessage = showNoChangeMessage,
                        showChangeNotAvailableDialog = showChangeNotAvailableDialog,
                        showInvalidCoinMessage = showInvalidCoinMessage,
                        dispensedDrink = dispensedDrink,
                        changeAmount = changeAmount,
                        availableDrinks = availableDrinks,
                        coinColors = coinColors,
                        onInsertCoin = { coin ->
                            // Compare using .equals() for Coin objects
                            if (coin.equals(usQuarter) || coin.equals(euroCoin)) {
                                showInvalidCoinMessage = true
                            } else {
                                val coinValue = when(coin.valueSen) {
                                    10 -> 0.10
                                    20 -> 0.20
                                    50 -> 0.50
                                    100 -> 1.00
                                    else -> 0.00
                                }

                                if (coinValue > 0) {
                                    val currentValue = totalInserted.toDoubleOrNull() ?: 0.0
                                    totalInserted = formatCurrency(currentValue + coinValue)
                                    insertedCoins.value = insertedCoins.value + coin
                                    isTransactionActive = true

                                    // Update message based on whether a drink is selected
                                    uiMessage = if (selectedDrink != null) {
                                        "Coin accepted. Selected: ${selectedDrink?.name} - RM ${selectedDrink?.price}"
                                    } else {
                                        "Coin accepted. Please select a drink."
                                    }
                                }
                            }
                        },
                        onSelectDrink = { drink ->
                            // Allow selecting a drink whether or not coins have been inserted
                            selectedDrink = drink
                            uiMessage = if (totalInserted.toDoubleOrNull() ?: 0.0 > 0.0) {
                                "Selected: ${drink.name} - RM ${drink.price}. Insert more coins if needed."
                            } else {
                                "Selected: ${drink.name} - RM ${drink.price}. Please insert coins."
                            }
                        },
                        onPurchase = {
                            val currentInserted = totalInserted.toDoubleOrNull() ?: 0.0
                            val drinkPrice = selectedDrink?.price?.toDoubleOrNull() ?: 0.0

                            if (selectedDrink == null) {
                                uiMessage = "Please select a drink first."
                            } else if (currentInserted < drinkPrice) {
                                uiMessage = "Not enough money. Please insert more coins."
                            } else {
                                // Calculate change
                                val change = currentInserted - drinkPrice

                                // Randomly simulate no change available scenario (10% chance)
                                if (change > 0 && Random.nextDouble() < 0.1) {
                                    showChangeNotAvailableDialog = true
                                } else {
                                    dispensedDrink = selectedDrink?.name ?: ""
                                    changeAmount = formatCurrency(change)
                                    totalInserted = "0.00"
                                    uiMessage = "Thank you for your purchase!"
                                    isTransactionActive = false
                                    selectedDrink = null
                                    insertedCoins.value = emptyList()
                                }
                            }
                        },
                        onReturnCash = {
                            if (totalInserted.toDoubleOrNull() ?: 0.0 > 0) {
                                changeAmount = totalInserted
                                totalInserted = "0.00"
                                uiMessage = "Cash returned."
                                isTransactionActive = false
                                selectedDrink = null
                                insertedCoins.value = emptyList()
                            } else {
                                uiMessage = "No money to return."
                            }
                        },
                        onTerminateTransaction = {
                            if (isTransactionActive) {
                                changeAmount = totalInserted
                                totalInserted = "0.00"
                                uiMessage = "Transaction terminated. Cash returned."
                                isTransactionActive = false
                                selectedDrink = null
                                insertedCoins.value = emptyList()
                            }
                        },
                        onProceedWithoutChange = {
                            showChangeNotAvailableDialog = false
                            showNoChangeMessage = true
                            dispensedDrink = selectedDrink?.name ?: ""
                            totalInserted = "0.00"
                            changeAmount = "0.00"
                            uiMessage = "Drink dispensed. No change given."
                            isTransactionActive = false
                            selectedDrink = null
                            insertedCoins.value = emptyList()
                        },
                        onCancelPurchase = {
                            showChangeNotAvailableDialog = false
                            changeAmount = totalInserted
                            totalInserted = "0.00"
                            uiMessage = "Purchase cancelled. Cash returned."
                            isTransactionActive = false
                            selectedDrink = null
                            insertedCoins.value = emptyList()
                        },
                        onCloseDialog = {
                            showChangeNotAvailableDialog = false
                        },
                        onCollectChange = {
                            if (changeAmount != "0.00") {
                                changeAmount = "0.00"
                                uiMessage = "Change collected."
                            }
                        },
                        onCollectDrink = {
                            if (dispensedDrink.isNotEmpty()) {
                                uiMessage = "Enjoy your ${dispensedDrink}!"
                                dispensedDrink = ""
                            }
                        }
                    )
                } else {
                    MobileLayout(
                        innerPadding = innerPadding,
                        showPowerCut = { showPowerCut = true },
                        totalInserted = totalInserted,
                        uiMessage = uiMessage,
                        isTransactionActive = isTransactionActive,
                        selectedDrink = selectedDrink,
                        showNoChangeMessage = showNoChangeMessage,
                        showChangeNotAvailableDialog = showChangeNotAvailableDialog,
                        showInvalidCoinMessage = showInvalidCoinMessage,
                        dispensedDrink = dispensedDrink,
                        changeAmount = changeAmount,
                        availableDrinks = availableDrinks,
                        coinColors = coinColors,
                        onInsertCoin = { coin ->
                            // Compare using .equals() for Coin objects
                            if (coin.equals(usQuarter) || coin.equals(euroCoin)) {
                                showInvalidCoinMessage = true
                            } else {
                                val coinValue = when(coin.valueSen) {
                                    10 -> 0.10
                                    20 -> 0.20
                                    50 -> 0.50
                                    100 -> 1.00
                                    else -> 0.00
                                }

                                if (coinValue > 0) {
                                    val currentValue = totalInserted.toDoubleOrNull() ?: 0.0
                                    totalInserted = formatCurrency(currentValue + coinValue)
                                    insertedCoins.value = insertedCoins.value + coin
                                    isTransactionActive = true

                                    // Update message based on whether a drink is selected
                                    uiMessage = if (selectedDrink != null) {
                                        "Coin accepted. Selected: ${selectedDrink?.name} - RM ${selectedDrink?.price}"
                                    } else {
                                        "Coin accepted. Please select a drink."
                                    }
                                }
                            }
                        },
                        onSelectDrink = { drink ->
                            // Allow selecting a drink whether or not coins have been inserted
                            selectedDrink = drink
                            uiMessage = if (totalInserted.toDoubleOrNull() ?: 0.0 > 0.0) {
                                "Selected: ${drink.name} - RM ${drink.price}. Insert more coins if needed."
                            } else {
                                "Selected: ${drink.name} - RM ${drink.price}. Please insert coins."
                            }
                        },
                        onPurchase = {
                            val currentInserted = totalInserted.toDoubleOrNull() ?: 0.0
                            val drinkPrice = selectedDrink?.price?.toDoubleOrNull() ?: 0.0

                            if (selectedDrink == null) {
                                uiMessage = "Please select a drink first."
                            } else if (currentInserted < drinkPrice) {
                                uiMessage = "Not enough money. Please insert more coins."
                            } else {
                                // Calculate change
                                val change = currentInserted - drinkPrice

                                // Randomly simulate no change available scenario (10% chance)
                                if (change > 0 && Random.nextDouble() < 0.1) {
                                    showChangeNotAvailableDialog = true
                                } else {
                                    dispensedDrink = selectedDrink?.name ?: ""
                                    changeAmount = formatCurrency(change)
                                    totalInserted = "0.00"
                                    uiMessage = "Thank you for your purchase!"
                                    isTransactionActive = false
                                    selectedDrink = null
                                    insertedCoins.value = emptyList()
                                }
                            }
                        },
                        onReturnCash = {
                            if (totalInserted.toDoubleOrNull() ?: 0.0 > 0) {
                                changeAmount = totalInserted
                                totalInserted = "0.00"
                                uiMessage = "Cash returned."
                                isTransactionActive = false
                                selectedDrink = null
                                insertedCoins.value = emptyList()
                            } else {
                                uiMessage = "No money to return."
                            }
                        },
                        onTerminateTransaction = {
                            if (isTransactionActive) {
                                changeAmount = totalInserted
                                totalInserted = "0.00"
                                uiMessage = "Transaction terminated. Cash returned."
                                isTransactionActive = false
                                selectedDrink = null
                                insertedCoins.value = emptyList()
                            }
                        },
                        onProceedWithoutChange = {
                            showChangeNotAvailableDialog = false
                            showNoChangeMessage = true
                            dispensedDrink = selectedDrink?.name ?: ""
                            totalInserted = "0.00"
                            changeAmount = "0.00"
                            uiMessage = "Drink dispensed. No change given."
                            isTransactionActive = false
                            selectedDrink = null
                            insertedCoins.value = emptyList()
                        },
                        onCancelPurchase = {
                            showChangeNotAvailableDialog = false
                            changeAmount = totalInserted
                            totalInserted = "0.00"
                            uiMessage = "Purchase cancelled. Cash returned."
                            isTransactionActive = false
                            selectedDrink = null
                            insertedCoins.value = emptyList()
                        },
                        onCloseDialog = {
                            showChangeNotAvailableDialog = false
                        },
                        onCollectChange = {
                            if (changeAmount != "0.00") {
                                changeAmount = "0.00"
                                uiMessage = "Change collected."
                            }
                        },
                        onCollectDrink = {
                            if (dispensedDrink.isNotEmpty()) {
                                uiMessage = "Enjoy your ${dispensedDrink}!"
                                dispensedDrink = ""
                            }
                        }
                    )
                }
            }

            // Power cut overlay
            PowerCutOverlay(isVisible = showPowerCut)
        }

        // Handle the NO CHANGE AVAILABLE dialog
        ChangeNotAvailableDialog(
            show = showChangeNotAvailableDialog,
            onProceed = {
                showChangeNotAvailableDialog = false
                showNoChangeMessage = true
                dispensedDrink = selectedDrink?.name ?: ""
                totalInserted = "0.00"
                changeAmount = "0.00"
                uiMessage = "Drink dispensed. No change given."
                isTransactionActive = false
                selectedDrink = null
                insertedCoins.value = emptyList()
            },
            onCancel = {
                showChangeNotAvailableDialog = false
                changeAmount = totalInserted
                totalInserted = "0.00"
                uiMessage = "Purchase cancelled. Cash returned."
                isTransactionActive = false
                selectedDrink = null
                insertedCoins.value = emptyList()
            },
            onDismiss = {
                showChangeNotAvailableDialog = false
            }
        )
    }
}

// Data class to represent a drink item in the simulation
data class DrinkItem(
    val name: String,
    val price: String,
    val stock: Int
)

// Extension function to convert simulator's DrinkItem to model's DrinkItem
private fun DrinkItem.toModelDrinkItem(): org.junaed.vending_machine.model.DrinkItem {
    return org.junaed.vending_machine.model.DrinkItem(
        name = this.name,
        price = this.price,
        inStock = this.stock > 0
    )
}

@Composable
private fun DesktopLayout(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    showPowerCut: () -> Unit,
    totalInserted: String,
    uiMessage: String,
    isTransactionActive: Boolean,
    selectedDrink: DrinkItem?,
    showNoChangeMessage: Boolean,
    showChangeNotAvailableDialog: Boolean,
    showInvalidCoinMessage: Boolean,
    dispensedDrink: String,
    changeAmount: String,
    availableDrinks: List<DrinkItem>,
    coinColors: Map<Coin, String>,
    onInsertCoin: (Coin) -> Unit,
    onSelectDrink: (DrinkItem) -> Unit,
    onPurchase: () -> Unit,
    onReturnCash: () -> Unit,
    onTerminateTransaction: () -> Unit,
    onProceedWithoutChange: () -> Unit,
    onCancelPurchase: () -> Unit,
    onCloseDialog: () -> Unit,
    onCollectChange: () -> Unit,
    onCollectDrink: () -> Unit
) {
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
            DrinkBotMachineHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Status message section
            StatusMessageSection(uiMessage)

            Spacer(modifier = Modifier.height(16.dp))

            // Coin insertion section
            CoinInsertionSection(
                coinColors = coinColors,
                onInsertCoin = onInsertCoin,
                showInvalidCoinMessage = showInvalidCoinMessage
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total money display
            MoneyDisplaySection(totalInserted)

            Spacer(modifier = Modifier.height(16.dp))

            // Terminate Transaction button
            if (isTransactionActive) {
                Button(
                    onClick = { onTerminateTransaction() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("TERMINATE TRANSACTION", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Return cash button
            ReturnCashButton(onReturnCash = onReturnCash)

            Spacer(modifier = Modifier.height(16.dp))

            // Power cut simulation button
            Button(
                onClick = { showPowerCut() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("SIMULATE POWER CUT", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Right panel: Drink selection and collection
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Drink selection section
            DrinkSelectionSection(
                availableDrinks = availableDrinks,
                selectedDrink = selectedDrink,
                isTransactionActive = isTransactionActive,
                onSelectDrink = onSelectDrink
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Purchase button
            PurchaseButton(
                selectedDrink = selectedDrink,
                totalInserted = totalInserted,
                onPurchase = onPurchase
            )

            Spacer(modifier = Modifier.height(16.dp))

            // No change message
            NoChangeSection(showNoChangeMessage = showNoChangeMessage)

            Spacer(modifier = Modifier.height(16.dp))

            // Collection slots
            CollectionSlotsSection(
                changeAmount = changeAmount,
                dispensedDrink = dispensedDrink,
                onCollectChange = onCollectChange,
                onCollectDrink = onCollectDrink
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MobileLayout(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    showPowerCut: () -> Unit,
    totalInserted: String,
    uiMessage: String,
    isTransactionActive: Boolean,
    selectedDrink: DrinkItem?,
    showNoChangeMessage: Boolean,
    showChangeNotAvailableDialog: Boolean,
    showInvalidCoinMessage: Boolean,
    dispensedDrink: String,
    changeAmount: String,
    availableDrinks: List<DrinkItem>,
    coinColors: Map<Coin, String>,
    onInsertCoin: (Coin) -> Unit,
    onSelectDrink: (DrinkItem) -> Unit,
    onPurchase: () -> Unit,
    onReturnCash: () -> Unit,
    onTerminateTransaction: () -> Unit,
    onProceedWithoutChange: () -> Unit,
    onCancelPurchase: () -> Unit,
    onCloseDialog: () -> Unit,
    onCollectChange: () -> Unit,
    onCollectDrink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        // Machine header
        DrinkBotMachineHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // Status message section
        StatusMessageSection(uiMessage)

        Spacer(modifier = Modifier.height(16.dp))

        // Coin insertion section
        CoinInsertionSection(
            coinColors = coinColors,
            onInsertCoin = onInsertCoin,
            showInvalidCoinMessage = showInvalidCoinMessage
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total money display
        MoneyDisplaySection(totalInserted)

        Spacer(modifier = Modifier.height(16.dp))

        // Drink selection section
        DrinkSelectionSection(
            availableDrinks = availableDrinks,
            selectedDrink = selectedDrink,
            isTransactionActive = isTransactionActive,
            onSelectDrink = onSelectDrink
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Purchase button
        PurchaseButton(
            selectedDrink = selectedDrink,
            totalInserted = totalInserted,
            onPurchase = onPurchase
        )

        Spacer(modifier = Modifier.height(16.dp))

        // No change message
        NoChangeSection(showNoChangeMessage = showNoChangeMessage)

        Spacer(modifier = Modifier.height(16.dp))

        // Terminate Transaction button
        if (isTransactionActive) {
            Button(
                onClick = { onTerminateTransaction() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("TERMINATE TRANSACTION", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Return cash button
        ReturnCashButton(onReturnCash = onReturnCash)

        Spacer(modifier = Modifier.height(16.dp))

        // Collection slots
        CollectionSlotsSection(
            changeAmount = changeAmount,
            dispensedDrink = dispensedDrink,
            onCollectChange = onCollectChange,
            onCollectDrink = onCollectDrink
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Power cut simulation button
        Button(
            onClick = { showPowerCut() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("SIMULATE POWER CUT", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrinkBotMachineHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = VendingMachineColors.MachinePanelColor
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
                "DrinkBot SOFT DRINKS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = VendingMachineColors.DisplayColor
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
    coinColors: Map<Coin, String>,
    onInsertCoin: (Coin) -> Unit,
    showInvalidCoinMessage: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.9f)
        ),
        border = BorderStroke(2.dp, Color.DarkGray),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header text with more prominent styling
            Text(
                "INSERT COIN HERE",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = VendingMachineColors.DisplayColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = VendingMachineColors.MachinePanelColor.copy(alpha = 1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = VendingMachineColors.AccentColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Malaysian Coin Buttons - Regular coin section
            Text(
                "Malaysian Coins:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Use the actual Coin objects from CoinRepository
                listOf(
                    CoinRepository.MALAYSIAN_10_SEN,
                    CoinRepository.MALAYSIAN_20_SEN,
                    CoinRepository.MALAYSIAN_50_SEN,
                    CoinRepository.MALAYSIAN_1_RINGGIT
                ).forEach { coin ->
                    CoinButton(
                        coin = coin,
                        colorHex = coinColors[coin] ?: "#C0C0C0",
                        onClick = { onInsertCoin(coin) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Foreign coin section with divider
            androidx.compose.material3.Divider(
                color = Color.DarkGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                "Foreign Coins (Will Be Rejected):",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Add US Quarter and Euro coin as examples
                val foreignCoins = listOf(
                    Coin(
                        valueSen = 25,
                        displayName = "25¢",
                        diameter = 24.26,
                        thickness = 1.75,
                        weight = 5.67,
                        material = "Cupronickel"
                    ),
                    Coin(
                        valueSen = 100,
                        displayName = "€1",
                        diameter = 23.25,
                        thickness = 2.33,
                        weight = 7.5,
                        material = "Bi-metallic"
                    )
                )

                foreignCoins.forEach { coin ->
                    CoinButton(
                        coin = coin,
                        colorHex = coinColors[coin] ?: "#C0C0C0",
                        onClick = { onInsertCoin(coin) }
                    )
                }
            }
        }
    }

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
            border = BorderStroke(1.dp, VendingMachineColors.AccentColor)
        ) {
            Text(
                "COIN NOT VALID",
                color = VendingMachineColors.AccentColor,
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
private fun MoneyDisplaySection(totalInserted: String) {
    Text(
        "TOTAL MONEY INSERTED",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.White
    )
    OutlinedTextField(
        value = "RM $totalInserted",
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            disabledTextColor = VendingMachineColors.DisplayColor,
            focusedIndicatorColor = VendingMachineColors.DisplayColor,
            unfocusedIndicatorColor = VendingMachineColors.DisplayColor,
            disabledIndicatorColor = VendingMachineColors.DisplayColor,
        ),
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun DrinkSelectionSection(
    availableDrinks: List<DrinkItem>,
    selectedDrink: DrinkItem?,
    isTransactionActive: Boolean,
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
            containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.8f)
        ),
        border = BorderStroke(2.dp, Color.DarkGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            availableDrinks.forEach { drink ->
                // Determine if this drink is selectable based on transaction state
                val isActive = isTransactionActive
                val isSelected = selectedDrink?.name == drink.name
                val isSelectable = !isActive || isSelected

                // Convert simulator DrinkItem to model DrinkItem and use the shared component
                val modelDrinkItem = drink.toModelDrinkItem()
                org.junaed.vending_machine.ui.components.DrinkSelectionButton(
                    drinkItem = modelDrinkItem,
                    onClick = { onSelectDrink(drink) },
                    isSelected = isSelected,
                    isSelectable = isSelectable
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun NoChangeSection(showNoChangeMessage: Boolean) {
    if (showNoChangeMessage) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, VendingMachineColors.AccentColor)
        ) {
            Text(
                text = "NO CHANGE AVAILABLE",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                color = VendingMachineColors.AccentColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ReturnCashButton(onReturnCash: () -> Unit) {
    Text(
        "PRESS HERE TO RETURN CASH AND TERMINATE TRANSACTION HERE",
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    )

    Button(
        onClick = onReturnCash,
        colors = ButtonDefaults.buttonColors(containerColor = VendingMachineColors.AccentColor),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("RETURN CASH", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CollectionSlotsSection(
    changeAmount: String,
    dispensedDrink: String,
    onCollectChange: () -> Unit,
    onCollectDrink: () -> Unit
) {
    // Change collection slot
    Text(
        "COLLECT CHANGE / RETURNED CASH HERE ",
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Card(
        onClick = onCollectChange,
        enabled = changeAmount != "0.00",
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(4.dp))
            .border(
                width = if (changeAmount != "0.00") 3.dp else 2.dp,
                color = if (changeAmount != "0.00") Color.Yellow else Color.DarkGray,
                shape = RoundedCornerShape(4.dp)
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = if (changeAmount == "0.00") "Empty" else "RM $changeAmount",
                color = VendingMachineColors.DisplayColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Product collection slot
    Text(
        "COLLECT CAN HERE ",
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Card(
        onClick = onCollectDrink,
        enabled = dispensedDrink.isNotEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),

        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        ),
        border = BorderStroke(
            width = if (dispensedDrink.isNotEmpty()) 3.dp else 2.dp,
            color = if (dispensedDrink.isNotEmpty()) Color.Green else Color.DarkGray
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = if (dispensedDrink.isEmpty()) "Empty" else dispensedDrink,
                color = VendingMachineColors.DisplayColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun StatusMessageSection(message: String) {
    if (message.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.8f)
            ),
            border = BorderStroke(1.dp, VendingMachineColors.AccentColor)
        ) {
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                color = VendingMachineColors.DisplayColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ChangeNotAvailableDialog(
    show: Boolean,
    onProceed: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "NO CHANGE AVAILABLE",
                    fontWeight = FontWeight.Bold,
                    color = VendingMachineColors.AccentColor
                )
            },
            text = {
                Text(
                    "Change is not available. Do you want to cancel or continue without change?",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = onProceed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.AccentColor
                    )
                ) {
                    Text("Proceed Without Change")
                }
            },
            dismissButton = {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Cancel")
                }
            },
            containerColor = VendingMachineColors.MachinePanelColor,
            titleContentColor = VendingMachineColors.DisplayColor,
            textContentColor = Color.White
        )
    }
}

@Composable
private fun PurchaseButton(
    selectedDrink: DrinkItem?,
    totalInserted: String,
    onPurchase: () -> Unit
) {
    val hasEnoughMoney = selectedDrink?.let { drink ->
        try {
            val inserted = totalInserted.toDouble()
            val price = drink.price.toDouble()
            inserted >= price
        } catch (_: NumberFormatException) {
            false
        }
    } ?: false

    Button(
        onClick = onPurchase,
        enabled = hasEnoughMoney,
        colors = ButtonDefaults.buttonColors(
            containerColor = VendingMachineColors.AccentColor,
            disabledContainerColor = Color.Gray
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            "PURCHASE DRINK",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PowerCutOverlay(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Power Outage",
                color = Color.Red.copy(alpha = 0.7f),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Extension function for DrinkSelectionButton to work with our custom DrinkItem class
@Composable
private fun DrinkSelectionButton(
    drinkItem: DrinkItem,
    onClick: () -> Unit,
    isSelected: Boolean,
    isSelectable: Boolean
) {
    // Determine if the drink is in stock (in this simulation, we assume all drinks with stock > 0 are in stock)
    val inStock = drinkItem.stock > 0
    val isEnabled = inStock && isSelectable

    // Change background color if selected (matching the shared component's logic)
    val backgroundColor = when {
        isSelected -> Color(0xFF2C4B8E) // Highlighted blue when selected
        inStock -> VendingMachineColors.MachinePanelColor
        else -> Color(0xFF0A1622) // Darker color for NOT IN STOCK
    }

    // Change border color based on selection and availability
    val borderColor = when {
        isSelected -> VendingMachineColors.AccentColor
        inStock -> Color.DarkGray
        else -> Color.Gray.copy(alpha = 0.5f)
    }

    // Text color based on selection
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = if (inStock) 1f else 0.6f)

    Card(
        onClick = { if (isEnabled) onClick() },
        enabled = isEnabled,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = borderColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    drinkItem.name,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Text(
                    text = if (inStock) "Stock: ${drinkItem.stock}" else "NOT IN STOCK",
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else
                           if (inStock) Color.White.copy(alpha = 0.7f) else Color.Red.copy(alpha = 0.7f)
                )
            }

            Text(
                "RM ${drinkItem.price}",
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
