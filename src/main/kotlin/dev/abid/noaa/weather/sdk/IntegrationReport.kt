package dev.abid.noaa.weather.sdk

import dev.abid.noaa.weather.sdk.exception.NoaaWeatherException
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.time.Instant

/**
 * Runs integration tests against the live NWS API and generates an HTML evaluation report.
 */
fun main() = runBlocking {
    val results = mutableListOf<ApiResult>()
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            response
        }
        .build()

    val userAgent = "noaa-weather-sdk-kotlin-integration-test, test@example.com"
    val sdk = NoaaWeatherSdk.builder()
        .userAgent(userAgent)
        .enableLogging(false)
        .build()

    val testLat = 39.7456
    val testLon = -104.9984
    val testStation = "KDEN"
    val testZone = "COZ001"
    val testState = "CO"

    // 1. Resolve gridpoint
    val pointResult = rawCall(client, "https://api.weather.gov/points/$testLat,$testLon", userAgent)
    results.add(pointResult)
    var wfo = "BOU"
    var gridX = 65
    var gridY = 61
    if (pointResult.status == 200) {
        try {
            val gson = com.google.gson.Gson()
            val props = gson.fromJson(pointResult.body, Map::class.java)
                ?.get("properties") as? Map<*, *>
            if (props != null) {
                wfo = props["gridId"] as? String ?: wfo
                gridX = (props["gridX"] as? Double)?.toInt() ?: gridX
                gridY = (props["gridY"] as? Double)?.toInt() ?: gridY
            }
        } catch (_: Exception) {}
    }

    // 2. Daily forecast
    val dailyResult = rawCall(client, "https://api.weather.gov/gridpoints/$wfo/$gridX,$gridY/forecast", userAgent)
    results.add(dailyResult.copy(name = "Daily Forecast (gridpoint)"))

    // 3. Hourly forecast
    val hourlyResult = rawCall(client, "https://api.weather.gov/gridpoints/$wfo/$gridX,$gridY/forecast/hourly", userAgent)
    results.add(hourlyResult.copy(name = "Hourly Forecast (gridpoint)"))

    // 4. Zone forecast
    val zoneResult = rawCall(client, "https://api.weather.gov/zones/forecast/$testZone/forecast", userAgent)
    results.add(zoneResult.copy(name = "Zone Forecast"))

    // 5. Active alerts
    val alertsResult = rawCall(client, "https://api.weather.gov/alerts?status=actual&limit=5", userAgent)
    results.add(alertsResult.copy(name = "Active Alerts"))

    // 6. Alerts by area
    val alertsAreaResult = rawCall(client, "https://api.weather.gov/alerts?area=$testState&limit=5", userAgent)
    results.add(alertsAreaResult.copy(name = "Alerts by Area ($testState)"))

    // 7. Nearby stations
    val stationsResult = rawCall(client, "https://api.weather.gov/points/$testLat,$testLon/stations", userAgent)
    results.add(stationsResult.copy(name = "Nearby Stations"))

    // 8. Latest observation
    val obsResult = rawCall(client, "https://api.weather.gov/stations/$testStation/observations/latest", userAgent)
    results.add(obsResult.copy(name = "Latest Observation ($testStation)"))

    // 9. Observations
    val obsListResult = rawCall(client, "https://api.weather.gov/stations/$testStation/observations?limit=3", userAgent)
    results.add(obsListResult.copy(name = "Observations ($testStation)"))

    // 10. Glossary
    val glossaryResult = rawCall(client, "https://api.weather.gov/glossary", userAgent)
    results.add(glossaryResult.copy(name = "Glossary"))

    // 11. Icons
    val iconsResult = rawCall(client, "https://api.weather.gov/icons", userAgent)
    results.add(iconsResult.copy(name = "Icons"))

    // SDK-level tests
    val sdkResults = mutableListOf<SdkResult>()

    sdkResults.add(testSdkCall("SDK: getDailyForecast") {
        val f = sdk.forecasts.getDailyForecast(testLat, testLon)
        "${f.periods.size} periods — first: ${f.periods.firstOrNull()?.name}: ${f.periods.firstOrNull()?.shortForecast}"
    })

    sdkResults.add(testSdkCall("SDK: getHourlyForecast") {
        val h = sdk.forecasts.getHourlyForecast(testLat, testLon)
        "${h.size} hourly periods returned"
    })

    sdkResults.add(testSdkCall("SDK: getGridpointForecast") {
        val g = sdk.forecasts.getGridpointForecast(wfo, gridX, gridY)
        "${g.periods.size} periods for gridpoint ${g.gridPoint.wfo}/${g.gridPoint.gridX},${g.gridPoint.gridY}"
    })

    sdkResults.add(testSdkCall("SDK: getZoneForecast") {
        val z = sdk.forecasts.getZoneForecast(testZone)
        "${z.periods.size} periods for zone ${z.zoneId}"
    })

    sdkResults.add(testSdkCall("SDK: getActiveAlerts") {
        val a = sdk.alerts.getActiveAlerts()
        "${a.size} active alerts"
    })

    sdkResults.add(testSdkCall("SDK: getAlertsByArea($testState)") {
        val a = sdk.alerts.getAlertsByArea(testState)
        "${a.size} alerts for $testState"
    })

    sdkResults.add(testSdkCall("SDK: getAlertsByZone($testZone)") {
        val a = sdk.alerts.getAlertsByZone(testZone)
        "${a.size} alerts for zone $testZone"
    })

    sdkResults.add(testSdkCall("SDK: getNearbyStations") {
        val s = sdk.observations.getNearbyStations(testLat, testLon)
        "${s.size} stations — first: ${s.firstOrNull()?.id} (${s.firstOrNull()?.name})"
    })

    sdkResults.add(testSdkCall("SDK: getLatestObservation($testStation)") {
        val o = sdk.observations.getLatestObservation(testStation)
        "Temp: ${o.temperature} ${o.temperatureUnit}, Desc: ${o.weatherDescription}"
    })

    sdkResults.add(testSdkCall("SDK: getObservations($testStation)") {
        val o = sdk.observations.getObservations(testStation)
        "${o.size} observations returned"
    })

    sdkResults.add(testSdkCall("SDK: getGlossaryTerms") {
        val t = sdk.glossary.getGlossaryTerms()
        "${t.size} terms — first: ${t.firstOrNull()?.term}"
    })

    sdkResults.add(testSdkCall("SDK: getIcons") {
        val i = sdk.glossary.getIcons()
        "${i.size} icons returned"
    })

    // Generate HTML
    val html = generateHtml(results, sdkResults)
    val outFile = File("evaluation-report.html")
    outFile.writeText(html)
    println("Report written to ${outFile.absolutePath}")
}

