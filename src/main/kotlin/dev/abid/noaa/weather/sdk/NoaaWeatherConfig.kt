package dev.abid.noaa.weather.sdk

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NoaaWeatherConfig(
    val userAgent: String,
    val baseUrl: String = "https://api.weather.gov",
    val connectTimeout: Duration = 10.seconds,
    val readTimeout: Duration = 30.seconds,
    val enableLogging: Boolean = false
)
