package dev.abid.noaa.weather.sdk.model.alert

import java.time.Instant

data class Alert(
    val id: String,
    val type: String,
    val event: String,
    val headline: String,
    val description: String,
    val instruction: String?,
    val severity: AlertSeverity,
    val urgency: AlertUrgency,
    val certainty: AlertCertainty,
    val area: String,
    val affectedZones: List<String>,
    val effective: Instant,
    val expires: Instant?,
    val sender: String
)

enum class AlertSeverity { EXTREME, SEVERE, MODERATE, MINOR, UNKNOWN }
enum class AlertUrgency { IMMEDIATE, EXPECTED, FUTURE, PAST, UNKNOWN }
enum class AlertCertainty { OBSERVED, LIKELY, POSSIBLE, UNLIKELY, UNKNOWN }
