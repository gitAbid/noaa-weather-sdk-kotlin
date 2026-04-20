package dev.abid.noaa.weather.sdk.api

import dev.abid.noaa.weather.sdk.exception.NoaaApiException
import dev.abid.noaa.weather.sdk.exception.NoaaRateLimitException
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

internal class NoaaWeatherInterceptor(private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("User-Agent", userAgent)
            .header("Accept", "application/geo+json")
            .build()

        logger.debug { "Request: ${request.method} ${request.url}" }

        val response = chain.proceed(request)

        logger.debug { "Response: ${response.code} ${request.url}" }

        when {
            response.code == 429 -> {
                val retryAfterSeconds = response.header("Retry-After")?.toLongOrNull()
                logger.error { "Rate limited by NWS API (429) for ${request.url}, Retry-After: ${retryAfterSeconds}s" }
                throw NoaaRateLimitException(retryAfterSeconds?.seconds)
            }
            response.code == 404 -> {
                logger.warn { "Resource not found (404): ${request.url}" }
                throw NoaaApiException(404, null, "Resource not found")
            }
            !response.isSuccessful -> {
                val errorBody = response.body?.string()
                logger.error { "API error ${response.code} for ${request.url}: ${response.message}" }
                throw NoaaApiException(
                    response.code,
                    errorBody,
                    response.message
                )
            }
        }
        return response
    }
}
