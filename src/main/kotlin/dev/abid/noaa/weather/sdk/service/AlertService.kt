package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.model.alert.Alert

interface AlertService {
    suspend fun getActiveAlerts(): List<Alert>
    suspend fun getAlertsByArea(stateCode: String): List<Alert>
    suspend fun getAlertsByZone(zoneId: String): List<Alert>
    suspend fun getAlert(id: String): Alert
}
