package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.ObservationApi
import dev.abid.noaa.weather.sdk.mapper.ObservationMapper
import dev.abid.noaa.weather.sdk.model.observation.Observation
import dev.abid.noaa.weather.sdk.model.observation.Station

internal class DefaultObservationService(private val api: ObservationApi) : ObservationService {

    override suspend fun getNearbyStations(latitude: Double, longitude: Double): List<Station> {
        return ObservationMapper.mapStations(api.getNearbyStations(latitude, longitude))
    }

    override suspend fun getLatestObservation(stationId: String): Observation {
        return ObservationMapper.mapObservation(api.getLatestObservation(stationId))
    }

    override suspend fun getObservations(stationId: String): List<Observation> {
        return ObservationMapper.mapObservations(api.getObservations(stationId))
    }
}
