package com.example.lasttime.presentation.components

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val brFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun LocalDate.toBr(): String = format(brFormatter)

// O DatePicker do Material 3 trabalha com milissegundos em UTC.
fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.toLocalDateFromUtcMillis(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

fun daysLabel(days: Long): String = when (days) {
    0L -> "Hoje"
    1L -> "Há 1 dia"
    else -> "Há $days dias"
}
