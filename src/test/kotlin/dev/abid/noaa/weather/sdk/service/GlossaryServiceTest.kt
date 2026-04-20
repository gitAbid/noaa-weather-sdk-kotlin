package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.GlossaryApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

class GlossaryServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: GlossaryService

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GlossaryApi::class.java)
        service = DefaultGlossaryService(api)
    }

    @AfterEach
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getGlossaryTerms parses correctly`() = kotlinx.coroutines.test.runTest {
        server.enqueue(MockResponse().setBody("""
            {
                "glossary": [
                    {"term": "Ablation", "definition": "Loss of ice or snow"},
                    {"term": "Advisory", "definition": "Issued when conditions are less serious"}
                ]
            }
        """.trimIndent()))

        val result = service.getGlossaryTerms()
        assertEquals(2, result.size)
        assertEquals("Ablation", result[0].term)
        assertEquals("Loss of ice or snow", result[0].definition)
    }
}
