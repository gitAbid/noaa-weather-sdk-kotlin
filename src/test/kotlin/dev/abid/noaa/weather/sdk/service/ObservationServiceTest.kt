package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.ObservationApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ObservationServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: ObservationService

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ObservationApi::class.java)
        service = DefaultObservationService(api)
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getLatestObservation parses observation`() = kotlinx.coroutines.test.runTest {
        server.enqueue(MockResponse().setBody("""
            {
                "properties": {
                    "@id": "https://api.weather.gov/stations/KDEN/observations/2026-04-20T12:00:00Z",
                    "timestamp": "2026-04-20T12:00:00Z",
                    "temperature": { "value": 15.5, "unitCode": "wmoUnit:degC" },
                    "dewpoint": { "value": 5.0, "unitCode": "wmoUnit:degC" },
                    "windSpeed": { "value": 4.5, "unitCode": "wmoUnit:km_h-1" },
                    "windDirection": { "value": 270.0, "unitCode": "wmoUnit:degree_(angle)" },
                    "windGust": null,
                    "barometricPressure": { "value": 101325.0, "unitCode": "wmoUnit:Pa" },
                    "visibility": { "value": 16093.0, "unitCode": "wmoUnit:m" },
                    "relativeHumidity": { "value": 55.0, "unitCode": "wmoUnit:percent" },
                    "precipitationLastHour": null,
                    "textDescription": "Partly Cloudy"
                }
            }
        """.trimIndent()))

        val result = service.getLatestObservation("KDEN")
        assertEquals("2026-04-20T12:00:00Z", result.stationId)
        assertEquals(15.5, result.temperature)
        assertEquals("Partly Cloudy", result.weatherDescription)
        assertEquals(55.0, result.relativeHumidity)
    }

    @Test
    fun `getNearbyStations parses station list`() = kotlinx.coroutines.test.runTest {
        server.enqueue(MockResponse().setBody("""
            {
                "features": [
                    {
                        "properties": {
                            "@id": "https://api.weather.gov/stations/KDEN",
                            "name": "Denver International Airport",
                            "elevation": { "value": 1655.0, "unitCode": "wmoUnit:m" },
                            "geometry": { "coordinates": [-104.67, 39.86] }
                        }
                    }
                ]
            }
        """.trimIndent()))

        val result = service.getNearbyStations(39.86, -104.67)
        assertEquals(1, result.size)
        assertEquals("KDEN", result[0].id)
        assertEquals("Denver International Airport", result[0].name)
        assertEquals(39.86, result[0].coordinates.latitude)
        assertEquals(-104.67, result[0].coordinates.longitude)
    }
}
