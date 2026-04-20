package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.AlertApi
import dev.abid.noaa.weather.sdk.mapper.AlertMapper
import dev.abid.noaa.weather.sdk.model.alert.Alert
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class DefaultAlertService(private val api: AlertApi) : AlertService {

    override suspend fun getActiveAlerts(): List<Alert> {
        logger.debug { "getActiveAlerts: fetching all active alerts" }
        val alerts = AlertMapper.mapAlerts(api.getActiveAlerts())
        logger.info { "getActiveAlerts: returned ${alerts.size} alerts" }
        return alerts
    }

    override suspend fun getAlertsByArea(stateCode: String): List<Alert> {
        logger.debug { "getAlertsByArea: stateCode=$stateCode" }
        val alerts = AlertMapper.mapAlerts(api.getAlertsByArea(stateCode))
        logger.info { "getAlertsByArea: returned ${alerts.size} alerts for $stateCode" }
        return alerts
    }

    override suspend fun getAlertsByZone(zoneId: String): List<Alert> {
        logger.debug { "getAlertsByZone: zoneId=$zoneId" }
        val alerts = AlertMapper.mapAlerts(api.getAlertsByZone(zoneId))
        logger.info { "getAlertsByZone: returned ${alerts.size} alerts for zone $zoneId" }
        return alerts
    }

    override suspend fun getAlert(id: String): Alert {
        logger.debug { "getAlert: id=$id" }
        val alert = AlertMapper.mapAlert(api.getAlert(id))
        logger.info { "getAlert: returned alert '${alert.event}' (${alert.severity})" }
        return alert
    }
}
