package dev.abid.noaa.weather.sdk.mapper

import dev.abid.noaa.weather.sdk.api.*
import dev.abid.noaa.weather.sdk.model.common.GeoPoint
import dev.abid.noaa.weather.sdk.model.observation.*
import java.time.Instant

internal object ObservationMapper {

    fun mapStations(response: NwsStationListResponse): List<Station> {
        return response.features.map { feature ->
            val p = feature.properties
            val coords = p.geometry?.coordinates
            Station(
                id = p.id?.substringAfterLast("/") ?: "",
                name = p.name ?: "",
                coordinates = if (coords != null && coords.size >= 2) GeoPoint(coords[1], coords[0]) else GeoPoint(0.0, 0.0),
                elevation = p.elevation?.value
            )
        }
    }

    fun mapObservation(feature: NwsObservationFeature): Observation {
        val p = feature.properties
        return Observation(
            stationId = p.id?.substringAfterLast("/") ?: "",
            timestamp = parseInstant(p.timestamp),
            temperature = p.temperature?.value,
            temperatureUnit = p.temperature?.unitCode?.substringAfterLast("/"),
            dewpoint = p.dewpoint?.value,
            windSpeed = p.windSpeed?.value,
            windDirection = p.windDirection?.value,
            windGust = p.windGust?.value,
            barometricPressure = p.barometricPressure?.value,
            visibility = p.visibility?.value,
            relativeHumidity = p.relativeHumidity?.value,
            precipitationLastHour = p.precipitationLastHour?.value,
            weatherDescription = p.textDescription
        )
    }

    fun mapObservations(response: NwsObservationListResponse): List<Observation> {
        return response.features.map { mapObservation(it) }
    }

    private fun parseInstant(isoString: String?): Instant {
        if (isoString.isNullOrBlank()) return Instant.now()
        return Instant.parse(isoString)
    }
}
