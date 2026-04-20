package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.model.observation.Observation
import dev.abid.noaa.weather.sdk.model.observation.Station

interface ObservationService {
    suspend fun getNearbyStations(latitude: Double, longitude: Double): List<Station>
    suspend fun getLatestObservation(stationId: String): Observation
    suspend fun getObservations(stationId: String): List<Observation>
}
