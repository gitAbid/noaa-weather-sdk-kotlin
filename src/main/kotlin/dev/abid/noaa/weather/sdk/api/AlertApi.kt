package dev.abid.noaa.weather.sdk.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface AlertApi {
    @GET("/alerts")
    suspend fun getActiveAlerts(
        @Query("status") status: String = "actual"
    ): NwsAlertListResponse

    @GET("/alerts")
    suspend fun getAlertsByArea(
        @Query("area") area: String
    ): NwsAlertListResponse

    @GET("/alerts")
    suspend fun getAlertsByZone(
        @Query("zone") zone: String
    ): NwsAlertListResponse

    @GET("/alerts/{id}")
    suspend fun getAlert(
        @Path("id") id: String
    ): NwsAlertFeature
}
