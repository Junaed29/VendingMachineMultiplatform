package org.junaed.vending_machine.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.serialization.serializer
import org.junaed.vending_machine.logic.StorageService
import org.junaed.vending_machine.logic.getSettingsFactory
import org.junaed.vending_machine.model.MaintenanceSettings
import org.junaed.vending_machine.model.Transaction

/**
 * ViewModel that manages the maintenance mode operations of the vending machine
 */
class MaintenanceViewModel {
    private val storageService = StorageService(getSettingsFactory().createSettings())

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------
    companion object {
        const val MAINTENANCE_SETTINGS_KEY = "maintenance_settings"
        const val TRANSACTIONS_KEY = "transactions"
        const val AVAILABLE_CHANGE_KEY = "available_change"
        const val PASSWORD_MIN_LENGTH = 6
    }

    //----------------------------------------------------------------------------------------------
    // UI STATE PROPERTIES
    //----------------------------------------------------------------------------------------------
    var isPasswordValid by mutableStateOf(false)
        private set
    var showInvalidPasswordMessage by mutableStateOf(false)
        private set
    var isMaintenanceMode by mutableStateOf(false)
        private set
    var maintenanceMessage by mutableStateOf("")
        private set
    var isDoorUnlocked by mutableStateOf(false)
        private set

    //----------------------------------------------------------------------------------------------
    // DATA STATE PROPERTIES
    //----------------------------------------------------------------------------------------------
    var coinsByDenomination by mutableStateOf<Map<Int, Int>>(mapOf())
        private set
    var drinkStockLevels by mutableStateOf<Map<String, Int>>(mapOf())
        private set
    var drinkPriceSettings by mutableStateOf<Map<String, Double>>(mapOf())
        private set

    //----------------------------------------------------------------------------------------------
    // TEMPORARY EDIT STATE PROPERTIES
    //----------------------------------------------------------------------------------------------
    var tempDrinkStockLevels by mutableStateOf<MutableMap<String, Int>>(mutableMapOf())
        private set
    var tempDrinkPrices by mutableStateOf<MutableMap<String, Double>>(mutableMapOf())
        private set
    var tempCoinLevels by mutableStateOf<MutableMap<Int, Int>>(mutableMapOf())
        private set

    init {
        loadMaintenanceSettings()
        loadCoinInventory()
    }

    //----------------------------------------------------------------------------------------------
    // AUTHENTICATION MANAGEMENT
    //----------------------------------------------------------------------------------------------

    /**
     * Validates the password for maintenance access
     * Password must be exactly 6 alphanumeric characters (Rule R1)
     */
    fun validatePassword(password: String): Boolean {
        val settings = loadMaintenanceSettingsFromStorage()

        // Check if password matches stored admin password and is valid
        val isValid = password == settings.adminPassword && isValidPasswordFormat(password)

        isPasswordValid = isValid
        showInvalidPasswordMessage = !isValid

        if (isValid) {
            enterMaintenanceMode()
        }

        return isValid
    }

    /**
     * Check password format: must be exactly 6 alphanumeric characters
     */
    private fun isValidPasswordFormat(password: String): Boolean {
        val regex = "^[a-zA-Z0-9]{6}$".toRegex()
        return regex.matches(password)
    }

    /**
     * Clear authentication state and exit maintenance mode
     */
    fun clearAuthentication() {
        exitMaintenanceMode()
        isPasswordValid = false
        showInvalidPasswordMessage = false
    }

    //----------------------------------------------------------------------------------------------
    // MAINTENANCE MODE MANAGEMENT
    //----------------------------------------------------------------------------------------------

    /**
     * Enter maintenance mode - activate maintenance mode and unlock door
     */
    private fun enterMaintenanceMode() {
        val settings = loadMaintenanceSettingsFromStorage()

        // Update maintenance settings with active flag
        val updatedSettings = settings.copy(
            isMaintenanceActive = true,
            lastMaintenanceDate = Clock.System.now().toEpochMilliseconds()
        )

        // Save to storage
        storageService.saveObject(
            MAINTENANCE_SETTINGS_KEY,
            updatedSettings,
            serializer()
        )

        // Update UI state
        isMaintenanceMode = true
        maintenanceMessage = "Maintenance Mode Active"
        isDoorUnlocked = true

        // Load data for maintenance operations
        loadDrinkData()
        loadCoinData()
    }

