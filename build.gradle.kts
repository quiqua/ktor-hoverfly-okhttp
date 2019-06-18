import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.11"
}

group = "eu.quiqua"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/spekframework/spek-dev")
    }
}

extra.apply {
    set("ktor_version", "1.1.1")
    set("logback_version", "1.2.3")
    set("mockk_version", "1.9")
    set("spek_version", "2.0.5")
    set("kotlin_version", "1.3.11")
    set("hamkrest_version", "1.7.0.0")
    set("kodein_version", "6.2.1")
    set("hoverfly_version", "0.12.0")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.ktor:ktor-server-netty:${extra.get("ktor_version")}")
    compile("ch.qos.logback:logback-classic:${extra.get("logback_version")}")
    // KTOR OKHTTP HTTP CLIENT
    implementation("io.ktor:ktor-client-okhttp:${extra.get("ktor_version")}")
    // KODEIN DEPENDENCY INJECTION WITH KTOR EXTENSION
    implementation("org.kodein.di:kodein-di-generic-jvm:${extra.get("kodein_version")}")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:${extra.get("kodein_version")}")

    // HOVERFLY VCR
    testCompile("io.specto:hoverfly-java:${extra.get("hoverfly_version")}")

    testCompile("io.ktor:ktor-server-test-host:${extra.get("ktor_version")}")
    testCompile("io.mockk:mockk:${extra.get("mockk_version")}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${extra.get("spek_version")}") {
        //exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly ("org.spekframework.spek2:spek-runner-junit5:${extra.get("spek_version")}") {
        //exclude(group = "org.junit.platform")
        //exclude(group = "org.jetbrains.kotlin")
    }

    // spek requires kotlin-reflect, can be omitted if already in the classpath
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:${extra.get("kotlin_version")}")
    testImplementation("com.natpryce:hamkrest:${extra.get("hamkrest_version")}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "io.door2door.example.app.ServerKt"
}

tasks.test {
    useJUnitPlatform {
        includeEngines("spek2")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}