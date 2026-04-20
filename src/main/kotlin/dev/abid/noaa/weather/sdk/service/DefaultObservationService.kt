package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.ObservationApi
import dev.abid.noaa.weather.sdk.mapper.ObservationMapper
import dev.abid.noaa.weather.sdk.model.observation.Observation
import dev.abid.noaa.weather.sdk.model.observation.Station
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class DefaultObservationService(private val api: ObservationApi) : ObservationService {

    override suspend fun getNearbyStations(latitude: Double, longitude: Double): List<Station> {
        logger.debug { "getNearbyStations: lat=$latitude, lon=$longitude" }
        val stations = ObservationMapper.mapStations(api.getNearbyStations(latitude, longitude))
        logger.info { "getNearbyStations: returned ${stations.size} stations" }
        return stations
    }

    override suspend fun getLatestObservation(stationId: String): Observation {
        logger.debug { "getLatestObservation: stationId=$stationId" }
        val obs = ObservationMapper.mapObservation(api.getLatestObservation(stationId))
        logger.info { "getLatestObservation: $stationId — temp=${obs.temperature} ${obs.temperatureUnit}, desc=${obs.weatherDescription}" }
        return obs
    }

    override suspend fun getObservations(stationId: String): List<Observation> {
        logger.debug { "getObservations: stationId=$stationId" }
        val observations = ObservationMapper.mapObservations(api.getObservations(stationId))
        logger.info { "getObservations: returned ${observations.size} observations for $stationId" }
        return observations
    }
}
