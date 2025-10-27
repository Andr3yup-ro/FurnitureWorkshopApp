package com.predandrei.atelier.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyRon {
    private val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("ro", "RO")).apply {
        currency = java.util.Currency.getInstance("RON")
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    fun formatMinorUnits(valueRonMinor: Long): String {
        return format.format(valueRonMinor / 100.0)
    }
}
