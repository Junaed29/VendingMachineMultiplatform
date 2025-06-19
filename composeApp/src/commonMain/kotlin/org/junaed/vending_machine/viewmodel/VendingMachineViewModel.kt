package org.junaed.vending_machine.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.junaed.vending_machine.logic.CoinRepository
import org.junaed.vending_machine.logic.VendingMachineService
import org.junaed.vending_machine.model.Coin
import org.junaed.vending_machine.model.DrinkItem
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * ViewModel that manages the state and business logic for the vending machine screen
 */
class VendingMachineViewModel {
    private val vendingMachineService = VendingMachineService()
    private val coinRepository = CoinRepository()

    // UI State
    var totalInserted by mutableStateOf("0.00")
        private set
    var selectedDrink by mutableStateOf<DrinkItem?>(null)
        private set
    var changeAmount by mutableStateOf("0.00")
        private set
    var showInvalidCoinMessage by mutableStateOf(false)
        private set
    var invalidCoinMessage by mutableStateOf("")
        private set
    var showNoChangeMessage by mutableStateOf(true) // Always showing as per requirement
        private set
    var dispensedDrink by mutableStateOf("")
        private set

    // Collection of inserted coins
    val insertedCoins = mutableStateListOf<Coin>()

    // Predefined drink items for the vending machine
    val availableDrinks = listOf(
        DrinkItem("BRAND 1", "0.70", false),
        DrinkItem("BRAND 2", "0.70", false),
        DrinkItem("BRAND 3", "0.70", false),
        DrinkItem("BRAND 4", "0.60", false),
        DrinkItem("BRAND 5", "0.60", false)
    )

    // Map of coin display colors
    val coinColors = mapOf(
        CoinRepository.MALAYSIAN_10_SEN to VendingMachineColors.SilverCoin,
        CoinRepository.MALAYSIAN_20_SEN to VendingMachineColors.SilverCoin,
        CoinRepository.MALAYSIAN_50_SEN to VendingMachineColors.SilverCoin,
        CoinRepository.MALAYSIAN_1_RINGGIT to VendingMachineColors.GoldCoin,
        CoinRepository.US_QUARTER to VendingMachineColors.SilverCoin,
        CoinRepository.US_DIME to VendingMachineColors.SilverCoin,
        CoinRepository.EURO_1 to VendingMachineColors.EuroCoin
    )

    /**
     * Process a coin insertion attempt
     */
    fun insertCoin(coin: Coin) {
        if (vendingMachineService.isValidMalaysianCoin(coin)) {
            // Valid Malaysian coin
            insertedCoins.add(coin)
            totalInserted = vendingMachineService.calculateTotal(insertedCoins)
            showInvalidCoinMessage = false
        } else {
            // Foreign or invalid coin
            val reason = vendingMachineService.getCoinRejectionReason(coin)
            invalidCoinMessage = reason ?: "Invalid coin"
            showInvalidCoinMessage = true
        }
    }

    /**
     * Process drink selection
     */
    fun selectDrink(drink: DrinkItem) {
        if (vendingMachineService.hasEnoughMoney(totalInserted, drink.price)) {
            changeAmount = vendingMachineService.calculateChange(
                totalInserted,
                drink.price
            )
            selectedDrink = drink
            dispensedDrink = drink.name
            totalInserted = "0.00"
            insertedCoins.clear()
        }
    }

    /**
     * Return inserted cash and terminate transaction
     */
    fun returnCash() {
        if (insertedCoins.isNotEmpty()) {
            changeAmount = totalInserted
            totalInserted = "0.00"
            insertedCoins.clear()
        }
    }

    /**
     * Reset the transaction state
     */
    fun resetTransaction() {
        totalInserted = "0.00"
        selectedDrink = null
        changeAmount = "0.00"
        showInvalidCoinMessage = false
        invalidCoinMessage = ""
        dispensedDrink = ""
        insertedCoins.clear()
    }
}
