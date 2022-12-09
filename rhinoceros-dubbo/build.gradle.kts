plugins {
    kotlin("jvm")
}

var test: String by rootProject.extra

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.slf4j:slf4j-api:2.0.5")
    api("com.alibaba.fastjson2:fastjson2:2.0.20")
    implementation("org.apache.skywalking:apm-toolkit-trace:8.13.0")
    implementation("com.alibaba:transmittable-thread-local:2.14.2")

    runtimeOnly("org.aspectj:aspectjrt:1.9.9.1")
}