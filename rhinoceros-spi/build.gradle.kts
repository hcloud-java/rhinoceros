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
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.slf4j:slf4j-api:2.0.5")

}