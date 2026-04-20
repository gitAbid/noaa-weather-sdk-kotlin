package dev.abid.noaa.weather.sdk.model.forecast

import java.time.Instant

data class DailyForecast(
    val periods: List<ForecastPeriod>,
    val generatedAt: Instant
)

data class ForecastPeriod(
    val number: Int,
    val name: String,
    val startTime: Instant,
    val endTime: Instant,
    val isDaytime: Boolean,
    val temperature: Int,
    val temperatureUnit: String,
    val windSpeed: String,
    val windDirection: String,
    val shortForecast: String,
    val detailedForecast: String?,
    val icon: String?
)

data class HourlyForecast(
    val time: Instant,
    val temperature: Int,
    val temperatureUnit: String,
    val windSpeed: String,
    val windDirection: String,
    val shortForecast: String,
    val probabilityOfPrecipitation: Int?,
    val dewpoint: Double?,
    val relativeHumidity: Int?,
    val windChill: Int?,
    val heatIndex: Int?,
    val icon: String?
)

data class GridpointForecast(
    val gridPoint: dev.abid.noaa.weather.sdk.model.common.GridPoint,
    val periods: List<ForecastPeriod>,
    val generatedAt: Instant
)

data class ZoneForecast(
    val zoneId: String,
    val zoneName: String,
    val periods: List<ForecastPeriod>,
    val generatedAt: Instant
)
