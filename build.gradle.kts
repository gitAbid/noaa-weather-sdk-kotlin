plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "dev.abid.noaa.weather"
version = "1.0.0-SNAPSHOT"

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
