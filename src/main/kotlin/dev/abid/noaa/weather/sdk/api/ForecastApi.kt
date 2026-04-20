package dev.abid.noaa.weather.sdk.api

import retrofit2.http.GET
import retrofit2.http.Path

internal interface ForecastApi {
    @GET("/points/{lat},{lon}")
    suspend fun getPoint(
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double
    ): NwsPointResponse

    @GET("/gridpoints/{wfo}/{x},{y}/forecast")
    suspend fun getForecast(
        @Path("wfo") wfo: String,
        @Path("x") gridX: Int,
        @Path("y") gridY: Int
    ): NwsForecastResponse

    @GET("/gridpoints/{wfo}/{x},{y}/forecast/hourly")
    suspend fun getHourlyForecast(
        @Path("wfo") wfo: String,
        @Path("x") gridX: Int,
        @Path("y") gridY: Int
    ): NwsForecastResponse

    @GET("/zones/forecast/{zoneId}/forecast")
    suspend fun getZoneForecast(
        @Path("zoneId") zoneId: String
    ): NwsForecastResponse
}