private fun rawCall(client: OkHttpClient, url: String, userAgent: String): ApiResult {
    return try {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "application/geo+json")
            .build()
        val start = System.currentTimeMillis()
        val response = client.newCall(request).execute()
        val elapsed = System.currentTimeMillis() - start
        val body = response.body?.string() ?: ""
        ApiResult(
            name = url.substringAfter("api.weather.gov"),
            url = url,
            status = response.code,
            latencyMs = elapsed,
            body = body,
            error = null
        )
    } catch (e: Exception) {
        ApiResult(
            name = url.substringAfter("api.weather.gov"),
            url = url,
            status = -1,
            latencyMs = -1,
            body = "",
            error = e.message
        )
    }
}

private suspend fun testSdkCall(name: String, block: suspend () -> String): SdkResult {
    return try {
        val start = System.currentTimeMillis()
        val summary = block()
        val elapsed = System.currentTimeMillis() - start
        SdkResult(name = name, success = true, latencyMs = elapsed, summary = summary, error = null)
    } catch (e: NoaaWeatherException) {
        SdkResult(name = name, success = false, latencyMs = -1, summary = null, error = "${e::class.simpleName}: ${e.message}")
    } catch (e: Exception) {
        SdkResult(name = name, success = false, latencyMs = -1, summary = null, error = "${e::class.simpleName}: ${e.message}")
    }
}

