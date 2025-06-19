package org.junaed.vending_machine.ui.components

import kotlin.math.abs

/**
 * Represents a coin with its value, display name, and physical properties
 * @param valueSen The value in sen (e.g., 10, 20, 50, 100)
 * @param displayName How the coin should be displayed (e.g., "10 sen", "RM1")
 * @param colorHex The color representation of the coin for UI display
 * @param diameter The diameter of the coin in millimeters
 * @param thickness The thickness of the coin in millimeters
 * @param weight The weight of the coin in grams
 * @param material Optional description of the coin's material
 */
data class Coin(
    val valueSen: Int,
    val displayName: String,
    val colorHex: String,
    val diameter: Double,
    val thickness: Double,
    val weight: Double,
    val material: String = ""
) {
    companion object {
        // Physical tolerance for coin validation
        const val DIAMETER_TOLERANCE_MM = 0.1
        const val THICKNESS_TOLERANCE_MM = 0.05
        const val WEIGHT_TOLERANCE_G = 0.1

        // Malaysian coins with accurate physical measurements
        val MALAYSIAN_10_SEN = Coin(
            valueSen = 10,
            displayName = "10 sen",
            colorHex = "#C0C0C0",
            diameter = 18.5,
            thickness = 1.6,
            weight = 2.98,
            material = "Stainless Steel"
        )

        val MALAYSIAN_20_SEN = Coin(
            valueSen = 20,
            displayName = "20 sen",
            colorHex = "#C0C0C0",
            diameter = 20.0,
            thickness = 1.75,
            weight = 4.18,
            material = "Stainless Steel"
        )

        val MALAYSIAN_50_SEN = Coin(
            valueSen = 50,
            displayName = "50 sen",
            colorHex = "#C0C0C0",
            diameter = 24.0,
            thickness = 1.8,
            weight = 5.66,
            material = "Nickel Plated Steel"
        )

        val MALAYSIAN_1_RINGGIT = Coin(
            valueSen = 100,
            displayName = "RM1",
            colorHex = "#E6BE8A",
            diameter = 24.0,
            thickness = 2.0,
            weight = 7.55,
            material = "Nickel Brass"
        )

        // List of officially accepted Malaysian coins
        val MALAYSIAN_COINS = listOf(
            MALAYSIAN_10_SEN,
            MALAYSIAN_20_SEN,
            MALAYSIAN_50_SEN,
            MALAYSIAN_1_RINGGIT
        )

        // Foreign coins for testing rejection logic
        val US_QUARTER = Coin(
            valueSen = 25, // 25 cents equivalent
            displayName = "25¢",
            colorHex = "#C0C0C0",
            diameter = 24.26,
            thickness = 1.75,
            weight = 5.67,
            material = "Cupronickel"
        )

        val US_DIME = Coin(
            valueSen = 10, // 10 cents equivalent
            displayName = "10¢",
            colorHex = "#C0C0C0",
            diameter = 17.91,
            thickness = 1.35,
            weight = 2.268,
            material = "Cupronickel"
        )

        val EURO_1 = Coin(
            valueSen = 100, // 1 euro equivalent
            displayName = "€1",
            colorHex = "#DCB950",
            diameter = 23.25,
            thickness = 2.33,
            weight = 7.5,
            material = "Bi-metallic"
        )

        // For UI display/testing purposes - available foreign coins
        val FOREIGN_COINS = listOf(
            US_QUARTER,
            US_DIME,
            EURO_1
        )

        /**
         * Validates if a coin matches Malaysian coin specifications within tolerance
         * @param coin The coin to validate
         * @return True if the coin matches a Malaysian coin's physical properties
         */
        fun isValidMalaysianCoin(coin: Coin): Boolean {
            return MALAYSIAN_COINS.any { malaysianCoin ->
                // Check if all physical attributes match within tolerance
                abs(coin.diameter - malaysianCoin.diameter) <= DIAMETER_TOLERANCE_MM &&
                abs(coin.thickness - malaysianCoin.thickness) <= THICKNESS_TOLERANCE_MM &&
                abs(coin.weight - malaysianCoin.weight) <= WEIGHT_TOLERANCE_G
            }
        }

        /**
         * Attempts to identify a Malaysian coin based on physical attributes
         * @param diameter The measured diameter in millimeters
         * @param thickness The measured thickness in millimeters
         * @param weight The measured weight in grams
         * @return The identified Malaysian coin or null if no match
         */
        fun identifyCoin(diameter: Double, thickness: Double, weight: Double): Coin? {
            return MALAYSIAN_COINS.find { coin ->
                abs(diameter - coin.diameter) <= DIAMETER_TOLERANCE_MM &&
                abs(thickness - coin.thickness) <= THICKNESS_TOLERANCE_MM &&
                abs(weight - coin.weight) <= WEIGHT_TOLERANCE_G
            }
        }
    }

    /**
     * Converts the coin value to the actual money value in RM
     */
    fun toRinggit(): Double = valueSen / 100.0
}
