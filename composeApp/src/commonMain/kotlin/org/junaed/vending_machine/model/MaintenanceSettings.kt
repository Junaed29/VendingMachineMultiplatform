package org.junaed.vending_machine.model

import kotlinx.serialization.Serializable

/**
 * Data model for storing vending machine maintenance settings
 * Uses kotlinx.serialization to enable JSON storage with multiplatform-settings
 */
@Serializable
data class MaintenanceSettings(
    val adminPassword: String = "admin123", // Default admin password
    val drinkStockLevels: Map<String, Int> = mapOf(), // Stock levels for drinks
    val priceSettings: Map<String, Double> = mapOf(), // Price settings for products
    val lastMaintenanceDate: Long = 0 // Timestamp of last maintenance
)
