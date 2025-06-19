package org.junaed.vending_machine.logic

import org.junaed.vending_machine.model.Coin
import kotlin.math.abs

/**
 * Repository class that provides data and validation logic for coins
 */
class CoinRepository {
    companion object {
        // Physical tolerance for coin validation
        const val DIAMETER_TOLERANCE_MM = 0.1
        const val THICKNESS_TOLERANCE_MM = 0.05
        const val WEIGHT_TOLERANCE_G = 0.1

        // Malaysian coins with accurate physical measurements
        val MALAYSIAN_10_SEN = Coin(
            valueSen = 10,
            displayName = "10 sen",
            diameter = 18.5,
            thickness = 1.6,
            weight = 2.98,
            material = "Stainless Steel"
        )

        val MALAYSIAN_20_SEN = Coin(
            valueSen = 20,
            displayName = "20 sen",
            diameter = 20.0,
            thickness = 1.75,
            weight = 4.18,
            material = "Stainless Steel"
        )

        val MALAYSIAN_50_SEN = Coin(
            valueSen = 50,
            displayName = "50 sen",
            diameter = 24.0,
            thickness = 1.8,
            weight = 5.66,
            material = "Nickel Plated Steel"
        )

        val MALAYSIAN_1_RINGGIT = Coin(
            valueSen = 100,
            displayName = "RM1",
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
            diameter = 24.26,
            thickness = 1.75,
            weight = 5.67,
            material = "Cupronickel"
        )

        val US_DIME = Coin(
            valueSen = 10, // 10 cents equivalent
            displayName = "10¢",
            diameter = 17.91,
            thickness = 1.35,
            weight = 2.268,
            material = "Cupronickel"
        )

        val EURO_1 = Coin(
            valueSen = 100, // 1 euro equivalent
            displayName = "€1",
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
    }

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

    /**
     * Get the reason why a coin was rejected
     * @param coin The coin being evaluated
     * @return A string describing why the coin was rejected or null if valid
     */
    fun getCoinRejectionReason(coin: Coin): String? {
        if (isValidMalaysianCoin(coin)) return null

        // Find the closest Malaysian coin based on value
        val closestCoin = MALAYSIAN_COINS.find { it.valueSen == coin.valueSen }
            ?: MALAYSIAN_COINS.minByOrNull { abs(it.valueSen - coin.valueSen) }
            ?: return "Invalid coin specifications"

        // Check which property is out of tolerance
        val diameterOff = abs(coin.diameter - closestCoin.diameter) > DIAMETER_TOLERANCE_MM
        val thicknessOff = abs(coin.thickness - closestCoin.thickness) > THICKNESS_TOLERANCE_MM
        val weightOff = abs(coin.weight - closestCoin.weight) > WEIGHT_TOLERANCE_G

        return when {
            diameterOff && thicknessOff && weightOff -> "Coin doesn't match Malaysian specifications"
            diameterOff -> "Incorrect coin diameter"
            thicknessOff -> "Incorrect coin thickness"
            weightOff -> "Incorrect coin weight"
            else -> "Coin not recognized"
        }
    }
}
