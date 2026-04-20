package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.GlossaryApi
import dev.abid.noaa.weather.sdk.exception.NoaaParseException
import dev.abid.noaa.weather.sdk.mapper.GlossaryMapper
import dev.abid.noaa.weather.sdk.model.glossary.GlossaryTerm
import dev.abid.noaa.weather.sdk.model.glossary.IconInfo

internal class DefaultGlossaryService(private val api: GlossaryApi) : GlossaryService {

    override suspend fun getGlossaryTerms(): List<GlossaryTerm> {
        return try {
            GlossaryMapper.mapGlossary(api.getGlossary())
        } catch (e: Exception) {
            throw NoaaParseException("Failed to parse glossary", e)
        }
    }

    override suspend fun getIcons(): List<IconInfo> {
        return try {
            GlossaryMapper.mapIcons(api.getIcons())
        } catch (e: Exception) {
            throw NoaaParseException("Failed to parse icons", e)
        }
    }
}
