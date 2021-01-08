/**
 * Subproject: "ksdtoolkit-mobapp".
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

plugins {

    id("com.android.application")

    /**
     * !!! Problems if "kotlin-android" and "kotlin-android-extensions" are included with plugin "com.android.application"
     * - SOLUTION: Use full names e.g. "org.jetbrains.kotlin.android.extensions", so that
     *             pluginManagement in settings.gradle.kts can recognize name.
     */
    id("org.jetbrains.kotlin.android")

    id("org.jetbrains.kotlin.android.extensions")
}


android {
    compileSdkVersion(28)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "hr.unipu.mobilesimulatorapp"
        minSdkVersion(24)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testBuildType = "debug"
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}


dependencies {
    implementation(project(path = ":ksdtoolkit-core", configuration = "default"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    api("com.google.android.material:material:1.3.0-alpha04")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.20")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.20")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    testImplementation("junit:junit:4.13.1")
    testImplementation("org.mockito:mockito-core:3.1.0")
    testImplementation("androidx.test:core:1.3.0")

    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("org.hamcrest:hamcrest-library:1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
}


tasks.register("appStart") {
    dependsOn(":ksdtoolkit-mobapp:installDebug")
    doLast {
        exec {
            commandLine("cmd", "/c", "adb", "shell", "am", "start", "-n", "hr.unipu.mobilesimulatorapp/.MobSimulatorApp")
        }
    }
}