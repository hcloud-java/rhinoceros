plugins {
    kotlin("jvm")
}

var test: String by rootProject.extra

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {

}