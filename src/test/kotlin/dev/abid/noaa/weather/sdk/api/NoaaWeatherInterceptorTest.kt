package dev.abid.noaa.weather.sdk.api

import dev.abid.noaa.weather.sdk.exception.NoaaApiException
import dev.abid.noaa.weather.sdk.exception.NoaaRateLimitException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NoaaWeatherInterceptorTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(NoaaWeatherInterceptor("test-agent"))
            .build()
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `adds required headers`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val request = Request.Builder().url(server.url("/test")).build()
        client.newCall(request).execute()

        val recorded = server.takeRequest()
        assertEquals("test-agent", recorded.getHeader("User-Agent"))
        assertEquals("application/geo+json", recorded.getHeader("Accept"))
    }

    @Test
    fun `throws NoaaRateLimitException on 429`() {
        server.enqueue(MockResponse().setResponseCode(429).addHeader("Retry-After", "60"))

        val request = Request.Builder().url(server.url("/test")).build()
        assertThrows<NoaaRateLimitException> {
            client.newCall(request).execute()
        }
    }

    @Test
    fun `throws NoaaApiException on 404`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val request = Request.Builder().url(server.url("/test")).build()
        assertThrows<NoaaApiException> {
            client.newCall(request).execute()
        }
    }

    @Test
    fun `throws NoaaApiException on 500`() {
        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val request = Request.Builder().url(server.url("/test")).build()
        val ex = assertThrows<NoaaApiException> {
            client.newCall(request).execute()
        }
        assertEquals(500, ex.statusCode)
    }
}
