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
import java.util.concurrent.ConcurrentHashMap

internal class TranslationRegistryImpl(
    override var defaultLocale: Locale,
) : TranslationRegistry {
    private val dictionaries: MutableMap<Locale, MutableMap<String, Message>> = ConcurrentHashMap()

    override fun contains(key: String) = dictionaries.any { (_, dict) -> key in dict }

    override fun get(
        key: String,
        locale: Locale,
    ): Message? = dictionaries[locale]?.get(key) ?: getFallbackMessage(key, Locale.of(locale.language))

    override fun register(
        key: String,
        message: Message,
        locale: Locale,
    ) = apply {
        require(dictionaries[locale]?.contains(key) != true) {
            "Translation already registered: $key (${message::class.simpleName})"
        }
        dictionaries.getOrPut(locale) { ConcurrentHashMap() }[key] = message
    }

    override fun unregisterAll(key: String) {
        for ((_, dict) in dictionaries) {
            dict -= key
        }
    }

    private fun getFallbackMessage(
        key: String,
        fallback: Locale,
    ) = dictionaries[fallback]?.get(key) ?: dictionaries[defaultLocale]?.get(key)
}
