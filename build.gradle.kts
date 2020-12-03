import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.10"
}
group = "me.vlad"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val ktor_version = "1.4.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("org.jetbrains.exposed:exposed-core:0.24.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.24.1")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}