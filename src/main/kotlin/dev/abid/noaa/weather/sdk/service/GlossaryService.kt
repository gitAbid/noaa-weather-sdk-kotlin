package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.model.glossary.GlossaryTerm
import dev.abid.noaa.weather.sdk.model.glossary.IconInfo

interface GlossaryService {
    suspend fun getGlossaryTerms(): List<GlossaryTerm>
    suspend fun getIcons(): List<IconInfo>
}
