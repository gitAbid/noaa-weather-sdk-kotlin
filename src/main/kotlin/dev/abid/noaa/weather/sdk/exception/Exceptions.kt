package dev.abid.noaa.weather.sdk.exception

import java.io.IOException
import kotlin.time.Duration

sealed class NoaaWeatherException(message: String, cause: Throwable?) : RuntimeException(message, cause)

class NoaaApiException(val statusCode: Int, val errorBody: String?, message: String) : NoaaWeatherException(message, null)

class NoaaRateLimitException(val retryAfter: Duration?) : NoaaWeatherException("Rate limited${retryAfter?.let { ", retry after $it" } ?: ""}", null)

class NoaaNetworkException(cause: IOException) : NoaaWeatherException("Network error: ${cause.message}", cause)

class NoaaParseException(val rawBody: String, cause: Throwable) : NoaaWeatherException("Failed to parse response: ${cause.message}", cause)
