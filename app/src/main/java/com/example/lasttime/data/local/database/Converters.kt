package com.example.lasttime.data.local.database

import androidx.room.TypeConverter
import java.time.LocalDate

/** Guarda LocalDate como "epoch day" (inteiro), sem fuso horário. */
class Converters {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}
