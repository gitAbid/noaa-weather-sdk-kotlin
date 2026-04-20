package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.AlertApi
import dev.abid.noaa.weather.sdk.model.alert.AlertSeverity
import dev.abid.noaa.weather.sdk.model.alert.AlertUrgency
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

class AlertServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: AlertService

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlertApi::class.java)
        service = DefaultAlertService(api)
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getActiveAlerts parses alert list`() = kotlinx.coroutines.test.runTest {
        server.enqueue(MockResponse().setBody("""
            {
                "features": [
                    {
                        "id": "https://api.weather.gov/alerts/urn:oid:2.49.0.1.840.0.test",
                        "properties": {
                            "id": "urn:oid:2.49.0.1.840.0.test",
                            "type": "Alert",
                            "event": "Tornado Warning",
                            "headline": "Tornado Warning issued for Test County",
                            "description": "A tornado has been spotted.",
                            "instruction": "Take shelter immediately.",
                            "severity": "Extreme",
                            "urgency": "Immediate",
                            "certainty": "Observed",
                            "area": "Test County",
                            "affectedZones": ["COZ001"],
                            "effective": "2026-04-20T12:00:00Z",
                            "expires": "2026-04-20T13:00:00Z",
                            "senderName": "NWS Denver"
                        }
                    }
                ]
            }
        """.trimIndent()))

        val result = service.getActiveAlerts()
        assertEquals(1, result.size)
        assertEquals("Tornado Warning", result[0].event)
        assertEquals(AlertSeverity.EXTREME, result[0].severity)
        assertEquals(AlertUrgency.IMMEDIATE, result[0].urgency)
        assertEquals(listOf("COZ001"), result[0].affectedZones)
    }

    @Test
    fun `getAlertsByArea sends area query`() = kotlinx.coroutines.test.runTest {
        server.enqueue(MockResponse().setBody("""
            { "features": [] }
        """.trimIndent()))

        service.getAlertsByArea("CO")

        val request = server.takeRequest()
        assertTrue(request.path!!.contains("area=CO"))
    }
}
