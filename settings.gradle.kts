plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "foras"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

subproject("core")
subproject("adventure")
subproject("jackson")

fun subproject(
    module: String,
    dir: String = module,
    prefix: String = "${rootProject.name}-",
) {
    val name = "$prefix$module"
    include(name)
    project(":$name").projectDir = file(dir)
}
