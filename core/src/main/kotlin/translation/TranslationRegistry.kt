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

import io.github.tozydev.foras.Message
import java.util.Locale

/**
 * A registry for managing translations.
 *
 * This interface provides methods for registering, retrieving, and managing translations
 * associated with specific keys and locales.
 */
interface TranslationRegistry {
    /** The default locale to use when retrieving translations. */
    var defaultLocale: Locale

    /**
     * Checks if a translation exists for the given [key].
     *
     * @param key The key to check for.
     * @return `true` if a translation exists for the key, `false` otherwise.
     */
    operator fun contains(key: String): Boolean

    /**
     * Retrieves the [Message] associated with the given [key] and [locale].
     *
     * @param key The key to retrieve the translation for.
     * @param locale The locale to retrieve the translation for. Defaults to [defaultLocale].
     * @return The [Message] associated with the key and locale, or `null` if not found.
     */
    operator fun get(
        key: String,
        locale: Locale = defaultLocale,
    ): Message?

    /**
     * Registers a [message] for the given [key] and [locale].
     *
     * @param key The key to register the translation for.
     * @param message The [Message] to register.
     * @param locale The locale to register the translation for. Defaults to [defaultLocale].
     * @return This [TranslationRegistry] instance.
     */
    fun register(
        key: String,
        message: Message,
        locale: Locale = defaultLocale,
    ): TranslationRegistry

    /** Unregisters all translations associated with the given [key]. */
    fun unregisterAll(key: String)
}

/**
 * Creates a new [TranslationRegistry] with the given [defaultLocale] and applies the [init] block.
 *
 * When retrieving a translation, if it's not found, a fallback translation will be retrieved.
 * The fallback translation is retrieve from locale with language code only or from default locale.
 *
 * @param defaultLocale The default locale for the registry. Defaults to [Locale.ENGLISH].
 * @param init An initialization block to configure the registry.
 * @return A new [TranslationRegistry] instance.
 */
fun TranslationRegistry(
    defaultLocale: Locale = Locale.ENGLISH,
    init: TranslationRegistry.() -> Unit = {},
): TranslationRegistry = TranslationRegistryImpl(defaultLocale).apply(init)

/**
 * Registers multiple translations from a `vararg` [dictionary] of key-message pairs.
 *
 * @param dictionary The key-message pairs to register.
 * @param locale The locale to register the translations for. Defaults to [TranslationRegistry.defaultLocale].
 * @return This [TranslationRegistry] instance.
 */
fun TranslationRegistry.registerAll(
    vararg dictionary: Pair<String, Message>,
    locale: Locale = defaultLocale,
) = apply {
    for ((key, msg) in dictionary) {
        register(key, msg, locale)
    }
}

/**
 * Registers multiple translations from a [dictionary] map of key-message pairs.
 *
 * @param dictionary The key-message map to register.
 * @param locale The locale to register the translations for. Defaults to [TranslationRegistry.defaultLocale].
 * @return This [TranslationRegistry] instance.
 */
fun TranslationRegistry.registerAll(
    dictionary: Map<String, Message>,
    locale: Locale = defaultLocale,
) = apply {
    for ((key, msg) in dictionary) {
        register(key, msg, locale)
    }
}

/**  Registers a [message] for the given [key] and [locale], using the `[]=` operator. */
operator fun TranslationRegistry.set(
    locale: Locale,
    key: String,
    message: Message,
) {
    register(key, message, locale)
}

/** Unregisters the translation associated with the given [key] using the `-=` operator. */
operator fun TranslationRegistry.minusAssign(key: String) = unregisterAll(key)
