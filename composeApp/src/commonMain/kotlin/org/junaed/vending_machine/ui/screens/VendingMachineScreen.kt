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
import androidx.compose.runtime.remember
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
import org.junaed.vending_machine.logic.CoinRepository
import org.junaed.vending_machine.ui.components.CoinButton
import org.junaed.vending_machine.ui.components.DrinkSelectionButton
import org.junaed.vending_machine.ui.theme.VendingMachineColors
import org.junaed.vending_machine.ui.utils.WindowSize
import org.junaed.vending_machine.ui.utils.rememberWindowSize
import org.junaed.vending_machine.viewmodel.VendingMachineViewModel

/**
 * VIMTO Soft Drinks Dispenser Screen
 * This screen displays the vending machine interface modeled after a VIMTO vending machine
 */
class VendingMachineScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Initialize ViewModel
        val viewModel = remember { VendingMachineViewModel() }

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
                        containerColor = VendingMachineColors.MachinePanelColor
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
                                VendingMachineColors.MachineBackground,
                                VendingMachineColors.MachineBackground.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                // Choose layout based on device form factor
                if (isDesktop) {
                    DesktopLayout(viewModel, innerPadding)
                } else {
                    MobileLayout(viewModel, innerPadding)
                }
            }
        }
    }

    @Composable
    private fun DesktopLayout(
        viewModel: VendingMachineViewModel,
        innerPadding: androidx.compose.foundation.layout.PaddingValues
    ) {
        // Handle the change not available dialog
        ChangeNotAvailableDialog(
            show = viewModel.showChangeNotAvailableDialog,
            onProceed = { viewModel.proceedWithoutChange() },
            onCancel = { viewModel.returnCash() },
            onDismiss = { viewModel.closeDialog() }
        )

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
                VimtoMachineHeader()

                Spacer(modifier = Modifier.height(16.dp))

                // Status message section
                StatusMessageSection(viewModel.uiMessage)

                Spacer(modifier = Modifier.height(16.dp))

                // Coin insertion section
                CoinInsertionSection(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Total money display
                MoneyDisplaySection(viewModel.totalInserted)

                Spacer(modifier = Modifier.height(16.dp))

                // Terminate Transaction button
                TerminateTransactionButton(viewModel)

                Spacer(modifier = Modifier.height(8.dp))

                // Return cash button
                ReturnCashButton(
                    onReturnCash = { viewModel.returnCash() }
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
                DrinkSelectionSection(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Purchase button
                PurchaseButton(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // No change message
                NoChangeSection(showNoChangeMessage = viewModel.showNoChangeMessage)

                Spacer(modifier = Modifier.height(16.dp))

                // Collection slots
                CollectionSlotsSection(
                    changeAmount = viewModel.changeAmount,
                    dispensedDrink = viewModel.dispensedDrink,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @Composable
    private fun MobileLayout(
        viewModel: VendingMachineViewModel,
        innerPadding: androidx.compose.foundation.layout.PaddingValues
    ) {
        // Handle the change not available dialog
        ChangeNotAvailableDialog(
            show = viewModel.showChangeNotAvailableDialog,
            onProceed = { viewModel.proceedWithoutChange() },
            onCancel = { viewModel.returnCash() },
            onDismiss = { viewModel.closeDialog() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Machine header
            VimtoMachineHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Status message section
            StatusMessageSection(viewModel.uiMessage)

            Spacer(modifier = Modifier.height(16.dp))

            // Coin insertion section
            CoinInsertionSection(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Total money display
            MoneyDisplaySection(viewModel.totalInserted)

            Spacer(modifier = Modifier.height(16.dp))

            // Drink selection section
            DrinkSelectionSection(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Purchase button
            PurchaseButton(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // No change message
            NoChangeSection(showNoChangeMessage = viewModel.showNoChangeMessage)

            Spacer(modifier = Modifier.height(16.dp))

            // Terminate Transaction button
            TerminateTransactionButton(viewModel)

            Spacer(modifier = Modifier.height(8.dp))

            // Return cash button
            ReturnCashButton(
                onReturnCash = { viewModel.returnCash() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Collection slots
            CollectionSlotsSection(
                changeAmount = viewModel.changeAmount,
                dispensedDrink = viewModel.dispensedDrink,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    private fun VimtoMachineHeader() {
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
                    "VIMTO SOFT DRINKS",
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
    private fun CoinInsertionSection(viewModel: VendingMachineViewModel) {
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
                    CoinRepository.MALAYSIAN_COINS.forEach { coin ->
                        CoinButton(
                            coin = coin,
                            colorHex = viewModel.coinColors[coin] ?: "#C0C0C0",
                            onClick = { viewModel.insertCoin(coin) }
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
                    CoinButton(
                        coin = CoinRepository.US_QUARTER,
                        colorHex = viewModel.coinColors[CoinRepository.US_QUARTER] ?: "#C0C0C0",
                        onClick = { viewModel.insertCoin(CoinRepository.US_QUARTER) }
                    )

                    CoinButton(
                        coin = CoinRepository.EURO_1,
                        colorHex = viewModel.coinColors[CoinRepository.EURO_1] ?: "#DCB950",
                        onClick = { viewModel.insertCoin(CoinRepository.EURO_1) }
                    )
                }
            }
        }

        // Show invalid coin message only when needed
        AnimatedVisibility(
            visible = viewModel.showInvalidCoinMessage,
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
    private fun DrinkSelectionSection(viewModel: VendingMachineViewModel) {
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
                viewModel.availableDrinks.forEach { drink ->
                    // Determine if this drink is selectable based on transaction state
                    val isActive = viewModel.isTransactionActive
                    val isSelected = viewModel.selectedDrink?.name == drink.name
                    val isSelectable = !isActive || isSelected

                    DrinkSelectionButton(
                        drinkItem = drink,
                        onClick = { viewModel.selectDrink(drink) },
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
    private fun TerminateTransactionButton(viewModel: VendingMachineViewModel) {
        // Only show the terminate button when a transaction is active
        if (viewModel.isTransactionActive) {
            Button(
                onClick = { viewModel.terminateTransaction() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("TERMINATE TRANSACTION", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    @Composable
    private fun CollectionSlotsSection(
        changeAmount: String,
        dispensedDrink: String,
        viewModel: VendingMachineViewModel
    ) {
        // Change collection slot
        Text(
            "COLLECT CHANGE / RETURNED CASH HERE (Click to collect)",
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            onClick = { viewModel.collectChange() },
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
            "COLLECT CAN HERE (Click to collect)",
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            onClick = { viewModel.collectDrink() },
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
                        "Change Not Available",
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
    private fun PurchaseButton(viewModel: VendingMachineViewModel) {
        val hasEnoughMoney = viewModel.selectedDrink?.let { drink ->
            viewModel.insertedCoins.isNotEmpty() &&
            try {
                val inserted = viewModel.totalInserted.toDouble()
                val price = drink.price.toDouble()
                inserted >= price
            } catch (_: NumberFormatException) {
                false
            }
        } ?: false

        Button(
            onClick = { viewModel.completePurchase() },
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
}
