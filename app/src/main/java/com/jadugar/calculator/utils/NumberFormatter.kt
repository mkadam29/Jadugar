package com.jadugar.calculator.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility functions for formatting numbers in the Jadugar UI.
 */
object NumberFormatter {

    private val currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    private val intFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    /**
     * Formats a Double as a price string with 2 decimal places.
     * e.g. 12345.6789 → "12,345.68"
     */
    fun formatPrice(value: Double): String = currencyFormat.format(value)

    /**
     * Formats a Double as an integer string (no decimals).
     * e.g. 1234.0 → "1,234"
     */
    fun formatInteger(value: Double): String = intFormat.format(value)

    /**
     * Formats a Float slider value as a percentage string.
     * e.g. 25.0f → "25%"
     */
    fun formatPercent(value: Float): String = "${value.toInt()}%"
}
