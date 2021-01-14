/**
 * Subproject: "ksdtoolkit-core".
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 * @author [Krešimir Pripužić](mailto:kresimir.pripuzic@fer.hr)
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")

    if (JavaVersion.current().isJava11Compatible) {
        id("org.openjfx.javafxplugin") version "0.0.9"
    }
}

tasks.withType<Jar> {
    enabled = true
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    implementation("org.hamcrest:hamcrest-all:1.3")

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")

    implementation("org.slf4j:slf4j-api:1.7.30")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

javafx {
    modules("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.swing")
}

repositories {
    mavenCentral()
}