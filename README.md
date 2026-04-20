# NOAA Weather SDK for Kotlin

A Kotlin/JVM SDK for the [National Weather Service (NWS) API](https://api.weather.gov/). Provides a developer-friendly, coroutine-based interface for fetching weather forecasts, alerts, observations, and glossary data.

## Features

- **Forecasts** — Daily and hourly forecasts by coordinates, gridpoint, or zone
- **Alerts** — Active weather alerts filtered by area, zone, or ID
- **Observations** — Current conditions from weather stations
- **Glossary** — NWS term definitions and weather icons
- **Coroutine-first** — All service methods are `suspend` functions
- **Clean models** — NWS GeoJSON is unwrapped into idiomatic Kotlin data classes
- **Builder pattern** — Simple configuration with sensible defaults
- **Structured errors** — Typed exception hierarchy for API, rate limit, network, and parse errors

## Quick Start

### Setup

```kotlin
val sdk = NoaaWeatherSdk.builder()
    .userAgent("my-weather-app, contact@example.com")  // Required by NWS
    .enableLogging(true)                                 // Optional
    .build()
```

The `userAgent` is required by the NWS API. Use the format `"app-name, contact-info"`.

### Forecasts

```kotlin
// Get daily forecast by coordinates (resolves gridpoint automatically)
val forecast = sdk.forecasts.getDailyForecast(39.7456, -104.9984)
forecast.periods.forEach { period ->
    println("${period.name}: ${period.temperature}${period.temperatureUnit} — ${period.shortForecast}")
}

// Get hourly forecast
val hourly = sdk.forecasts.getHourlyForecast(39.7456, -104.9984)

// Get forecast by gridpoint (skip the location lookup)
val grid = sdk.forecasts.getGridpointForecast("BOU", 65, 61)

// Get forecast by zone
val zone = sdk.forecasts.getZoneForecast("COZ001")
```

### Alerts

```kotlin
// All active alerts
val allAlerts = sdk.alerts.getActiveAlerts()

// Filter by state (2-letter code)
val coAlerts = sdk.alerts.getAlertsByArea("CO")

// Filter by zone
val zoneAlerts = sdk.alerts.getAlertsByZone("COZ001")

// Single alert by ID
val alert = sdk.alerts.getAlert("urn:oid:2.49.0.1.840.0.example")
println("${alert.event}: ${alert.headline}")
println("Severity: ${alert.severity}, Urgency: ${alert.urgency}")
```

### Observations

```kotlin
// Find nearby stations
val stations = sdk.observations.getNearbyStations(39.86, -104.67)
stations.forEach { println("${it.id} — ${it.name}") }

// Latest observation from a station
val obs = sdk.observations.getLatestObservation("KDEN")
println("Temp: ${obs.temperature} ${obs.temperatureUnit}")
println("Wind: ${obs.windSpeed}, Humidity: ${obs.relativeHumidity}%")

// Historical observations
val history = sdk.observations.getObservations("KDEN")
```

### Glossary

```kotlin
val terms = sdk.glossary.getGlossaryTerms()
terms.forEach { println("${it.term}: ${it.definition}") }
```

## Configuration

| Option | Default | Description |
|--------|---------|-------------|
| `userAgent` | — | **Required.** App identifier for NWS API. |
| `baseUrl` | `https://api.weather.gov` | API base URL. Override for testing. |
| `connectTimeout` | `10s` | HTTP connection timeout. |
| `readTimeout` | `30s` | HTTP read timeout. |
| `enableLogging` | `false` | Enable OkHttp request/response logging. |

```kotlin
val sdk = NoaaWeatherSdk.builder()
    .userAgent("my-app, me@example.com")
    .baseUrl("http://localhost:8080")        // For testing with MockWebServer
    .connectTimeout(5.seconds)
    .readTimeout(15.seconds)
    .enableLogging(true)
    .build()
```

## Error Handling

All errors are subclasses of `NoaaWeatherException`:

| Exception | When |
|-----------|------|
| `NoaaApiException` | NWS API returned a non-2xx response. Check `statusCode` and `errorBody`. |
| `NoaaRateLimitException` | HTTP 429. Check `retryAfter` for suggested wait time. |
| `NoaaNetworkException` | Network failure (timeout, DNS, connection refused). |
| `NoaaParseException` | Response body could not be parsed. Check `rawBody`. |

```kotlin
try {
    val forecast = sdk.forecasts.getDailyForecast(39.7456, -104.9984)
} catch (e: NoaaRateLimitException) {
    println("Rate limited. Retry after: ${e.retryAfter}")
} catch (e: NoaaApiException) {
    println("API error ${e.statusCode}: ${e.message}")
} catch (e: NoaaWeatherException) {
    println("Weather SDK error: ${e.message}")
}
```

## Requirements

- Kotlin 1.9+
- Java 11+
- No API key required (NWS API is free and public)

## Dependencies

- [OkHttp 4.12](https://square.github.io/okhttp/) — HTTP client
- [Retrofit 2.11](https://square.github.io/retrofit/) — REST client
- [Gson](https://github.com/google/gson) — JSON parsing
- [Kotlin Coroutines 1.9](https://github.com/Kotlin/kotlinx.coroutines) — Async support

## Building

```bash
./gradlew build
```

## Testing

```bash
./gradlew test
```

Tests use [MockWebServer](https://square.github.io/okhttp/#mockwebserver) to simulate NWS API responses. No network access required.

## Project Structure

```
src/main/kotlin/dev/abid/noaa/weather/sdk/
├── NoaaWeatherSdk.kt              # Entry point + builder
├── NoaaWeatherConfig.kt           # Configuration
├── api/                           # Internal Retrofit interfaces + DTOs
├── exception/                     # Exception hierarchy
├── mapper/                        # NWS response → public model mappers
├── model/                         # Public data models
│   ├── alert/
│   ├── common/
│   ├── forecast/
│   ├── glossary/
│   └── observation/
└── service/                       # Public service interfaces + implementations
```

## License

This project is provided as-is. The NWS API is a public service operated by the National Oceanic and Atmospheric Administration. This SDK is not affiliated with or endorsed by NOAA.