data class ApiResult(
    val name: String,
    val url: String,
    val status: Int,
    val latencyMs: Long,
    val body: String,
    val error: String?
)

data class SdkResult(
    val name: String,
    val success: Boolean,
    val latencyMs: Long,
    val summary: String?,
    val error: String?
)

private fun generateHtml(apiResults: List<ApiResult>, sdkResults: List<SdkResult>): String {
    val now = Instant.now()
    val passCount = apiResults.count { it.status in 200..299 } + sdkResults.count { it.success }
    val totalCount = apiResults.size + sdkResults.size
    val failCount = totalCount - passCount

    val allLatencies = (apiResults.map { it.latencyMs } + sdkResults.mapNotNull { if (it.latencyMs > 0) it.latencyMs else null })
        .filter { it > 0 }
    val avgLatency = if (allLatencies.isNotEmpty()) "%.0f ms".format(allLatencies.average()) else "N/A"

    fun escapeHtml(s: String) = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

    fun formatJson(body: String): String {
        return try {
            val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
            val elem = gson.fromJson(body, Any::class.java)
            escapeHtml(gson.toJson(elem))
        } catch (_: Exception) {
            escapeHtml(body.take(5000))
        }
    }

    // Build API table rows
    val apiRows = StringBuilder()
    apiResults.forEachIndexed { i, r ->
        val statusBadge = when {
            r.status == -1 -> "<span class=\"badge badge-err\">ERROR</span>"
            r.status in 500..599 -> "<span class=\"badge badge-5xx\">${r.status}</span>"
            r.status in 400..499 -> "<span class=\"badge badge-4xx\">${r.status}</span>"
            else -> "<span class=\"badge badge-2xx\">${r.status}</span>"
        }
        val latency = if (r.latencyMs > 0) "${r.latencyMs} ms" else "—"
        val bodyPreview = if (r.error != null) escapeHtml(r.error) else formatJson(r.body)
        apiRows.append("""
        <tr>
          <td>${i + 1}</td>
          <td><span class="mono endpoint">${escapeHtml(r.url)}</span></td>
          <td>${statusBadge}</td>
          <td class="latency">${latency}</td>
          <td>
            <span class="collapsible" onclick="toggleResponse('api-${i}')">Show/Hide Response</span>
            <div id="api-${i}" class="response-container">
              <div class="response-block">${bodyPreview}</div>
            </div>
          </td>
        </tr>
        """.trimIndent())
    }

    // Build SDK table rows
    val sdkRows = StringBuilder()
    sdkResults.forEachIndexed { i, r ->
        val badge = if (r.success) "<span class=\"badge badge-pass\">PASS</span>" else "<span class=\"badge badge-fail\">FAIL</span>"
        val latency = if (r.latencyMs > 0) "${r.latencyMs} ms" else "—"
        val detail = if (r.success) "<span class=\"sdk-summary\">${escapeHtml(r.summary ?: "")}</span>"
            else "<span class=\"sdk-error\">${escapeHtml(r.error ?: "Unknown error")}</span>"
        sdkRows.append("""
        <tr>
          <td>${i + 1}</td>
          <td class="mono">${escapeHtml(r.name)}</td>
          <td>${badge}</td>
          <td class="latency">${latency}</td>
          <td>${detail}</td>
        </tr>
        """.trimIndent())
    }

    return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>NOAA Weather SDK — Integration Evaluation Report</title>
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f0f2f5; color: #1a1a2e; line-height: 1.6; }
  .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
  h1 { font-size: 1.8rem; margin-bottom: 4px; color: #1a1a2e; }
  .subtitle { color: #666; margin-bottom: 24px; font-size: 0.95rem; }
  .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 32px; }
  .card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
  .card.total { border-left: 4px solid #3b82f6; }
  .card.passed { border-left: 4px solid #22c55e; }
  .card.failed { border-left: 4px solid #ef4444; }
  .card.latency { border-left: 4px solid #f59e0b; }
  .card .label { font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; color: #888; margin-bottom: 4px; }
  .card .value { font-size: 2rem; font-weight: 700; }
  .card.total .value { color: #3b82f6; }
  .card.passed .value { color: #22c55e; }
  .card.failed .value { color: #ef4444; }
  .card.latency .value { color: #f59e0b; font-size: 1.4rem; }
  h2 { font-size: 1.3rem; margin: 32px 0 16px; color: #1a1a2e; display: flex; align-items: center; gap: 8px; }
  .section-icon { font-size: 1.1rem; }
  table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.08); margin-bottom: 24px; }
  th { background: #1a1a2e; color: #fff; text-align: left; padding: 12px 16px; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px; }
  td { padding: 12px 16px; border-bottom: 1px solid #eee; font-size: 0.9rem; vertical-align: top; }
  tr:last-child td { border-bottom: none; }
  tr:hover { background: #f8f9fa; }
  .badge { display: inline-block; padding: 3px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }
  .badge-pass { background: #dcfce7; color: #166534; }
  .badge-fail { background: #fee2e2; color: #991b1b; }
  .badge-2xx { background: #dcfce7; color: #166534; }
  .badge-4xx { background: #fef3c7; color: #92400e; }
  .badge-5xx { background: #fee2e2; color: #991b1b; }
  .badge-err { background: #fee2e2; color: #991b1b; }
  .mono { font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace; font-size: 0.82rem; }
  .latency { color: #666; font-size: 0.85rem; }
  .response-block { background: #1e1e2e; color: #cdd6f4; border-radius: 8px; padding: 16px; margin-top: 8px; max-height: 400px; overflow-y: auto; font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace; font-size: 0.8rem; line-height: 1.5; white-space: pre-wrap; word-break: break-all; }
  .collapsible { cursor: pointer; user-select: none; }
  .collapsible:hover { color: #3b82f6; }
  .response-container { display: none; }
  .response-container.open { display: block; }
  .endpoint { color: #3b82f6; word-break: break-all; }
  .sdk-summary { color: #166534; }
  .sdk-error { color: #991b1b; }
</style>
</head>
<body>
<div class="container">
  <h1>NOAA Weather SDK &mdash; Integration Evaluation Report</h1>
  <p class="subtitle">Generated ${escapeHtml(now.toString())} &bull; Test coordinates: 39.7456, -104.9984 (Denver, CO)</p>

  <div class="summary">
    <div class="card total">
      <div class="label">Total Checks</div>
      <div class="value">${totalCount}</div>
    </div>
    <div class="card passed">
      <div class="label">Passed</div>
      <div class="value">${passCount}</div>
    </div>
    <div class="card failed">
      <div class="label">Failed</div>
      <div class="value">${failCount}</div>
    </div>
    <div class="card latency">
      <div class="label">Avg Latency</div>
      <div class="value">${avgLatency}</div>
    </div>
  </div>

  <h2><span class="section-icon">&#9432;</span> Raw API Endpoints</h2>
  <table>
    <tr>
      <th>#</th>
      <th>Endpoint</th>
      <th>Status</th>
      <th>Latency</th>
      <th>Response</th>
    </tr>
    ${apiRows}
  </table>

  <h2><span class="section-icon">&#9889;</span> SDK Service Calls</h2>
  <table>
    <tr>
      <th>#</th>
      <th>Method</th>
      <th>Status</th>
      <th>Latency</th>
      <th>Result</th>
    </tr>
    ${sdkRows}
  </table>

</div>

<script>
function toggleResponse(id) {
  const el = document.getElementById(id);
  if (el) el.classList.toggle('open');
}
</script>
</body>
</html>
""".trimIndent()
}
