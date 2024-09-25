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

package io.github.tozydev.foras.translation

import io.github.tozydev.foras.EmptyMessage
import io.github.tozydev.foras.Message
import io.github.tozydev.foras.translation.Translator.Global.defaultLocale
import io.github.tozydev.foras.translation.Translator.Global.sources
import java.util.Locale

/** Provides functionality for translating keys into localized [Message] instances. */
interface Translator {
    /**
     * Translates the given [key] into a localized [Message] based on the provided [locale] or
     * a default locale if not specified.
     */
    fun translate(
        key: String,
        locale: Locale? = null,
    ): Message

    /**
     * A global [Translator] that aggregates translations from multiple [TranslationRegistry] sources.
     *
     * This companion object provides a convenient way to access translations from a shared pool of
     * registries. The first registry containing a matching translation for the given key and locale
     * will be used.
     *
     * @property sources The set of [TranslationRegistry] instances to use as translation sources.
     * @property defaultLocale The default locale to use for translations if no locale is explicitly provided.
     */
    companion object Global : Translator {
        val sources: MutableSet<TranslationRegistry> = hashSetOf<TranslationRegistry>()

        var defaultLocale: Locale = Locale.ENGLISH

        override fun translate(
            key: String,
            locale: Locale?,
        ) = sources.firstNotNullOfOrNull { it[key, locale ?: defaultLocale] } ?: EmptyMessage
    }
}

/**
 * Creates a [Translator] instance using the provided [TranslationRegistry].
 *
 * @param registry The [TranslationRegistry] to use for translations.
 * @return A new [Translator] instance.
 */
fun Translator(registry: TranslationRegistry): Translator = TranslatorImpl(registry)
