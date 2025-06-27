package org.junaed.vending_machine.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import org.junaed.vending_machine.logic.CoinRepository
import org.junaed.vending_machine.logic.getSettingsFactory
import org.junaed.vending_machine.logic.StorageService
import org.junaed.vending_machine.logic.VendingMachineService
import org.junaed.vending_machine.model.Coin
import org.junaed.vending_machine.model.DrinkItem
import org.junaed.vending_machine.model.MaintenanceSettings
import org.junaed.vending_machine.model.Transaction
import org.junaed.vending_machine.ui.theme.VendingMachineColors
import kotlinx.serialization.serializer

/**
 * ViewModel that manages the state and business logic for the vending machine screen
 */
class VendingMachineViewModel {
    private val vendingMachineService = VendingMachineService()
    private val coinRepository = CoinRepository()
    private val storageService = StorageService(getSettingsFactory().createSettings())

    // Constants for storage keys
    companion object {
        const val MAINTENANCE_SETTINGS_KEY = "maintenance_settings"
        const val TRANSACTIONS_KEY = "transactions"
        const val DRINK_INVENTORY_KEY = "drink_inventory"
        const val AVAILABLE_CHANGE_KEY = "available_change"
    }

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
    var showNoChangeMessage by mutableStateOf(false)
        private set
    var dispensedDrink by mutableStateOf("")
        private set
    var uiMessage by mutableStateOf("")
        private set
    var isMaintenanceMode by mutableStateOf(false)
        private set
    var showChangeNotAvailableDialog by mutableStateOf(false)
        private set
    var showMaintenanceDialog by mutableStateOf(false)
        private set
    var isTransactionActive by mutableStateOf(false)
        private set

    // Collection of inserted coins
    val insertedCoins = mutableStateListOf<Coin>()

    // Live drink inventory with stock status
    var availableDrinks = listOf<DrinkItem>()
        private set

    // Cached inventory data
    private var drinkInventory = mapOf<String, Int>()
    private var availableChange = mapOf<Int, Int>()

    init {
        loadMaintenanceStatus()
        loadDrinkInventory()
        loadAvailableChange()
    }

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
     * Check if the system is in maintenance mode
     */
    private fun loadMaintenanceStatus() {
        val settings = storageService.getObject(
            MAINTENANCE_SETTINGS_KEY,
            serializer<MaintenanceSettings>(),
            MaintenanceSettings()
        ) ?: MaintenanceSettings()

        isMaintenanceMode = settings.isMaintenanceActive
    }

    /**
     * Load drink inventory from storage
     */
    private fun loadDrinkInventory() {
        val settings = storageService.getObject(
            MAINTENANCE_SETTINGS_KEY,
            serializer<MaintenanceSettings>(),
            MaintenanceSettings()
        ) ?: MaintenanceSettings()

        drinkInventory = settings.drinkStockLevels

        // If no stock levels are defined, set default values
        if (drinkInventory.isEmpty()) {
            drinkInventory = mapOf(
                "BRAND 1" to 10,
                "BRAND 2" to 10,
                "BRAND 3" to 10,
                "BRAND 4" to 10,
                "BRAND 5" to 10
            )
        }

        // Create drink items with accurate stock status and prices from settings
        availableDrinks = listOf(
            DrinkItem(
                name = "BRAND 1",
                price = settings.priceSettings["BRAND 1"]?.toString() ?: "0.70",
                inStock = drinkInventory["BRAND 1"] ?: 0 > 0
            ),
            DrinkItem(
                name = "BRAND 2",
                price = settings.priceSettings["BRAND 2"]?.toString() ?: "0.70",
                inStock = drinkInventory["BRAND 2"] ?: 0 > 0
            ),
            DrinkItem(
                name = "BRAND 3",
                price = settings.priceSettings["BRAND 3"]?.toString() ?: "0.70",
                inStock = drinkInventory["BRAND 3"] ?: 0 > 0
            ),
            DrinkItem(
                name = "BRAND 4",
                price = settings.priceSettings["BRAND 4"]?.toString() ?: "0.60",
                inStock = drinkInventory["BRAND 4"] ?: 0 > 0
            ),
            DrinkItem(
                name = "BRAND 5",
                price = settings.priceSettings["BRAND 5"]?.toString() ?: "0.60",
                inStock = drinkInventory["BRAND 5"] ?: 0 > 0
            )
        )
    }

    /**
     * Load available change from storage
     */
    private fun loadAvailableChange() {
        availableChange = storageService.getObject(
            AVAILABLE_CHANGE_KEY,
            serializer<Map<Int, Int>>(),
            mapOf(
                10 to 20,  // 20 coins of 10 sen
                20 to 20,  // 20 coins of 20 sen
                50 to 20,  // 20 coins of 50 sen
                100 to 10  // 10 coins of 1 RM
            )
        ) ?: mapOf(
            10 to 20,
            20 to 20,
            50 to 20,
            100 to 10
        )
    }

