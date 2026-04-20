package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.ForecastApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ForecastServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: ForecastService

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ForecastApi::class.java)
        service = DefaultForecastService(api)
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getDailyForecast resolves gridpoint and returns forecast`() = kotlinx.coroutines.test.runTest {
        // First call: /points/{lat},{lon}
        server.enqueue(MockResponse().setBody("""
            {
                "properties": {
                    "gridId": "BOU",
                    "gridX": 65,
                    "gridY": 61,
                    "forecast": "https://api.weather.gov/gridpoints/BOU/65,61/forecast",
                    "forecastHourly": null,
                    "observationStations": null
                }
            }
        """.trimIndent()))

        // Second call: /gridpoints/{wfo}/{x},{y}/forecast
        server.enqueue(MockResponse().setBody("""
            {
                "properties": {
                    "generatedAt": "2026-04-20T12:00:00Z",
                    "periods": [
                        {
                            "number": 1,
                            "name": "Tonight",
                            "startTime": "2026-04-20T18:00:00Z",
                            "endTime": "2026-04-21T06:00:00Z",
                            "isDaytime": false,
                            "temperature": 45,
                            "temperatureUnit": "F",
                            "windSpeed": "5 mph",
                            "windDirection": "SW",
                            "shortForecast": "Partly Cloudy",
                            "detailedForecast": "Partly cloudy, with a low around 45.",
                            "icon": "https://api.weather.gov/icons/land/night/sct?size=medium"
                        }
                    ]
                }
            }
        """.trimIndent()))

        val result = service.getDailyForecast(39.7456, -104.9984)
        assertEquals(1, result.periods.size)
        assertEquals("Tonight", result.periods[0].name)
        assertEquals(45, result.periods[0].temperature)
        assertEquals("F", result.periods[0].temperatureUnit)
        assertEquals("Partly Cloudy", result.periods[0].shortForecast)
    }

    @Test
    fun `getGridpointForecast returns forecast with gridpoint`() = kotlinx.coroutines.test.runTest {
        server.enqueue(MockResponse().setBody("""
            {
                "properties": {
                    "generatedAt": "2026-04-20T12:00:00Z",
                    "periods": [
                        {
                            "number": 1,
                            "name": "Today",
                            "startTime": "2026-04-20T12:00:00Z",
                            "endTime": "2026-04-20T18:00:00Z",
                            "isDaytime": true,
                            "temperature": 72,
                            "temperatureUnit": "F",
                            "windSpeed": "10 mph",
                            "windDirection": "NW",
                            "shortForecast": "Sunny",
                            "detailedForecast": null,
                            "icon": null
                        }
                    ]
                }
            }
        """.trimIndent()))

        val result = service.getGridpointForecast("BOU", 65, 61)
        assertEquals("BOU", result.gridPoint.wfo)
        assertEquals(65, result.gridPoint.gridX)
        assertEquals(1, result.periods.size)
        assertEquals("Sunny", result.periods[0].shortForecast)
        assertTrue(result.periods[0].isDaytime)
    }
}
