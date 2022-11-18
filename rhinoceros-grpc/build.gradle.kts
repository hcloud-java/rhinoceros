import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    id("java")
    id("com.google.protobuf")
    id("idea")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:21.0-rc-1"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.51.0"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") { }
            }
        }
    }
}

var test: String by rootProject.extra

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.21.9")
    implementation("io.grpc:grpc-stub:1.50.2")
    implementation("io.grpc:grpc-protobuf:1.50.2")
    if (JavaVersion.current().isJava9Compatible) {
        // Workaround for @javax.annotation.Generated
        // see: https://github.com/grpc/grpc-java/issues/3633
        implementation("javax.annotation:javax.annotation-api:1.3.1")
    }
    testImplementation("junit:junit:4.13.2")
}
