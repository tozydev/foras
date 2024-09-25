plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.forasCore)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.yaml)
}
