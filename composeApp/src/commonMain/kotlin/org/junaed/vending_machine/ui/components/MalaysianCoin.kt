package org.junaed.vending_machine.ui.components

/**
 * Represents a Malaysian coin with its value and display name
 * @param valueSen The value in sen (e.g., 10, 20, 50, 100)
 * @param displayName How the coin should be displayed (e.g., "10 sen", "RM1")
 * @param color The color representation of the coin
 */
data class MalaysianCoin(
    val valueSen: Int,
    val displayName: String,
    val colorHex: String
) {
    companion object {
        val AVAILABLE_COINS = listOf(
            MalaysianCoin(10, "10 sen", "#C0C0C0"), // Silver
            MalaysianCoin(20, "20 sen", "#C0C0C0"), // Silver
            MalaysianCoin(50, "50 sen", "#C0C0C0"), // Silver
            MalaysianCoin(100, "RM1", "#E6BE8A")    // Gold-bronze
        )
    }

    /**
     * Converts the coin value to the actual money value in RM
     */
    fun toRinggit(): Double = valueSen / 100.0
}
