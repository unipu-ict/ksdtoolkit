/**
 * Root project.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

plugins {

}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url="https://maven.google.com")
        maven(url="https://jitpack.io")
    }
}

group = "hr.unpu"
version = "1.0-SNAPSHOT"