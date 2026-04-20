package dev.abid.noaa.weather.sdk.api

import com.google.gson.annotations.SerializedName

// --- Glossary ---
internal data class NwsGlossaryResponse(
    val glossary: List<NwsGlossaryItem>
)

internal data class NwsGlossaryItem(
    val term: String,
    val definition: String
)

internal data class NwsRawIconsResponse(
    @SerializedName("@context") val context: Any? = null,
    val icons: Map<String, Map<String, NwsIconDefinition>> = emptyMap()
)

internal data class NwsIconDefinition(
    val description: String = ""
)

// --- Points ---
internal data class NwsPointResponse(
    val properties: NwsPointProperties
)

internal data class NwsPointProperties(
    val gridId: String,
    val gridX: Int,
    val gridY: Int,
    val forecast: String?,
    val forecastHourly: String?,
    val observationStations: String?
)

// --- Forecasts ---
internal data class NwsForecastResponse(
    val properties: NwsForecastProperties
)

internal data class NwsForecastProperties(
    val generatedAt: String?,
    val periods: List<NwsForecastPeriod>
)

internal data class NwsForecastPeriod(
    val number: Int = 0,
    val name: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val isDaytime: Boolean = true,
    val temperature: Int? = null,
    val temperatureUnit: String? = null,
    val windSpeed: String? = null,
    val windDirection: String? = null,
    val shortForecast: String? = null,
    val detailedForecast: String? = null,
    val icon: String? = null,
    val probabilityOfPrecipitation: NwsQuantitativeValue? = null,
    val dewpoint: NwsQuantitativeValue? = null,
    val relativeHumidity: NwsQuantitativeValue?,
    val windChill: NwsQuantitativeValue?,
    val heatIndex: NwsQuantitativeValue?
)

internal data class NwsQuantitativeValue(
    val value: Double?,
    val unitCode: String?
)

// --- Alerts ---
internal data class NwsAlertListResponse(
    val features: List<NwsAlertFeature>
)

internal data class NwsAlertFeature(
    val id: String,
    val properties: NwsAlertProperties
)

internal data class NwsAlertProperties(
    val id: String?,
    val type: String?,
    val event: String?,
    val headline: String?,
    val description: String?,
    val instruction: String?,
    val severity: String?,
    val urgency: String?,
    val certainty: String?,
    val area: String?,
    val affectedZones: List<String>?,
    val effective: String?,
    val expires: String?,
    val senderName: String?
)

// --- Observations ---
internal data class NwsStationListResponse(
    val features: List<NwsStationFeature>
)

internal data class NwsStationFeature(
    val properties: NwsStationProperties
)

internal data class NwsStationProperties(
    @SerializedName("@id") val id: String?,
    val name: String?,
    val elevation: NwsQuantitativeValue?,
    val geometry: NwsGeometry?
)

internal data class NwsGeometry(
    val coordinates: List<Double>?
)

internal data class NwsObservationListResponse(
    val features: List<NwsObservationFeature>
)

internal data class NwsObservationFeature(
    val properties: NwsObservationProperties
)

internal data class NwsObservationProperties(
    @SerializedName("@id") val id: String?,
    val timestamp: String?,
    val temperature: NwsQuantitativeValue?,
    val dewpoint: NwsQuantitativeValue?,
    val windSpeed: NwsQuantitativeValue?,
    val windDirection: NwsQuantitativeValue?,
    val windGust: NwsQuantitativeValue?,
    val barometricPressure: NwsQuantitativeValue?,
    val visibility: NwsQuantitativeValue?,
    val relativeHumidity: NwsQuantitativeValue?,
    val precipitationLastHour: NwsQuantitativeValue?,
    val textDescription: String?
)
