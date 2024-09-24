@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.paperweight.userdev) apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.shadow) apply false
}

val kotlinJvmId =
    libs.plugins.kotlin.jvm
        .get()
        .pluginId
val ktlintId =
    libs.plugins.ktlint
        .get()
        .pluginId
subprojects {
    plugins.withId(kotlinJvmId) {
        apply(plugin = ktlintId)

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
}
