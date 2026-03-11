package com.jadugar.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.abs

/**
 * CalculatorViewModel
 *
 * Holds all UI state and encapsulates the pricing formula:
 *
 *   Step 1:  profit     = a × (b / 100)
 *   Step 2:  base_price = a + profit
 *   Step 3:  x = base_price / (1 − c/118)
 *
 * Where:
 *   a = costPrice    (user input)
 *   b = profitPct    (slider 10–200)
 *   c = jadooPct     (slider 10–30)
 *   x = finalPrice   (computed output)
 */
class CalculatorViewModel : ViewModel() {

    // ── Inputs ────────────────────────────────────────────────────────────────

    /** Raw text entered by the user in the Cost Price field */
    var costPriceText by mutableStateOf("")
        private set

    /** Profit percentage – slider range 10..200 */
    var profitPct by mutableStateOf(20f)
        private set

    /** Jadoo percentage – slider range 10..30 */
    var jadooPct by mutableStateOf(18f)
        private set

    // ── Outputs ───────────────────────────────────────────────────────────────

    /** Computed final price; null when costPrice is invalid / empty */
    var finalPrice by mutableStateOf<Double?>(null)
        private set

    /** Parsed cost price; null when input is blank or non-numeric */
    var costPrice by mutableStateOf<Double?>(null)
        private set

    /** Validation error message; null when input is valid */
    var inputError by mutableStateOf<String?>(null)
        private set

    // ── UI state ──────────────────────────────────────────────────────────────

    /** Dark / Light mode toggle */
    var isDarkTheme by mutableStateOf(true)
        private set

    // ── Event handlers ────────────────────────────────────────────────────────

    fun onCostPriceChange(text: String) {
        // Allow only valid decimal input
        val filtered = text.filter { it.isDigit() || it == '.' }
        costPriceText = filtered
        validateAndCalculate()
    }

    fun onProfitPctChange(value: Float) {
        profitPct = value
        validateAndCalculate()
    }

    fun onJadooPctChange(value: Float) {
        jadooPct = value
        validateAndCalculate()
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun reset() {
        costPriceText = ""
        profitPct     = 20f
        jadooPct      = 18f
        costPrice     = null
        finalPrice    = null
        inputError    = null
    }

    // ── Core calculation ──────────────────────────────────────────────────────

    private fun validateAndCalculate() {
        if (costPriceText.isBlank()) {
            costPrice  = null
            finalPrice = null
            inputError = null
            return
        }

        val parsed = costPriceText.toDoubleOrNull()
        if (parsed == null) {
            inputError = "Please enter a valid number"
            costPrice  = null
            finalPrice = null
            return
        }
        if (parsed < 0) {
            inputError = "Cost price cannot be negative"
            costPrice  = null
            finalPrice = null
            return
        }

        inputError = null
        costPrice  = parsed
        finalPrice = calculateFinalPrice(parsed, profitPct.toDouble(), jadooPct.toDouble())
    }

    /**
     * Core pricing formula (closed-form algebraic solution):
     *
     *   profit     = a × (b / 100)
     *   base_price = a + profit
     *   x          = base_price / (1 − c/118)
     */
    private fun calculateFinalPrice(a: Double, b: Double, c: Double): Double {
        val profit    = a * (b / 100.0)
        val basePrice = a + profit

        // Denominator guard (should never be zero given c ∈ [10,30])
        val denominator = 1.0 - (c / 118.0)
        if (abs(denominator) < 1e-10) return 0.0

        return basePrice / denominator
    }
}
