/*
 * Copyright 2024 Nguyễn Thanh Tân (tozydev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.tozydev.foras.translation.TranslationRegistry
import io.github.tozydev.foras.translation.registerAll
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.bufferedReader
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

/**
 * A default [Locale] mapper that attempts to create a [Locale] instance from a file name.
 *
 * The file name is split by "_" and parsed into language, country, and variant components
 * of a [Locale].
 *
 * For example:
 * - "en" -> Locale.ENGLISH
 * - "en_US" -> Locale.US
 * - "fr_CA_QC" -> Locale("fr", "CA", "QC")
 */
internal val DEFAULT_LOCALE_MAPPER: (String) -> Locale? = {
    val parts = it.split('_')
    when (parts.size) {
        1 -> Locale.of(parts[0])
        2 -> Locale.of(parts[0], parts[1])
        3 -> Locale.of(parts[0], parts[1], parts[2])
        else -> null
    }
}

/**
 * Registers all translations from files within the specified directory.
 *
 * This function reads each file in the directory, determines the locale based on the file name
 * using the [localeMapper], and then registers the translations using the provided
 * [ObjectMapper] and [MessageNodeParser.MapParser].
 *
 * @param directory The directory containing translation files.
 * @param mapper The [ObjectMapper] used to parse JSON files.
 * @param localeMapper A function that maps file names to [Locale] instances. Defaults to
 * [DEFAULT_LOCALE_MAPPER].
 * @param parser The [MessageNodeParser.MapParser] used to parse file content into a map of
 * translation keys to messages. Defaults to [MessageNodeParser.MapParser.Default].
 * @return This [TranslationRegistry] instance.
 */
fun TranslationRegistry.registerFromDirectory(
    directory: Path,
    mapper: ObjectMapper,
    localeMapper: (String) -> Locale? = DEFAULT_LOCALE_MAPPER,
    parser: MessageNodeParser.MapParser = MessageNodeParser.MapParser.Default,
) = apply {
    directory
        .listDirectoryEntries()
        .mapNotNull { localeMapper(it.nameWithoutExtension)?.to(it) }
        .forEach { (locale, path) ->
            registerFromFile(path, mapper, locale, parser)
        }
}

/**
 * Registers translations from a file.
 *
 * This function reads the content from the specified [file] using the provided
 * [ObjectMapper] and [MessageNodeParser.MapParser], and then registers the translations
 * under the given [locale].
 *
 * @param file The path to the translation file.
 * @param mapper The [ObjectMapper] used to parse the JSON file.
 * @param locale The [Locale] for the translations in the file. Defaults to the registry's
 * default locale.
 * @param parser The [MessageNodeParser.MapParser] used to parse file content into a map of translation
 * keys to messages. Defaults to [MessageNodeParser.MapParser.Default].
 * @return This [TranslationRegistry] instance.
 */
fun TranslationRegistry.registerFromFile(
    file: Path,
    mapper: ObjectMapper,
    locale: Locale = defaultLocale,
    parser: MessageNodeParser.MapParser = MessageNodeParser.MapParser.Default,
) = registerAll(parser.parse(file.bufferedReader().use(mapper::readTree)), locale)
