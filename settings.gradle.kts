rootProject.name = "ksdtoolkit"

include("ksdtoolkit-core")
include("ksdtoolkit-mobapp")
include("ksdtoolkit-webapp")


pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        jcenter()
        mavenLocal()
    }

    val kotlinVersion = "1.4.20"
    val androidGradlePluginVersion = "4.0.2"

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
        id("org.jetbrains.kotlin.android") version kotlinVersion apply false
        id("org.jetbrains.kotlin.android.extensions") version kotlinVersion apply false

        id("com.android.application") version androidGradlePluginVersion apply false
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${androidGradlePluginVersion}")
            }
            if (requested.id.namespace == "com.google.gms") {
                useModule("com.google.gms:${requested.id.name}:${requested.version}")
            }
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
                useVersion("${kotlinVersion}")
            }
        }
    }
}