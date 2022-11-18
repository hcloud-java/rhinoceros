plugins {
    kotlin("jvm")
}


var test: String by rootProject.extra

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus:1.10.1")
}