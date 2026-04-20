package dev.abid.noaa.weather.sdk.api

import retrofit2.http.GET
import retrofit2.http.Path

internal interface ObservationApi {
    @GET("/points/{lat},{lon}/stations")
    suspend fun getNearbyStations(
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double
    ): NwsStationListResponse

    @GET("/stations/{stationId}/observations/latest")
    suspend fun getLatestObservation(
        @Path("stationId") stationId: String
    ): NwsObservationFeature

    @GET("/stations/{stationId}/observations")
    suspend fun getObservations(
        @Path("stationId") stationId: String
    ): NwsObservationListResponse
}
