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
import io.github.tozydev.foras.TextMessage
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TranslatorGlobalTest {
    private val registry1 = TranslationRegistryImpl(Locale.ENGLISH)
    private val registry2 = TranslationRegistryImpl(Locale.FRENCH)

    @BeforeTest
    fun setUp() {
        Translator.Global.sources.clear()
        Translator.Global.sources.addAll(listOf(registry1, registry2))
        Translator.Global.defaultLocale = Locale.ENGLISH
    }

    @AfterTest
    fun tearDown() {
        Translator.Global.sources.clear()
    }

    @Test
    fun `translate with existing key and locale in first registry`() {
        registry1.register("test.key", TextMessage("English"), Locale.ENGLISH)
        val message = Translator.Global.translate("test.key", Locale.ENGLISH)

        assertEquals(TextMessage("English"), message)
    }

    @Test
    fun `translate with existing key and locale in second registry`() {
        registry2.register("test.key", TextMessage("Français"), Locale.FRENCH)
        val message = Translator.Global.translate("test.key", Locale.FRENCH)

        assertEquals(TextMessage("Français"), message)
    }

    @Test
    fun `translate with existing key and default locale`() {
        registry1.register("test.key", TextMessage("English"), Locale.ENGLISH)
        val message = Translator.Global.translate("test.key")

        assertEquals(TextMessage("English"), message)
    }

    @Test
    fun `translate with existing key and fallback locale`() {
        registry1.register("test.key", TextMessage("English"), Locale.ENGLISH)
        val message = Translator.Global.translate("test.key", Locale.US)

        assertEquals(TextMessage("English"), message)
    }

    @Test
    fun `translate with non-existing key returns empty message`() {
        val message = Translator.Global.translate("non.existing.key", Locale.ENGLISH)

        assertEquals(EmptyMessage, message)
    }

    @Test
    fun `translate with empty sources returns empty message`() {
        Translator.Global.sources.clear()
        val message = Translator.Global.translate("test.key", Locale.ENGLISH)

        assertEquals(EmptyMessage, message)
    }
}
