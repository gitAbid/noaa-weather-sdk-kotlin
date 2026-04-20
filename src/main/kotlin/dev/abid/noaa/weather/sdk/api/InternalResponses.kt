package dev.abid.noaa.weather.sdk.api

import com.google.gson.annotations.SerializedName

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