    /**
     * Check for maintenance mode before any customer action
     * Returns true if operation should continue, false if blocked
     */
    private fun checkMaintenanceMode(): Boolean {
        // Refresh maintenance status
        loadMaintenanceStatus()

        if (isMaintenanceMode) {
            uiMessage = "System under maintenance. Please try again later."
            return false
        }

        return true
    }

    /**
     * Show maintenance login dialog
     */
    fun enterMaintenanceMode() {
        // If there's an active transaction, cancel it first
        if (insertedCoins.isNotEmpty()) {
            returnCash()
        }

        showMaintenanceDialog = true
    }

    /**
     * Process a coin insertion attempt
     */
    fun insertCoin(coin: Coin) {
        // Block operation if in maintenance mode
        if (!checkMaintenanceMode()) return

        // Require drink selection before accepting coins
        if (selectedDrink == null) {
            uiMessage = "Please select a drink first before inserting coins"
            return
        }

        if (vendingMachineService.isValidMalaysianCoin(coin)) {
            // Valid Malaysian coin
            insertedCoins.add(coin)
            totalInserted = vendingMachineService.calculateTotal(insertedCoins)
            uiMessage = ""
            showInvalidCoinMessage = false

            // If a drink is selected, check if we have enough money now
            selectedDrink?.let { drink ->
                if (vendingMachineService.hasEnoughMoney(totalInserted, drink.price)) {
                    uiMessage = "You have inserted enough money for ${drink.name}"
                } else {
                    val remainingAmount = vendingMachineService.calculateChange(drink.price, totalInserted)
                    uiMessage = "Please insert RM $remainingAmount more for ${drink.name}"
                }
            }
        } else {
            // Foreign or invalid coin
            val reason = vendingMachineService.getCoinRejectionReason(coin)
            invalidCoinMessage = reason ?: "Invalid coin"
            showInvalidCoinMessage = true
            uiMessage = "Invalid coin. Please insert a valid Malaysian coin."
        }
    }

    /**
     * Check if a drink is in stock
     */
    private fun isDrinkInStock(drinkName: String): Boolean {
        // Refresh inventory data first
        loadDrinkInventory()
        return drinkInventory[drinkName] ?: 0 > 0
    }

    /**
     * Process drink selection
     */
    fun selectDrink(drink: DrinkItem) {
        // Block operation if in maintenance mode
        if (!checkMaintenanceMode()) return

        // If a transaction is already active with a different drink selected,
        // don't allow changing the selection
        if (isTransactionActive && selectedDrink != null && selectedDrink?.name != drink.name) {
            uiMessage = "Complete current transaction first or terminate it"
            return
        }

        // Check if the drink is in stock
        if (!isDrinkInStock(drink.name)) {
            uiMessage = "Drink not in stock"
            return
        }

        selectedDrink = drink
        isTransactionActive = true

        // If user has already inserted money, check if it's enough
        if (insertedCoins.isNotEmpty()) {
            if (vendingMachineService.hasEnoughMoney(totalInserted, drink.price)) {
                uiMessage = "You have inserted enough money for ${drink.name}"
            } else {
                val remainingAmount = vendingMachineService.calculateChange(drink.price, totalInserted)
                uiMessage = "Please insert RM $remainingAmount more"
            }
        } else {
            uiMessage = "Selected ${drink.name}. Please insert RM ${drink.price}"
        }
    }

    /**
     * Check if there's enough change available for a transaction
     */
    private fun isChangeAvailable(changeAmount: Double): Boolean {
        // Refresh change data
        loadAvailableChange()

        // Simplified change availability check
        // In a real implementation, this would consider the denominations needed

        // For now, just check if we have any coins for change
        return availableChange.values.sum() > 0
    }

    /**
     * Complete the purchase transaction
     */
    fun completePurchase() {
        // Block operation if in maintenance mode
        if (!checkMaintenanceMode()) return

        val drink = selectedDrink ?: return

        if (!vendingMachineService.hasEnoughMoney(totalInserted, drink.price)) {
            val remainingAmount = vendingMachineService.calculateChange(drink.price, totalInserted)
            uiMessage = "Please insert RM $remainingAmount more"
            return
        }

        // Calculate change amount
        val change = vendingMachineService.calculateChange(totalInserted, drink.price)
        val changeDouble = change.toDoubleOrNull() ?: 0.0

        // Check if change is needed and available
        if (changeDouble > 0.0 && !isChangeAvailable(changeDouble)) {
            showChangeNotAvailableDialog = true
            return
        }

        // Process the purchase
        dispensedDrink = drink.name
        changeAmount = change

        // Update inventory
        updateDrinkInventory(drink.name)
        updateCoinInventory()

        // Log the transaction
        logTransaction(drink)

        // Reset transaction state
        totalInserted = "0.00"
        uiMessage = "Enjoy your ${drink.name}!"
        insertedCoins.clear()
    }

