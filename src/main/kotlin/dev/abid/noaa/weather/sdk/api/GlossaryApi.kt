package dev.abid.noaa.weather.sdk.api

import retrofit2.http.GET

internal interface GlossaryApi {
    @GET("/glossary")
    suspend fun getGlossary(): NwsGlossaryResponse

    @GET("/icons")
    suspend fun getIcons(): NwsRawIconsResponse
}
