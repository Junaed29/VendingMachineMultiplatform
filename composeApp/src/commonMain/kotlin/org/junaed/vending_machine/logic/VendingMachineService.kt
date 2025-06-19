package org.junaed.vending_machine.logic

import org.junaed.vending_machine.model.Coin
import kotlin.math.round

/**
 * Service class that handles all vending machine business logic including
 * coin validation, transaction processing, and monetary calculations.
 */
class VendingMachineService {
    private val coinRepository = CoinRepository()

    /**
     * Helper function to format double values to 2 decimal places
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
     * Validates a coin based on its physical properties
     */
    fun validateCoinByPhysicalProperties(
        diameter: Double,
        thickness: Double,
        weight: Double
    ): Coin? {
        return coinRepository.identifyCoin(diameter, thickness, weight)
    }

    /**
     * Validates if a coin meets Malaysian specifications
     */
    fun isValidMalaysianCoin(coin: Coin): Boolean {
        return coinRepository.isValidMalaysianCoin(coin)
    }

    /**
     * Get the reason why a coin was rejected
     */
    fun getCoinRejectionReason(coin: Coin): String? {
        return coinRepository.getCoinRejectionReason(coin)
    }

    /**
     * Calculate total amount from a list of added coins
     */
    fun calculateTotal(insertedCoins: List<Coin>): String {
        val totalSen = insertedCoins.sumOf { it.valueSen }
        val totalRM = totalSen / 100.0
        return formatToTwoDecimalPlaces(totalRM)
    }

    /**
     * Determines if there's enough money to purchase a drink
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
