plugins {
    kotlin("jvm") version "1.9.23"
    `maven-publish`
    application
}

group = "dev.abid.noaa.weather"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.abid.noaa.weather.sdk.IntegrationReportKt")
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("NOAA Weather SDK for Kotlin")
                description.set("A developer-friendly Kotlin SDK for the National Weather Service API")
                url.set("https://github.com/gitAbid/noaa-weather-sdk-kotlin")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("gitAbid")
                        name.set("Abid Hasan")
                        url.set("https://abidhasan.tech")
                    }
                }
                scm {
                    url.set("https://github.com/gitAbid/noaa-weather-sdk-kotlin")
                    connection.set("scm:git:git://github.com/gitAbid/noaa-weather-sdk-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/gitAbid/noaa-weather-sdk-kotlin.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gitAbid/noaa-weather-sdk-kotlin")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
