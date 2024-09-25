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

import io.github.tozydev.foras.TextMessage
import java.util.Locale
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TranslationRegistryImplTest {
    private lateinit var registry: TranslationRegistry

    @BeforeTest
    fun setUp() {
        registry = TranslationRegistryImpl(Locale.ENGLISH)
    }

    @Test
    fun `test default locale`() {
        assertEquals(Locale.ENGLISH, registry.defaultLocale)
    }

    @Test
    fun `test contains`() {
        assertFalse("test.key" in registry)

        registry.register("test.key", TextMessage("Test"), Locale.ENGLISH)
        assertTrue("test.key" in registry)
    }

    @Test
    fun `test get with existing key and locale`() {
        val message = TextMessage("Test")
        registry.register("test.key", message, Locale.ENGLISH)

        assertEquals(message, registry["test.key", Locale.ENGLISH])
    }

    @Test
    fun `test get with existing key and default locale`() {
        val message = TextMessage("Test")
        registry.register("test.key", message, Locale.ENGLISH)

        assertEquals(message, registry["test.key"])
    }

    @Test
    fun `test get with existing key and fallback locale`() {
        val englishMessage = TextMessage("English")
        val frenchMessage = TextMessage("Français")
        registry.register("test.key", englishMessage, Locale.ENGLISH)
        registry.register("test.key", frenchMessage, Locale.FRENCH)

        assertEquals(frenchMessage, registry["test.key", Locale.CANADA_FRENCH])
    }

    @Test
    fun `test get with non-existing key`() {
        assertNull(registry["non.existing.key", Locale.ENGLISH])
    }

    @Test
    fun `test register with new key`() {
        val message = TextMessage("Test")
        registry.register("test.key", message, Locale.ENGLISH)

        assertEquals(message, registry["test.key", Locale.ENGLISH])
    }

    @Test
    fun `test register with existing key throws exception`() {
        registry.register("test.key", TextMessage("Test"), Locale.ENGLISH)

        assertFailsWith<IllegalArgumentException> {
            registry.register("test.key", TextMessage("Duplicate"), Locale.ENGLISH)
        }
    }

    @Test
    fun `test unregisterAll`() {
        registry.register("test.key", TextMessage("Test"), Locale.ENGLISH)
        registry.register("test.key", TextMessage("Test Fr"), Locale.FRENCH)

        registry.unregisterAll("test.key")

        assertFalse("test.key" in registry)
        assertNull(registry["test.key", Locale.ENGLISH])
        assertNull(registry["test.key", Locale.FRENCH])
    }

    @Test
    fun `test getFallbackMessage with existing fallback locale`() {
        val englishMessage = TextMessage("English")
        registry.register("test.key", englishMessage, Locale.ENGLISH)
        registry.defaultLocale = Locale.FRENCH

        assertEquals(englishMessage, registry["test.key", Locale.ENGLISH])
    }

    @Test
    fun `test getFallbackMessage with existing default locale`() {
        val englishMessage = TextMessage("English")
        registry.register("test.key", englishMessage, Locale.ENGLISH)

        assertEquals(englishMessage, registry["test.key", Locale.GERMAN])
    }

    @Test
    fun `test getFallbackMessage with no matching locale returns null`() {
        registry.defaultLocale = Locale.FRENCH

        assertNull(registry["test.key", Locale.GERMAN])
    }
}
