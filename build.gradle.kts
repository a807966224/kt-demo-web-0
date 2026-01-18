plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.crash.course"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
        mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:2023.0.1.0")
    }
}

dependencies {

    // WebFlux（Reactive）
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // R2DBC
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Source: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-datetime
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")


    // H2（示例用）
    runtimeOnly("com.h2database:h2")

    // ✅ Nacos 服务发现（关键）
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")

    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    mainClass.set("com.crash.course.ktdemoweb0.AppKt")
}
