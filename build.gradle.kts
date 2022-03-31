import java.io.ByteArrayOutputStream

val coroutinesVersion = "1.5.0"
val serializationVersion = "1.2.1"
val datetimeVersion = "0.2.1"
val ktorVersion = "1.6.1"
val reactWrapperVersion = "17.0.2-pre.214-kotlin-1.5.20"
val reactStyledWrapperVersion = "5.3.0-pre.214-kotlin-1.5.20"


plugins {
    kotlin("multiplatform") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "no.calmeyersgt4"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }
    js(IR) {
        browser {
            binaries.executable()

            distribution {
                directory = file("$projectDir/build/distributions/browser")
            }
        }
    }
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlin.js.ExperimentalJsExport")
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetimeVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-apache:$ktorVersion")
                implementation("com.auth0:java-jwt:3.17.0")

                // Interop with CompletableFuture
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.0")

                // AWS:
                implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
                implementation(project.dependencies.platform("software.amazon.awssdk:bom:2.16.93"))
                implementation("software.amazon.awssdk:dynamodb")
                implementation("software.amazon.awssdk:dynamodb-enhanced")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")

                //Time zones
                implementation(npm("@js-joda/timezone", "2.3.0"))

                //React, React DOM + Wrappers
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactWrapperVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactWrapperVersion")

                //Kotlin Styled (chapter 3)
                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:$reactStyledWrapperVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

application {
    mainClassName = "LambdaHandlerKt"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("shadow")
    archiveVersion.set("")
}

// Custom tasks for sls deployment
tasks.create("package") {
    group = "deploy"
    dependsOn(tasks.getByName("installShadowDist"))
    dependsOn(tasks.getByName("jsBrowserDistribution"))
}
tasks.create("deploy") {
    group = "deploy"
    dependsOn(tasks.getByName("package"))
    doLast {
        cmd("sls", "deploy")
    }
}


fun cmd(vararg args: String, captureOutput: Boolean = false) = ByteArrayOutputStream().use {
    exec {
        commandLine(*args)
        if (captureOutput) standardOutput = it
    }
    it.toString()
}

// Does not update resources. Stack out of sync.
tasks.create("deployFunctionsAndSite") {
    group = "deploy"
    dependsOn(tasks.getByName("package"))
    doLast {
        cmd("sls", "deploy", "list", "functions", captureOutput = true)
            .also { println(it) }
            .split("\n")
            .let { it.subList(2, it.lastIndex) }
            .map { it.split(": ")[1] }
            .forEach {
                cmd("sls", "deploy", "function", "-f", it)
            }
        cmd("sls", "s3sync")
    }
}