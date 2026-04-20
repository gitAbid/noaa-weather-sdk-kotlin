package dev.abid.noaa.weather.sdk.mapper

import dev.abid.noaa.weather.sdk.api.NwsGlossaryResponse
import dev.abid.noaa.weather.sdk.api.NwsRawIconsResponse
import dev.abid.noaa.weather.sdk.model.glossary.GlossaryTerm
import dev.abid.noaa.weather.sdk.model.glossary.IconInfo

internal object GlossaryMapper {
    fun mapGlossary(response: NwsGlossaryResponse): List<GlossaryTerm> {
        return response.glossary.map { GlossaryTerm(it.term, it.definition) }
    }

    fun mapIcons(response: NwsRawIconsResponse): List<IconInfo> {
        val result = mutableListOf<IconInfo>()
        response.icons.forEach { (theme, innerMap) ->
            innerMap.forEach { (id, def) ->
                result.add(IconInfo(
                    id = "$theme:$id",
                    url = "https://api.weather.gov/icons/$theme/$id",
                    description = def.description
                ))
            }
        }
        return result
    }
}
