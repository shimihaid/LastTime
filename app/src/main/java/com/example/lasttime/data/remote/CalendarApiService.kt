package com.example.lasttime.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
data class HolidayDto(
    val date: String,
    val localName: String,
    val name: String
)

/** Só fala HTTP. Não conhece Room nem UI. API pública: https://date.nager.at */
class CalendarApiService(private val client: HttpClient) {

    suspend fun getPublicHolidays(year: Int, countryCode: String = "BR"): List<HolidayDto> =
        client.get("https://date.nager.at/api/v3/PublicHolidays/$year/$countryCode").body()
}
