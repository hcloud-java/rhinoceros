import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.google.protobuf") version "0.9.1"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("java")
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

allprojects {
    group = "com.hcloud"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "application")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    apply(plugin = "idea")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.apache.commons:commons-lang3:3.12.0")
        implementation("org.slf4j:slf4j-api:1.7.25")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

        testImplementation("junit:junit:4.13.2")
//        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }
}

dependencies {
    subprojects.forEach {
        implementation(it)
    }
}

tasks.register<Copy>("installGitHook") {

    dependsOn()
    var suffix = "macos"
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        suffix = "windows"
    }

    from(layout.projectDirectory.file("gradle/scripts/pre-commit-$suffix"))
    into(layout.projectDirectory.file(".git/hooks"))
    rename("pre-commit-$suffix", "pre-commit")
    fileMode = 0b111101101

    from(layout.projectDirectory.file("gradle/scripts/pre-push-$suffix"))
    into(layout.projectDirectory.file(".git/hooks"))
    rename("pre-push-$suffix", "pre-push")
    fileMode = 0b111101101
}

tasks.getByName("compileKotlin").dependsOn(tasks.getByName("installGitHook"))
