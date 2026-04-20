package dev.abid.noaa.weather.sdk.mapper

import dev.abid.noaa.weather.sdk.api.NwsAlertFeature
import dev.abid.noaa.weather.sdk.api.NwsAlertListResponse
import dev.abid.noaa.weather.sdk.model.alert.*
import java.time.Instant

internal object AlertMapper {

    fun mapAlerts(response: NwsAlertListResponse): List<Alert> {
        return response.features.map { mapAlert(it) }
    }

    fun mapAlert(feature: NwsAlertFeature): Alert {
        val p = feature.properties
        return Alert(
            id = p.id ?: feature.id,
            type = p.type ?: "",
            event = p.event ?: "",
            headline = p.headline ?: "",
            description = p.description ?: "",
            instruction = p.instruction,
            severity = parseSeverity(p.severity),
            urgency = parseUrgency(p.urgency),
            certainty = parseCertainty(p.certainty),
            area = p.area ?: "",
            affectedZones = p.affectedZones ?: emptyList(),
            effective = parseInstant(p.effective),
            expires = p.expires?.let { parseInstant(it) },
            sender = p.senderName ?: ""
        )
    }

    private fun parseSeverity(value: String?): AlertSeverity = when (value?.uppercase()) {
        "EXTREME" -> AlertSeverity.EXTREME
        "SEVERE" -> AlertSeverity.SEVERE
        "MODERATE" -> AlertSeverity.MODERATE
        "MINOR" -> AlertSeverity.MINOR
        else -> AlertSeverity.UNKNOWN
    }

    private fun parseUrgency(value: String?): AlertUrgency = when (value?.uppercase()) {
        "IMMEDIATE" -> AlertUrgency.IMMEDIATE
        "EXPECTED" -> AlertUrgency.EXPECTED
        "FUTURE" -> AlertUrgency.FUTURE
        "PAST" -> AlertUrgency.PAST
        else -> AlertUrgency.UNKNOWN
    }

    private fun parseCertainty(value: String?): AlertCertainty = when (value?.uppercase()) {
        "OBSERVED" -> AlertCertainty.OBSERVED
        "LIKELY" -> AlertCertainty.LIKELY
        "POSSIBLE" -> AlertCertainty.POSSIBLE
        "UNLIKELY" -> AlertCertainty.UNLIKELY
        else -> AlertCertainty.UNKNOWN
    }

    private fun parseInstant(isoString: String?): Instant {
        if (isoString.isNullOrBlank()) return Instant.now()
        return Instant.parse(isoString)
    }
}
