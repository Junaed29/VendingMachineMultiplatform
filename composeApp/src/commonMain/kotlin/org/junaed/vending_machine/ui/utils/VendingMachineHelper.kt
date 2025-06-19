package org.junaed.vending_machine.ui.utils

import org.junaed.vending_machine.ui.components.MalaysianCoin
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
         * @return A MalaysianCoin if valid, null otherwise
         */
        fun validateCoinInput(coinInputText: String): MalaysianCoin? {
            val validCoins = MalaysianCoin.AVAILABLE_COINS

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
         * Calculate total amount from a list of added coins
         * @param insertedCoins List of coins that have been inserted
         * @return The total amount in Ringgit Malaysia format (e.g., "2.50")
         */
        fun calculateTotal(insertedCoins: List<MalaysianCoin>): String {
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
