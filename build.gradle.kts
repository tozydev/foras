@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.maven.publish) apply false
}

val publishProjectNames = setOf(projects.forasCore, projects.forasAdventure, projects.forasJackson).map { it.name }

val kotlinJvmId =
    libs.plugins.kotlin.jvm
        .get()
        .pluginId
val ktlintId =
    libs.plugins.ktlint
        .get()
        .pluginId
val mavenPublishId =
    libs.plugins.maven.publish
        .get()
        .pluginId
subprojects {
    if (name in publishProjectNames) {
        apply(plugin = mavenPublishId)
    }
    plugins.withId(kotlinJvmId) {
        apply(plugin = ktlintId)

        dependencies {
            "compileOnly"(kotlin("stdlib"))
        }

        val jdkVersion: String by project
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(jdkVersion)
            }
        }
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions {
                jvmTarget = JvmTarget.fromTarget(jdkVersion)
            }
        }

        extensions.configure<TestingExtension> {
            suites {
                named<JvmTestSuite>(JavaPlugin.TEST_TASK_NAME) {
                    useKotlinTest(libs.versions.kotlin)
                }
            }
        }
    }

    repositories {
        mavenCentral()
    }

    plugins.withType<com.vanniktech.maven.publish.MavenPublishPlugin> {
        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "vela"
                    url =
                        if (version.toString().endsWith("-SNAPSHOT")) {
                            uri("https://vela.nguyenthanhtan.id.vn/snapshots/")
                        } else {
                            uri("https://vela.nguyenthanhtan.id.vn/releases/")
                        }
                    credentials(PasswordCredentials::class)
                }
            }
        }

        extensions.configure<MavenPublishBaseExtension> {
            signAllPublications()
            pom {
                inceptionYear.set("2024")
                url = "https://github.com/tozydev/foras/"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://github.com/tozydev/foras/blob/main/LICENSE"
                        distribution = "repo"
                    }
                }
                developers {
                    developer {
                        id = "tozydev"
                        name = "Nguyễn Thanh Tân"
                        email = "tozydev@nguyenthanhtan.id.vn"
                        url = "https://nguyenthanhtan.id.vn"
                    }
                }
                scm {
                    url = "https://github.com/tozydev/foras/"
                    connection = "scm:git:git://github.com/tozydev/foras.git"
                    developerConnection = "scm:git:git://github.com/tozydev/foras.git"
                }
            }
        }
    }
}
