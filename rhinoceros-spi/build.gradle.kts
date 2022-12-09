plugins {
    kotlin("jvm")
}

var test: String by rootProject.extra

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation("org.springframework:spring-core:6.0.2")
    implementation("org.springframework:spring-context:6.0.2")
}
