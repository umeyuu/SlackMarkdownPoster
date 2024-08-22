plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.8.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.0" // shadowプラグインの追加
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

// JARタスクの設定
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.MainKt"
    }
}

// Shadow JARタスクの設定
tasks.shadowJar {
    archiveBaseName.set("Markdown_Converter")
    archiveClassifier.set("")
    archiveVersion.set("1.0-SNAPSHOT")
}
