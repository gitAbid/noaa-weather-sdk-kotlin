package dev.abid.noaa.weather.sdk

import dev.abid.noaa.weather.sdk.api.*
import dev.abid.noaa.weather.sdk.service.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class NoaaWeatherSdk private constructor(config: NoaaWeatherConfig) {

    val forecasts: ForecastService
    val alerts: AlertService
    val observations: ObservationService
    val glossary: GlossaryService

    init {
        val okHttpBuilder = OkHttpClient.Builder()
            .addInterceptor(NoaaWeatherInterceptor(config.userAgent))
            .connectTimeout(config.connectTimeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)

        if (config.enableLogging) {
            okHttpBuilder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        val client = okHttpBuilder.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        forecasts = DefaultForecastService(retrofit.create(ForecastApi::class.java))
        alerts = DefaultAlertService(retrofit.create(AlertApi::class.java))
        observations = DefaultObservationService(retrofit.create(ObservationApi::class.java))
        glossary = DefaultGlossaryService(retrofit.create(GlossaryApi::class.java))
    }

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var userAgent: String? = null
        private var baseUrl: String = "https://api.weather.gov"
        private var connectTimeout: Duration = 10.seconds
        private var readTimeout: Duration = 30.seconds
        private var enableLogging: Boolean = false

        fun userAgent(userAgent: String) = apply { this.userAgent = userAgent }
        fun baseUrl(url: String) = apply { this.baseUrl = url }
        fun connectTimeout(timeout: Duration) = apply { this.connectTimeout = timeout }
        fun readTimeout(timeout: Duration) = apply { this.readTimeout = timeout }
        fun enableLogging(enabled: Boolean) = apply { this.enableLogging = enabled }

        fun build(): NoaaWeatherSdk {
            val agent = requireNotNull(userAgent) { "userAgent is required" }
            return NoaaWeatherSdk(
                NoaaWeatherConfig(
                    userAgent = agent,
                    baseUrl = baseUrl,
                    connectTimeout = connectTimeout,
                    readTimeout = readTimeout,
                    enableLogging = enableLogging
                )
            )
        }
    }
}
