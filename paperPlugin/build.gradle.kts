@file:Suppress("UnstableApiUsage")

import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
}

paperweight {
    reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

tasks {
    val processPaperPluginYml by registering(ProcessResources::class)
    afterEvaluate {
        processPaperPluginYml {
            mustRunAfter(processResources)
            from(sourceSets.main.map { it.resources })
            filesMatching("paper-plugin.yml") {
                filteringCharset = Charsets.UTF_8.name()
                expand(
                    "version" to project.version,
                    "name" to project.findProperty("pluginName"),
                )
            }
            into(sourceSets.main.map { it.output.resourcesDir!! })
        }
        jar {
            dependsOn(processPaperPluginYml)
        }
        shadowJar {
            configurations = listOf(project.configurations.shadow.get())

            dependsOn(processPaperPluginYml)
        }

        if (hasProperty("dev") || System.getenv("DEV") != null) {
            tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
                val jdkVersion: String by project
                javaLauncher = javaToolchains.launcherFor {
                    vendor = JvmVendorSpec.JETBRAINS
                    languageVersion = JavaLanguageVersion.of(jdkVersion)
                }
                jvmArgs("-XX:+AllowEnhancedClassRedefinition")
            }
        }
    }
}
