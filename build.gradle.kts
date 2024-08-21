plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.8.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.yaml:snakeyaml:2.0")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.example.MainKt") // メインクラスを指定
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}