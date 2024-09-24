plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "paper-template"

// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

subproject("plugin", "paperPlugin")

fun subproject(module: String, dir: String = module, prefix: String = "${rootProject.name}-") {
    val name = "$prefix$module"
    include(name)
    project(":$name").projectDir = file(dir)
}
