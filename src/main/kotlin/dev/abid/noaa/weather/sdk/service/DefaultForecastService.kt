package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.ForecastApi
import dev.abid.noaa.weather.sdk.mapper.ForecastMapper
import dev.abid.noaa.weather.sdk.model.common.GridPoint
import dev.abid.noaa.weather.sdk.model.forecast.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class DefaultForecastService(private val api: ForecastApi) : ForecastService {

    override suspend fun getDailyForecast(latitude: Double, longitude: Double): DailyForecast {
        logger.debug { "getDailyForecast: resolving gridpoint for lat=$latitude, lon=$longitude" }
        val point = ForecastMapper.mapPoint(api.getPoint(latitude, longitude))
        logger.info { "getDailyForecast: resolved to gridpoint ${point.wfo}/${point.gridX},${point.gridY}" }
        val response = api.getForecast(point.wfo, point.gridX, point.gridY)
        val forecast = ForecastMapper.mapDailyForecast(response)
        logger.info { "getDailyForecast: returned ${forecast.periods.size} periods" }
        return forecast
    }

    override suspend fun getHourlyForecast(latitude: Double, longitude: Double): List<HourlyForecast> {
        logger.debug { "getHourlyForecast: resolving gridpoint for lat=$latitude, lon=$longitude" }
        val point = ForecastMapper.mapPoint(api.getPoint(latitude, longitude))
        logger.info { "getHourlyForecast: resolved to gridpoint ${point.wfo}/${point.gridX},${point.gridY}" }
        val response = api.getHourlyForecast(point.wfo, point.gridX, point.gridY)
        val forecast = ForecastMapper.mapHourlyForecast(response)
        logger.info { "getHourlyForecast: returned ${forecast.size} hourly periods" }
        return forecast
    }

    override suspend fun getGridpointForecast(wfo: String, gridX: Int, gridY: Int): GridpointForecast {
        logger.debug { "getGridpointForecast: wfo=$wfo, gridX=$gridX, gridY=$gridY" }
        val response = api.getForecast(wfo, gridX, gridY)
        val forecast = ForecastMapper.mapGridpointForecast(response, GridPoint(wfo, gridX, gridY))
        logger.info { "getGridpointForecast: returned ${forecast.periods.size} periods for $wfo/$gridX,$gridY" }
        return forecast
    }

    override suspend fun getGridpointHourly(wfo: String, gridX: Int, gridY: Int): List<HourlyForecast> {
        logger.debug { "getGridpointHourly: wfo=$wfo, gridX=$gridX, gridY=$gridY" }
        val response = api.getHourlyForecast(wfo, gridX, gridY)
        val forecast = ForecastMapper.mapHourlyForecast(response)
        logger.info { "getGridpointHourly: returned ${forecast.size} hourly periods for $wfo/$gridX,$gridY" }
        return forecast
    }

    override suspend fun getZoneForecast(zoneId: String): ZoneForecast {
        logger.debug { "getZoneForecast: zoneId=$zoneId" }
        val response = api.getZoneForecast(zoneId)
        val forecast = ForecastMapper.mapZoneForecast(response, zoneId, zoneId)
        logger.info { "getZoneForecast: returned ${forecast.periods.size} periods for zone $zoneId" }
        return forecast
    }
}
