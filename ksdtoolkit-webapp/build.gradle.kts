/**
 * Subproject: "ksdtoolkit-webapp".
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.jetbrains.kotlin.jvm")

    id("org.gretty") version "3.0.3"

    id("com.devsoap.plugin.vaadin") version "2.0.0.beta2"
}

defaultTasks("clean", "build")

repositories {
    mavenCentral()
    jcenter()
    maven(url = "http://maven.vaadin.com/vaadin-addons" )   // vaadin-addons
}


tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}

vaadin {
    version = "8.5.2"
}

gretty {
    contextPath = "/"
    servletContainer = "jetty9.4"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {

        exceptionFormat = TestExceptionFormat.FULL
    }
}

val staging by configurations.creating

dependencies {
    implementation(project(path = ":ksdtoolkit-core", configuration = "default"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.apache.poi:poi:3.15")

    implementation("com.github.mvysny.karibudsl:karibu-dsl-v8:1.0.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation("org.slf4j:jul-to-slf4j:1.7.30")

    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v8:1.2.6")
    testImplementation("com.github.mvysny.dynatest:dynatest-engine:0.19")

    implementation("com.vaadin:vaadin-themes:${vaadin.version}")
    implementation("com.vaadin:vaadin-client-compiled:${vaadin.version}")

    implementation("com.vaadin:vaadin-push:${vaadin.version}")
    implementation("com.vaadin:vaadin-server:${vaadin.version}")
    implementation("javax.servlet:javax.servlet-api:4.0.1")

    implementation("com.vaadin:vaadin-charts:4.0.5")
    implementation("com.vaadin:vaadin-spreadsheet:2.0.1")

    implementation("org.vaadin.addons:dcharts-widget:1.7.0")

    runtimeOnly("com.google.gwt:gwt-servlet:2.8.2")

    staging("com.heroku:webapp-runner-main:9.0.36.1")
}


tasks {
    val copyToLib by registering(Copy::class) {
        into("$buildDir/server")
        from(staging) {
            include("webapp-runner*")
        }
    }
    val stage by registering {
        dependsOn("build", copyToLib)
    }
}

tasks.withType<Jar> {
    enabled = true
}

tasks.withType<com.devsoap.plugin.tasks.CompileWidgetsetTask> {
    doFirst {
        setJvmArgs("-Dvaadin.spreadsheet.developer.license=\${.vaadin.charts.developer.license}",
            "-Dvaadin.charts.developer.license=\${.vaadin.spreadsheet.developer.license}")
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}















/**
 * How to add breakpoint for debugging in Gradle+gretty?
 * - Start the gretty plug-in, appStartDebug, run directly (normal mode), without debugging.
 * - Set up remote debugging. Add Remote, click on configuration.
 *   The configuration port is synchronized with the project gretty configuration: debugPort=5005
 *   Select the corresponding module.
 * - Perform debugging.
 *   Start the web project first (in Normal mode), then start the remote just configured (in Debug mode),
 *   and then complete gretty remote debugging.
 * - Project can be started from Terminal as well:
 *   e.g. gradlew tasks -Dorg.gradle.debug=true --no-daemon
 * - To stop all Gradle daemons:
 *   e.g. gradlew --stop
 * - Run 'gradle appStop' to stop the server.
 */

gretty {
    jvmArgs = mutableListOf("-Xmx1024m", "-XX:MaxPermSize=512m")
    servletContainer = "jetty9.4"
    contextPath = "/"
    scanInterval = 0
    inplaceMode = "hard"
    debugPort = 5005
    debugSuspend = true
}