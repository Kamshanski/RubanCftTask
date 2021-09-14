package edu.kamshanski.rubancfttask.utils

import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

/** Signed hours between dates */
public fun hoursBetween(d1: LocalDateTime, d2: LocalDateTime) : Long {
    val sign = d1.compareTo(d2).toLong()
    return ChronoUnit.HOURS.between(d1, d2) * sign
}

public operator fun LocalDateTime.compareTo(o: LocalDateTime) : Int {
    return when {
        isEqual(o) -> 0
        isAfter(o) -> 1
        else -> -1
    }
}