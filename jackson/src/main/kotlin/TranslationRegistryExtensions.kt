package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.tozydev.foras.translation.TranslationRegistry
import io.github.tozydev.foras.translation.registerAll
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.walk

@OptIn(ExperimentalPathApi::class)
fun TranslationRegistry(
    directory: Path,
    objectMapper: ObjectMapper,
    defaultLocale: Locale = Locale.ENGLISH,
): TranslationRegistry {
    val resources = mutableMapOf<Locale, MutableList<Path>>()

    fun addResource(path: Path) {
        val locale = parseLocale(path.nameWithoutExtension) ?: return
        resources.getOrPut(locale) { mutableListOf() }.add(path)
        return
    }

    directory.listDirectoryEntries().forEach { path ->
        if (path.isRegularFile()) {
            addResource(path)
        }
        if (path.isDirectory()) {
            path.walk().forEach(::addResource)
        }
    }

    return TranslationRegistry(defaultLocale) {
        resources.forEach { (locale, paths) ->
            paths.forEach { path ->
                path.inputStream().bufferedReader().use {
                    registerAll(MessageMapParser.parse(objectMapper.readTree(it)), locale)
                }
            }
        }
    }
}

private fun parseLocale(value: String): Locale? {
    val parts = value.split("_")
    return when (parts.size) {
        1 -> Locale.of(parts[0])
        2 -> Locale.of(parts[0], parts[1])
        3 -> Locale.of(parts[0], parts[1], parts[2])
        else -> null
    }
}
