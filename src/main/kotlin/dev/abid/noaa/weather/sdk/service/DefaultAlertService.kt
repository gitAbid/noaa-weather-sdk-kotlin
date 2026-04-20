package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.AlertApi
import dev.abid.noaa.weather.sdk.mapper.AlertMapper
import dev.abid.noaa.weather.sdk.model.alert.Alert

internal class DefaultAlertService(private val api: AlertApi) : AlertService {

    override suspend fun getActiveAlerts(): List<Alert> {
        return AlertMapper.mapAlerts(api.getActiveAlerts())
    }

    override suspend fun getAlertsByArea(stateCode: String): List<Alert> {
        return AlertMapper.mapAlerts(api.getAlertsByArea(stateCode))
    }

    override suspend fun getAlertsByZone(zoneId: String): List<Alert> {
        return AlertMapper.mapAlerts(api.getAlertsByZone(zoneId))
    }

    override suspend fun getAlert(id: String): Alert {
        return AlertMapper.mapAlert(api.getAlert(id))
    }
}
