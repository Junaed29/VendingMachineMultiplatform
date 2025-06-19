package org.junaed.vending_machine.ui.utils

import org.junaed.vending_machine.ui.components.Coin
import kotlin.math.abs
import kotlin.math.round

/**
 * Helper class to manage vending machine operations
 */
class VendingMachineHelper {
    companion object {
        /**
         * Helper function to format double values to 2 decimal places
         * This is KMP compatible, unlike String.format which is JVM-specific
         */
        private fun formatToTwoDecimalPlaces(value: Double): String {
            val roundedValue = round(value * 100) / 100
            return buildString {
                append(roundedValue.toInt())
                append('.')
                val fraction = ((roundedValue - roundedValue.toInt()) * 100).toInt()
                if (fraction < 10) {
                    append('0')
                }
                append(fraction)
            }
        }

        /**
         * Validates if the input coin is one of the accepted Malaysian coins
         * @param coinInputText The string input to validate
         * @return A Coin if valid, null otherwise
         */
        fun validateCoinInput(coinInputText: String): Coin? {
            val validCoins = Coin.MALAYSIAN_COINS

            // Try to parse as integer (sen)
            return try {
                val inputValue = coinInputText.toInt()
                validCoins.find { it.valueSen == inputValue }
            } catch (e: NumberFormatException) {
                // If not an integer, try checking if it's a special format like "RM1"
                if (coinInputText.equals("RM1", ignoreCase = true)) {
                    validCoins.find { it.valueSen == 100 }
                } else {
                    null
                }
            }
        }

        /**
         * Validates a coin based on its physical properties (diameter, thickness, weight)
         * @param diameter The measured diameter in millimeters
         * @param thickness The measured thickness in millimeters
         * @param weight The measured weight in grams
         * @return A valid Malaysian coin if measurements match, null otherwise
         */
        fun validateCoinByPhysicalProperties(
            diameter: Double,
            thickness: Double,
            weight: Double
        ): Coin? {
            return Coin.identifyCoin(diameter, thickness, weight)
        }

        /**
         * Validates if a coin meets Malaysian specifications
         * @param coin The coin to validate
         * @return True if the coin is a valid Malaysian coin, false otherwise
         */
        fun isValidMalaysianCoin(coin: Coin): Boolean {
            return Coin.isValidMalaysianCoin(coin)
        }

        /**
         * Get the reason why a coin was rejected
         * @param coin The coin being evaluated
         * @return A string describing why the coin was rejected or null if valid
         */
        fun getCoinRejectionReason(coin: Coin): String? {
            if (isValidMalaysianCoin(coin)) return null

            // Find the closest Malaysian coin based on value
            val closestCoin = Coin.MALAYSIAN_COINS.find { it.valueSen == coin.valueSen }
                ?: Coin.MALAYSIAN_COINS.minByOrNull { abs(it.valueSen - coin.valueSen) }
                ?: return "Invalid coin specifications"

            // Check which property is out of tolerance
            val diameterOff = abs(coin.diameter - closestCoin.diameter) > Coin.DIAMETER_TOLERANCE_MM
            val thicknessOff = abs(coin.thickness - closestCoin.thickness) > Coin.THICKNESS_TOLERANCE_MM
            val weightOff = abs(coin.weight - closestCoin.weight) > Coin.WEIGHT_TOLERANCE_G

            return when {
                diameterOff && thicknessOff && weightOff -> "Coin doesn't match Malaysian specifications"
                diameterOff -> "Incorrect coin diameter"
                thicknessOff -> "Incorrect coin thickness"
                weightOff -> "Incorrect coin weight"
                else -> "Coin not recognized"
            }
        }

        /**
         * Calculate total amount from a list of added coins
         * @param insertedCoins List of coins that have been inserted
         * @return The total amount in Ringgit Malaysia format (e.g., "2.50")
         */
        fun calculateTotal(insertedCoins: List<Coin>): String {
            val totalSen = insertedCoins.sumOf { it.valueSen }
            val totalRM = totalSen / 100.0
            return formatToTwoDecimalPlaces(totalRM)
        }

        /**
         * Determines if there's enough money to purchase a drink
         * @param totalInserted Total amount inserted in RM format (e.g., "2.50")
         * @param drinkPrice Price of the drink in RM format (e.g., "1.50")
         * @return True if there's enough money, false otherwise
         */
        fun hasEnoughMoney(totalInserted: String, drinkPrice: String): Boolean {
            return try {
                val inserted = totalInserted.toDouble()
                val price = drinkPrice.toDouble()
                inserted >= price
            } catch (e: NumberFormatException) {
                false
            }
        }

        /**
         * Calculates change after a purchase
         * @param totalInserted Total amount inserted in RM format
         * @param drinkPrice Price of the drink in RM format
         * @return The change amount in RM format
         */
        fun calculateChange(totalInserted: String, drinkPrice: String): String {
            return try {
                val inserted = totalInserted.toDouble()
                val price = drinkPrice.toDouble()
                val change = inserted - price
                formatToTwoDecimalPlaces(change.coerceAtLeast(0.0))
            } catch (e: NumberFormatException) {
                "0.00"
            }
        }
    }
}
