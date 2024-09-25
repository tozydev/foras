plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.forasCore)
    implementation(libs.adventure.api)
    implementation(libs.adventure.key)
    compileOnly(libs.adventure.text.minimessage)
    testImplementation(libs.mockk)
}
