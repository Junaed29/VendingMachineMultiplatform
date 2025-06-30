package org.junaed.vending_machine.ui.screens.simulator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * SimRuntimeViewModel - Core state for the VMCS simulator
 *
 * Holds all the state for the simulation without any persistence.
 * All simulator data lives only in this object during runtime.
 */
class SimRuntimeViewModel {

    // Denomination types for coins
    enum class Denom { CENT_10, CENT_20, CENT_50, RM_1 }

    // Drink brands available in the machine
    enum class Brand { COKE, SPRITE, DRINKBOT, PEPSI, FANTA }

    // Main simulation state
    var isRunning by mutableStateOf(false)
        private set

    // Inventory state
    private val _coinCounts = mutableMapOf<Denom, Int>()
    val coinCounts: Map<Denom, Int> get() = _coinCounts

    private val _canCounts = mutableMapOf<Brand, Int>()
    val canCounts: Map<Brand, Int> get() = _canCounts

    // Door and maintenance state
    var doorLocked by mutableStateOf(true)
        private set

    var maintainerLoggedIn by mutableStateOf(false)
        private set

    // Customer transaction state
    var inFlightCustomerMoney by mutableStateOf(0.0)
        private set

    // Event logging
    private val _eventLog = mutableStateListOf<String>()
    val eventLog: List<String> get() = _eventLog

    init {
        initializeInventory()
    }

    private fun initializeInventory() {
        // Initialize coin counts to zero
        Denom.values().forEach { _coinCounts[it] = 0 }

        // Initialize can counts to zero
        Brand.values().forEach { _canCounts[it] = 0 }
    }

    /**
     * Start the simulation
     */
    fun startSimulation() {
        if (isRunning) return
        isRunning = true
        logEvent("Simulation started")
    }

    /**
     * End the simulation and reset all state
     */
    fun reset() {
        initializeInventory()
        doorLocked = true
        maintainerLoggedIn = false
        inFlightCustomerMoney = 0.0
        logEvent("Simulation ended")
        isRunning = false
    }

    /**
     * Update coin count for a specific denomination
     * @return true if the update was successful (within valid range 0-20)
     */
    fun updateCoinCount(denom: Denom, count: Int): Boolean {
        if (count in 0..20) {
            _coinCounts[denom] = count
            logEvent("Coin count updated: $denom = $count")
            return true
        }
        return false
    }

    /**
     * Update can count for a specific brand
     * @return true if the update was successful (within valid range 0-20)
     */
    fun updateCanCount(brand: Brand, count: Int): Boolean {
        if (count in 0..20) {
            _canCounts[brand] = count
            logEvent("Can count updated: $brand = $count")
            return true
        }
        return false
    }

    /**
     * Lock the machine door
     */
    fun lockDoor() {
        if (!doorLocked) {
            doorLocked = true
            logEvent("Door locked")
        }
    }

    /**
     * Unlock the machine door (can only be done through machinery panel)
     */
    fun unlockDoor() {
        if (doorLocked) {
            doorLocked = false
            logEvent("Door unlocked")
        }
    }

    /**
     * Log in the maintainer
     */
    fun loginMaintainer() {
        if (!maintainerLoggedIn) {
            maintainerLoggedIn = true
            logEvent("Maintainer logged in")
        }
    }

    /**
     * Log out the maintainer (only if door is locked)
     * @return true if logout was successful
     */
    fun logoutMaintainer(): Boolean {
        if (maintainerLoggedIn && doorLocked) {
            maintainerLoggedIn = false
            logEvent("Maintainer logged out")
            return true
        }
        return false
    }

    /**
     * Add customer money during purchase
     */
    fun addCustomerMoney(amount: Double) {
        inFlightCustomerMoney += amount
        logEvent("Customer inserted: RM${amount}")
    }

    /**
     * Complete a purchase, deducting money and can
     */
    fun completePurchase(brand: Brand, price: Double): Boolean {
        if (!isRunning) return false
        if (_canCounts[brand] ?: 0 <= 0) return false
        if (inFlightCustomerMoney < price) return false

        _canCounts[brand] = (_canCounts[brand] ?: 0) - 1
        inFlightCustomerMoney -= price
        logEvent("Purchase completed: $brand for RM$price")
        return true
    }

    /**
     * Get total number of coins
     */
    fun getTotalCoins(): Int {
        return _coinCounts.values.sum()
    }

    /**
     * Get total number of cans
     */
    fun getTotalCans(): Int {
        return _canCounts.values.sum()
    }

    /**
     * Get total value of coins
     */
    fun getTotalCoinValue(): Double {
        return (_coinCounts[Denom.CENT_10] ?: 0) * 0.10 +
               (_coinCounts[Denom.CENT_20] ?: 0) * 0.20 +
               (_coinCounts[Denom.CENT_50] ?: 0) * 0.50 +
               (_coinCounts[Denom.RM_1] ?: 0) * 1.0
    }

    /**
     * Add event to the log with timestamp
     */
    private fun logEvent(message: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timeString = "${now.hour}:${now.minute}:${now.second}"
        _eventLog.add("[$timeString] $message")
    }
}
