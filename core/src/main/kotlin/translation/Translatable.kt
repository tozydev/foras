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

/** Represents an object that can be translated. */
interface Translatable {
    /** The key used to identify the translation. */
    val key: String
}

/**
 * Translates the given [Translatable] key into a localized [Message] based on the provided [locale] or
 * a default locale if not specified.
 */
fun Translator.translate(
    key: Translatable,
    locale: Locale? = null,
) = translate(key.key, locale)
