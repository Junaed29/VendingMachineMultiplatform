package org.junaed.vending_machine.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.ExperimentalSerializationApi
import org.junaed.vending_machine.logic.getSettingsFactory
import org.junaed.vending_machine.model.MaintenanceSettings
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * ViewModel for the Maintenance Screen
 * Handles storage and retrieval of maintenance settings using multiplatform-settings
 */
@OptIn(ExperimentalSettingsApi::class, ExperimentalSerializationApi::class, ExperimentalTime::class)
class MaintenanceViewModel {
    // Use the settings directly instead of through StorageService
    private val settings: Settings = getSettingsFactory().createSettings()
    private val settingsKey = "maintenance_settings"

    // UI state variables
    var isAuthenticated by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Current settings (loaded from storage)
    var currentSettings by mutableStateOf(loadSettings())
        private set

    /**
     * Load settings from persistent storage using the serialization extension
     */
    private fun loadSettings(): MaintenanceSettings {
        return settings.decodeValueOrNull(MaintenanceSettings.serializer(), settingsKey) ?: MaintenanceSettings()
    }

    /**
     * Save current settings to persistent storage using the serialization extension
     */
    fun saveSettings() {
        settings.encodeValue(MaintenanceSettings.serializer(), settingsKey, currentSettings)
    }

    /**
     * Attempt to authenticate with the given password
     */
    fun authenticate(password: String): Boolean {
        val isValid = password == currentSettings.adminPassword
        isAuthenticated = isValid
        errorMessage = if (isValid) null else "Invalid password"
        return isValid
    }

    /**
     * Update drink stock level
     */
    fun updateStockLevel(drinkName: String, stockLevel: Int) {
        val updatedStockLevels = currentSettings.drinkStockLevels.toMutableMap()
        updatedStockLevels[drinkName] = stockLevel

        currentSettings = currentSettings.copy(
            drinkStockLevels = updatedStockLevels
        )

        saveSettings()
    }

    /**
     * Update drink price
     */
    fun updateDrinkPrice(drinkName: String, price: Double) {
        val updatedPrices = currentSettings.priceSettings.toMutableMap()
        updatedPrices[drinkName] = price

        currentSettings = currentSettings.copy(
            priceSettings = updatedPrices
        )

        saveSettings()
    }

    /**
     * Update admin password
     */
    fun updateAdminPassword(newPassword: String) {
        currentSettings = currentSettings.copy(
            adminPassword = newPassword
        )

        saveSettings()
    }

    /**
     * Record maintenance performed
     */
    fun recordMaintenance() {
        currentSettings = currentSettings.copy(
            // Use kotlinx.datetime.Clock instead of System.currentTimeMillis()
            lastMaintenanceDate = Clock.System.now().toEpochMilliseconds()
        )

        saveSettings()
    }

    /**
     * Clear authentication state when leaving screen
     */
    fun clearAuthentication() {
        isAuthenticated = false
    }
}
