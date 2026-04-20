package dev.abid.noaa.weather.sdk.model.glossary

data class GlossaryTerm(
    val term: String,
    val definition: String
)

data class IconInfo(
    val id: String,
    val url: String,
    val description: String?
)
