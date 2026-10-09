package com.example.lasttime.data.repository

import com.example.lasttime.data.remote.CalendarApiService
import com.example.lasttime.data.remote.HolidayDto
import com.example.lasttime.domain.model.ExternalDataException
import com.example.lasttime.domain.model.Holiday
import io.ktor.client.plugins.ResponseException
import java.io.IOException
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

class CalendarRepository(private val api: CalendarApiService) {

    /** Próximo feriado a partir de hoje. Falhas viram ExternalDataException com mensagem amigável. */
    suspend fun getNextHoliday(today: LocalDate): Result<Holiday?> =
        try {
            val thisYear = api.getPublicHolidays(today.year).toHolidays()
            val next = thisYear.firstOrNull { !it.date.isBefore(today) }
                ?: api.getPublicHolidays(today.year + 1).toHolidays().firstOrNull()
            Result.success(next)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(ExternalDataException(e.toFriendlyMessage(), e))
        }

    private fun List<HolidayDto>.toHolidays(): List<Holiday> =
        map { Holiday(date = LocalDate.parse(it.date), name = it.localName) }.sortedBy { it.date }

    private fun Exception.toFriendlyMessage(): String = when (this) {
        is ResponseException -> "O serviço de feriados está indisponível no momento."
        is IOException -> "Sem conexão com a internet ou tempo esgotado."
        else -> "Resposta inválida do serviço de feriados."
    }
}
