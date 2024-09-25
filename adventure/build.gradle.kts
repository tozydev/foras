plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.forasCore)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.key)
    compileOnly(libs.adventure.text.minimessage)
    testImplementation(libs.mockk)
    testImplementation(libs.adventure.api)
    testImplementation(libs.adventure.key)
}