    /**
     * Exit maintenance mode - return to normal operation
     */
    fun exitMaintenanceMode() {
        val settings = loadMaintenanceSettingsFromStorage()

        // Update maintenance settings, turning off maintenance mode
        val updatedSettings = settings.copy(
            isMaintenanceActive = false
        )

        // Save to storage
        storageService.saveObject(
            MAINTENANCE_SETTINGS_KEY,
            updatedSettings,
            serializer()
        )

        // Update UI state
        isMaintenanceMode = false
        maintenanceMessage = ""
        isDoorUnlocked = false
    }

    /**
     * Record maintenance actions before exiting
     */
    fun recordMaintenance() {
        // Save all changes before exiting
        saveChanges()

        // Log the maintenance action
        logMaintenanceAction("Maintenance completed")

        // Exit maintenance mode
        exitMaintenanceMode()
    }

    //----------------------------------------------------------------------------------------------
    // DATA LOADING METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Load maintenance settings from storage
     */
    private fun loadMaintenanceSettings() {
        val settings = loadMaintenanceSettingsFromStorage()

        // Update local state
        isMaintenanceMode = settings.isMaintenanceActive
        drinkStockLevels = settings.drinkStockLevels
        drinkPriceSettings = settings.priceSettings

        // Initialize temp maps for editing
        tempDrinkStockLevels = settings.drinkStockLevels.toMutableMap()
        tempDrinkPrices = settings.priceSettings.toMutableMap()
    }

    /**
     * Load settings from storage, with defaults if not found
     */
    private fun loadMaintenanceSettingsFromStorage(): MaintenanceSettings {
        return storageService.getObject(
            MAINTENANCE_SETTINGS_KEY,
            serializer<MaintenanceSettings>(),
            MaintenanceSettings()
        ) ?: MaintenanceSettings()
    }

