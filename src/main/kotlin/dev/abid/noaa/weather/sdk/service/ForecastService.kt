package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.model.forecast.*

interface ForecastService {
    suspend fun getDailyForecast(latitude: Double, longitude: Double): DailyForecast
    suspend fun getHourlyForecast(latitude: Double, longitude: Double): List<HourlyForecast>
    suspend fun getGridpointForecast(wfo: String, gridX: Int, gridY: Int): GridpointForecast
    suspend fun getGridpointHourly(wfo: String, gridX: Int, gridY: Int): List<HourlyForecast>
    suspend fun getZoneForecast(zoneId: String): ZoneForecast
}
