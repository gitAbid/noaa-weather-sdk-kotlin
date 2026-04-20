package dev.abid.noaa.weather.sdk

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull

class NoaaWeatherSdkTest {

    @Test
    fun `builder creates sdk with all services`() {
        val sdk = NoaaWeatherSdk.builder()
            .userAgent("test-app")
            .build()

        assertNotNull(sdk.forecasts)
        assertNotNull(sdk.alerts)
        assertNotNull(sdk.observations)
        assertNotNull(sdk.glossary)
    }

    @Test
    fun `builder requires userAgent`() {
        assertThrows<IllegalArgumentException> {
            NoaaWeatherSdk.builder().build()
        }
    }

    @Test
    fun `builder accepts custom baseUrl`() {
        val sdk = NoaaWeatherSdk.builder()
            .userAgent("test-app")
            .baseUrl("http://localhost:8080")
            .enableLogging(true)
            .build()

        assertNotNull(sdk)
    }
}
