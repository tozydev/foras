plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.forasCore)
    implementation(libs.jackson.databind)
}
