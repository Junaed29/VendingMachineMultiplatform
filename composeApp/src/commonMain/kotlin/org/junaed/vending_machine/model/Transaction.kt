package org.junaed.vending_machine.model

import kotlinx.serialization.Serializable

/**
 * Data model representing a vending machine transaction
 */
@Serializable
data class Transaction(
    val timestamp: Long,
    val drinkName: String,
    val drinkPrice: String,
    val amountInserted: String,
    val changeGiven: String,
    val coinsInserted: List<Int> = listOf() // List of coin values in sen
)
