package dev.abid.noaa.weather.sdk.service

import dev.abid.noaa.weather.sdk.api.GlossaryApi
import dev.abid.noaa.weather.sdk.exception.NoaaParseException
import dev.abid.noaa.weather.sdk.mapper.GlossaryMapper
import dev.abid.noaa.weather.sdk.model.glossary.GlossaryTerm
import dev.abid.noaa.weather.sdk.model.glossary.IconInfo
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class DefaultGlossaryService(private val api: GlossaryApi) : GlossaryService {

    override suspend fun getGlossaryTerms(): List<GlossaryTerm> {
        logger.debug { "getGlossaryTerms: fetching glossary" }
        return try {
            val terms = GlossaryMapper.mapGlossary(api.getGlossary())
            logger.info { "getGlossaryTerms: returned ${terms.size} terms" }
            terms
        } catch (e: Exception) {
            logger.error(e) { "getGlossaryTerms: failed to parse glossary response" }
            throw NoaaParseException("Failed to parse glossary", e)
        }
    }

    override suspend fun getIcons(): List<IconInfo> {
        logger.debug { "getIcons: fetching icon metadata" }
        return try {
            val icons = GlossaryMapper.mapIcons(api.getIcons())
            logger.info { "getIcons: returned ${icons.size} icons" }
            icons
        } catch (e: Exception) {
            logger.error(e) { "getIcons: failed to parse icons response" }
            throw NoaaParseException("Failed to parse icons", e)
        }
    }
}
