package edu.kamshanski.rubancfttask.utils

import java.math.BigDecimal

/*
Utils for primitives or close to primitive classes
 */

public fun String.toNonNegativeDouble() : Double {
    return (toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
}

public fun String.toBigDecimalOfZero() : BigDecimal {
    return try {
        val formattedNumber = this
            .replace(",", ".")
            .let { if (!it.contains(".")) it + ".0" else it }
            .let { if (it.last() == '.') it + "0" else it }
        BigDecimal(formattedNumber)
    } catch (nfe: NumberFormatException) {
        BigDecimal.ZERO
    }
}