    /**
     * Proceed with purchase when change is not available
     */
    fun proceedWithoutChange() {
        // Block operation if in maintenance mode
        if (!checkMaintenanceMode()) return

        val drink = selectedDrink ?: return

        // Process the purchase without giving change
        dispensedDrink = drink.name
        changeAmount = "0.00"

        // Update inventory
        updateDrinkInventory(drink.name)
        updateCoinInventory()

        // Log the transaction (note: customer didn't receive change)
        logTransaction(drink, changeGiven = "0.00")

        // Reset transaction state
        totalInserted = "0.00"
        uiMessage = "Enjoy your ${drink.name}!"
        showChangeNotAvailableDialog = false
        insertedCoins.clear()
    }

    /**
     * Update drink inventory after purchase
     */
    private fun updateDrinkInventory(drinkName: String) {
        val currentStock = drinkInventory[drinkName] ?: 0
        if (currentStock > 0) {
            val updatedInventory = drinkInventory.toMutableMap()
            updatedInventory[drinkName] = currentStock - 1
            drinkInventory = updatedInventory

            // Update maintenance settings
            val settings = storageService.getObject(
                MAINTENANCE_SETTINGS_KEY,
                serializer<MaintenanceSettings>(),
                MaintenanceSettings()
            ) ?: MaintenanceSettings()

            val updatedSettings = settings.copy(
                drinkStockLevels = updatedInventory
            )

            storageService.saveObject(
                MAINTENANCE_SETTINGS_KEY,
                updatedSettings,
                serializer()
            )

            // Refresh the available drinks list
            loadDrinkInventory()
        }
    }

    /**
     * Update coin inventory after a transaction
     */
    private fun updateCoinInventory() {
        // Load latest coin data
        loadAvailableChange()

        // Add inserted coins to inventory
        val updatedChange = availableChange.toMutableMap()

        for (coin in insertedCoins) {
            val count = updatedChange[coin.valueSen] ?: 0
            updatedChange[coin.valueSen] = count + 1
        }

        // Deduct change given (simplified)
        // In a real implementation, this would calculate the specific coins used for change

        availableChange = updatedChange

        // Save updated change inventory
        storageService.saveObject(
            AVAILABLE_CHANGE_KEY,
            availableChange,
            serializer()
        )
    }

    /**
     * Log a completed transaction
     */
    private fun logTransaction(drink: DrinkItem, changeGiven: String = changeAmount) {
        val transaction = Transaction(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            drinkName = drink.name,
            drinkPrice = drink.price,
            amountInserted = totalInserted,
            changeGiven = changeGiven,
            coinsInserted = insertedCoins.map { it.valueSen }
        )

        // Get existing transactions
        val transactions = storageService.getObject(
            TRANSACTIONS_KEY,
            serializer<List<Transaction>>(),
            listOf()
        ) ?: listOf()

        // Add new transaction to the list
        val updatedTransactions = transactions + transaction

        // Save updated transactions
        storageService.saveObject(
            TRANSACTIONS_KEY,
            updatedTransactions,
            serializer()
        )
    }

    /**
     * Return inserted cash and terminate transaction
     */
    fun returnCash() {
        // Block operation if in maintenance mode, unless called as part of entering maintenance mode
        if (isMaintenanceMode && !showMaintenanceDialog) {
            uiMessage = "System under maintenance. Please try again later."
            return
        }

        if (insertedCoins.isNotEmpty()) {
            changeAmount = totalInserted
            uiMessage = "Returning RM $totalInserted"
            totalInserted = "0.00"
            insertedCoins.clear()
        }

        // Reset transaction state entirely
        selectedDrink = null
        isTransactionActive = false
    }

    /**
     * Terminate the current transaction
     * This returns the customer's money and cancels the transaction without logging any records
     */
    fun terminateTransaction() {
        // Return any inserted money
        if (insertedCoins.isNotEmpty()) {
            changeAmount = totalInserted
            uiMessage = "Transaction terminated. Returning RM $totalInserted"
        } else {
            uiMessage = "Transaction terminated"
        }

        // Reset all transaction state
        totalInserted = "0.00"
        selectedDrink = null
        insertedCoins.clear()
        isTransactionActive = false
        showInvalidCoinMessage = false
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
        uiMessage = ""
        showChangeNotAvailableDialog = false
        insertedCoins.clear()
    }

    /**
     * Close any active dialogs
     */
    fun closeDialog() {
        showChangeNotAvailableDialog = false
        showMaintenanceDialog = false
    }

    /**
     * Refresh data from storage (called after maintenance changes)
     */
    fun refreshData() {
        loadMaintenanceStatus()
        loadDrinkInventory()
        loadAvailableChange()
    }
}
