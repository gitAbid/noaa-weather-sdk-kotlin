package dev.abid.noaa.weather.sdk.model.observation

import dev.abid.noaa.weather.sdk.model.common.GeoPoint
import java.time.Instant

data class Station(
    val id: String,
    val name: String,
    val coordinates: GeoPoint,
    val elevation: Double?
)

data class Observation(
    val stationId: String,
    val timestamp: Instant,
    val temperature: Double?,
    val temperatureUnit: String?,
    val dewpoint: Double?,
    val windSpeed: Double?,
    val windDirection: Double?,
    val windGust: Double?,
    val barometricPressure: Double?,
    val visibility: Double?,
    val relativeHumidity: Double?,
    val precipitationLastHour: Double?,
    val weatherDescription: String?
)