    /**
     * Load coin inventory from storage
     */
    private fun loadCoinInventory() {
        coinsByDenomination = storageService.getObject(
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

        // Initialize temp coin levels for editing
        tempCoinLevels = coinsByDenomination.toMutableMap()
    }

    /**
     * Load drink data from storage
     */
    private fun loadDrinkData() {
        val settings = loadMaintenanceSettingsFromStorage()

        // Initialize with default values if not set
        val defaultStock = mapOf(
            "BRAND 1" to 10,
            "BRAND 2" to 10,
            "BRAND 3" to 10,
            "BRAND 4" to 10,
            "BRAND 5" to 10
        )

        val defaultPrices = mapOf(
            "BRAND 1" to 0.70,
            "BRAND 2" to 0.70,
            "BRAND 3" to 0.70,
            "BRAND 4" to 0.60,
            "BRAND 5" to 0.60
        )

        // Use stored values or defaults
        drinkStockLevels = if (settings.drinkStockLevels.isEmpty()) defaultStock else settings.drinkStockLevels
        drinkPriceSettings = if (settings.priceSettings.isEmpty()) defaultPrices else settings.priceSettings

        // Initialize temp maps for editing
        tempDrinkStockLevels = drinkStockLevels.toMutableMap()
        tempDrinkPrices = drinkPriceSettings.toMutableMap()
    }

    /**
     * Load coin data from storage
     */
    private fun loadCoinData() {
        coinsByDenomination = storageService.getObject(
            AVAILABLE_CHANGE_KEY,
            serializer<Map<Int, Int>>(),
            mapOf(
                10 to 20,
                20 to 20,
                50 to 20,
                100 to 10
            )
        ) ?: mapOf(
            10 to 20,
            20 to 20,
            50 to 20,
            100 to 10
        )

        // Initialize temp coin levels for editing
        tempCoinLevels = coinsByDenomination.toMutableMap()
    }

    //----------------------------------------------------------------------------------------------
    // INVENTORY MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Update drink price
     */
    fun updateDrinkPrice(drinkName: String, newPrice: Double): Boolean {
        // Validate price is positive
        if (newPrice <= 0) {
            maintenanceMessage = "Price must be greater than zero"
            return false
        }

        // Update temporary map
        tempDrinkPrices[drinkName] = newPrice
        return true
    }

    /**
     * Update drink stock quantity
     */
    fun updateDrinkStock(drinkName: String, newQuantity: Int): Boolean {
        // Validate using Rule R2: Integer between 0 and 20
        if (newQuantity < 0 || newQuantity > 20) {
            maintenanceMessage = "Drink quantity must be between 0 and 20"
            return false
        }

        // Update temporary map
        tempDrinkStockLevels[drinkName] = newQuantity
        return true
    }

    /**
     * Update coin quantity
     */
    fun updateCoinQuantity(denomination: Int, newQuantity: Int): Boolean {
        // Validate using Rule R3: Integer between 0 and 20
        if (newQuantity < 0 || newQuantity > 20) {
            maintenanceMessage = "Coin quantity must be between 0 and 20"
            return false
        }

        // Update temporary map
        tempCoinLevels[denomination] = newQuantity
        return true
    }

    //----------------------------------------------------------------------------------------------
    // CASH MANAGEMENT METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Get the total value of all coins in the machine
     */
    fun calculateTotalCoinValue(): Double {
        var totalValueInSen = 0

        coinsByDenomination.forEach { (denomination, count) ->
            totalValueInSen += denomination * count
        }

        return totalValueInSen / 100.0
    }

    /**
     * Collect all coins (reset coin storage to zero)
     */
    fun collectAllCoins(): Double {
        val totalValue = calculateTotalCoinValue()

        // Reset coin inventory
        val emptyCoinInventory = coinsByDenomination.keys.associateWith { 0 }

        // Save to storage
        storageService.saveObject(
            AVAILABLE_CHANGE_KEY,
            emptyCoinInventory,
            serializer()
        )

        // Update local state
        coinsByDenomination = emptyCoinInventory
        tempCoinLevels = emptyCoinInventory.toMutableMap()

        // Log the maintenance action
        logMaintenanceAction("Collected all coins: RM $totalValue")

        return totalValue
    }

    /**
     * Get the total value of coins in the machine
     */
    fun getTotalCash(): Double {
        return calculateTotalCoinValue()
    }

    /**
     * Collect all cash from the machine
     */
    fun collectCash(): Double {
        return collectAllCoins()
    }

    //----------------------------------------------------------------------------------------------
    // DATA PERSISTENCE METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Save all changes to storage
     */
    fun saveChanges(): Boolean {
        val settings = loadMaintenanceSettingsFromStorage()

        // Update settings with new values
        val updatedSettings = settings.copy(
            drinkStockLevels = tempDrinkStockLevels,
            priceSettings = tempDrinkPrices
        )

        // Save settings
        storageService.saveObject(
            MAINTENANCE_SETTINGS_KEY,
            updatedSettings,
            serializer()
        )

        // Save coin inventory
        storageService.saveObject(
            AVAILABLE_CHANGE_KEY,
            tempCoinLevels,
            serializer()
        )

        // Update local state
        drinkStockLevels = tempDrinkStockLevels.toMap()
        drinkPriceSettings = tempDrinkPrices.toMap()
        coinsByDenomination = tempCoinLevels.toMap()

        // Log the maintenance action
        logMaintenanceAction("Updated inventory and prices")

        maintenanceMessage = "Changes saved successfully"
        return true
    }

    /**
     * Log maintenance actions
     */
    private fun logMaintenanceAction(action: String) {
        val maintenanceLog = Transaction(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            drinkName = "MAINTENANCE",
            drinkPrice = "0.00",
            amountInserted = "0.00",
            changeGiven = "0.00",
            coinsInserted = listOf(),
            maintenanceAction = action
        )

        // Get existing transactions
        val transactions = storageService.getObject(
            TRANSACTIONS_KEY,
            serializer<List<Transaction>>(),
            listOf()
        ) ?: listOf()

        // Add maintenance log to transactions
        val updatedTransactions = transactions + maintenanceLog

        // Save updated transactions
        storageService.saveObject(
            TRANSACTIONS_KEY,
            updatedTransactions,
            serializer()
        )
    }

    //----------------------------------------------------------------------------------------------
    // UI STATE MANAGEMENT
    //----------------------------------------------------------------------------------------------

    /**
     * Clear any error messages
     */
    fun clearMessage() {
        maintenanceMessage = ""
        showInvalidPasswordMessage = false
    }
}
