package com.predandrei.atelier.util

import java.math.BigDecimal
import java.math.RoundingMode

object MoneyParser {
    /**
     * Parse a human-entered decimal amount (e.g. "12.50" or "12,50") into minor units (Long bani).
     * Non-numeric characters except one decimal separator are ignored.
     */
    fun toMinorUnits(input: String): Long {
        if (input.isBlank()) return 0L
        val normalized = input.trim().replace(" ", "").replace(',', '.')
        return try {
            val bd = BigDecimal(normalized)
                .setScale(2, RoundingMode.HALF_UP)
            bd.movePointRight(2).longValueExact()
        } catch (e: Exception) {
            0L
        }
    }
}
