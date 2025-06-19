package org.junaed.vending_machine.model

/**
 * Data model representing a coin with its physical properties
 */
data class Coin(
    val valueSen: Int,
    val displayName: String,
    val diameter: Double,    // in millimeters
    val thickness: Double,   // in millimeters
    val weight: Double,      // in grams
    val material: String = ""
) {
    /**
     * Converts the coin value to the actual money value in RM
     */
    fun toRinggit(): Double = valueSen / 100.0
}
