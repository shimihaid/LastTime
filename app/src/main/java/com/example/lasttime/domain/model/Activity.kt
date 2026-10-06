package com.example.lasttime.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Activity(
    val id: Long,
    val name: String,
    val lastPerformedAt: LocalDate,
    val daysSinceLastPerformed: Long
)

