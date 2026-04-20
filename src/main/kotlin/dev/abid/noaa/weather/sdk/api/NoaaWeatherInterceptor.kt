package dev.abid.noaa.weather.sdk.api

import dev.abid.noaa.weather.sdk.exception.NoaaApiException
import dev.abid.noaa.weather.sdk.exception.NoaaRateLimitException
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.time.Duration.Companion.seconds

internal class NoaaWeatherInterceptor(private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("User-Agent", userAgent)
            .header("Accept", "application/geo+json")
            .build()

        val response = chain.proceed(request)

        when {
            response.code == 429 -> {
                val retryAfterSeconds = response.header("Retry-After")?.toLongOrNull()
                throw NoaaRateLimitException(retryAfterSeconds?.seconds)
            }
            response.code == 404 -> throw NoaaApiException(404, null, "Resource not found")
            !response.isSuccessful -> throw NoaaApiException(
                response.code,
                response.body?.string(),
                response.message
            )
        }
        return response
    }
}
