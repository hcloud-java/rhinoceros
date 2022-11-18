plugins {
    kotlin("jvm")
}


var test: String by rootProject.extra

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    api("com.amazonaws:aws-java-sdk-s3:1.12.344")
    implementation("org.springframework.boot:spring-boot-starter-web")
}