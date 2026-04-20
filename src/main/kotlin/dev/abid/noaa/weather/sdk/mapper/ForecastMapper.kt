package dev.abid.noaa.weather.sdk.mapper

import dev.abid.noaa.weather.sdk.api.*
import dev.abid.noaa.weather.sdk.model.common.GridPoint
import dev.abid.noaa.weather.sdk.model.forecast.*
import java.time.Instant

internal object ForecastMapper {

    fun mapPoint(response: NwsPointResponse): GridPoint {
        return GridPoint(
            wfo = response.properties.gridId,
            gridX = response.properties.gridX,
            gridY = response.properties.gridY
        )
    }

    fun mapDailyForecast(response: NwsForecastResponse): DailyForecast {
        return DailyForecast(
            periods = response.properties.periods.map { mapPeriod(it) },
            generatedAt = parseInstant(response.properties.generatedAt)
        )
    }

    fun mapHourlyForecast(response: NwsForecastResponse): List<HourlyForecast> {
        return response.properties.periods.map { mapHourlyPeriod(it) }
    }

    fun mapGridpointForecast(response: NwsForecastResponse, gridPoint: GridPoint): GridpointForecast {
        return GridpointForecast(
            gridPoint = gridPoint,
            periods = response.properties.periods.map { mapPeriod(it) },
            generatedAt = parseInstant(response.properties.generatedAt)
        )
    }

    fun mapZoneForecast(response: NwsForecastResponse, zoneId: String, zoneName: String): ZoneForecast {
        return ZoneForecast(
            zoneId = zoneId,
            zoneName = zoneName,
            periods = response.properties.periods.map { mapPeriod(it) },
            generatedAt = parseInstant(response.properties.generatedAt)
        )
    }

    private fun mapPeriod(p: NwsForecastPeriod): ForecastPeriod {
        return ForecastPeriod(
            number = p.number,
            name = p.name,
            startTime = parseInstant(p.startTime),
            endTime = parseInstant(p.endTime),
            isDaytime = p.isDaytime,
            temperature = p.temperature,
            temperatureUnit = p.temperatureUnit,
            windSpeed = p.windSpeed,
            windDirection = p.windDirection,
            shortForecast = p.shortForecast,
            detailedForecast = p.detailedForecast,
            icon = p.icon
        )
    }

    private fun mapHourlyPeriod(p: NwsForecastPeriod): HourlyForecast {
        return HourlyForecast(
            time = parseInstant(p.startTime),
            temperature = p.temperature,
            temperatureUnit = p.temperatureUnit,
            windSpeed = p.windSpeed,
            windDirection = p.windDirection,
            shortForecast = p.shortForecast,
            probabilityOfPrecipitation = p.probabilityOfPrecipitation?.value?.toInt(),
            dewpoint = p.dewpoint?.value,
            relativeHumidity = p.relativeHumidity?.value?.toInt(),
            windChill = p.windChill?.value?.toInt(),
            heatIndex = p.heatIndex?.value?.toInt(),
            icon = p.icon
        )
    }

    private fun parseInstant(isoString: String?): Instant {
        if (isoString.isNullOrBlank()) return Instant.now()
        return Instant.parse(isoString)
    }
}
