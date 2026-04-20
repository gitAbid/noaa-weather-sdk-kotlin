package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.ForecastApi
import dev.abid.noaa.weather.sdk.exception.NoaaParseException
import dev.abid.noaa.weather.sdk.mapper.ForecastMapper
import dev.abid.noaa.weather.sdk.model.forecast.*

internal class DefaultForecastService(private val api: ForecastApi) : ForecastService {

    override suspend fun getDailyForecast(latitude: Double, longitude: Double): DailyForecast {
        val point = ForecastMapper.mapPoint(api.getPoint(latitude, longitude))
        val response = api.getForecast(point.wfo, point.gridX, point.gridY)
        return ForecastMapper.mapDailyForecast(response)
    }

    override suspend fun getHourlyForecast(latitude: Double, longitude: Double): List<HourlyForecast> {
        val point = ForecastMapper.mapPoint(api.getPoint(latitude, longitude))
        val response = api.getHourlyForecast(point.wfo, point.gridX, point.gridY)
        return ForecastMapper.mapHourlyForecast(response)
    }

    override suspend fun getGridpointForecast(wfo: String, gridX: Int, gridY: Int): GridpointForecast {
        val response = api.getForecast(wfo, gridX, gridY)
        return ForecastMapper.mapGridpointForecast(response, dev.abid.noaa.weather.sdk.model.common.GridPoint(wfo, gridX, gridY))
    }

    override suspend fun getGridpointHourly(wfo: String, gridX: Int, gridY: Int): List<HourlyForecast> {
        val response = api.getHourlyForecast(wfo, gridX, gridY)
        return ForecastMapper.mapHourlyForecast(response)
    }

    override suspend fun getZoneForecast(zoneId: String): ZoneForecast {
        val response = api.getZoneForecast(zoneId)
        return ForecastMapper.mapZoneForecast(response, zoneId, zoneId)
    }
}
