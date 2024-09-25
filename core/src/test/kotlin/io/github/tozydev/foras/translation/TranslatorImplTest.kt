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
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TranslatorImplTest {
    private val message = TextMessage("Hello World!")
    private val registry =
        TranslationRegistry {
            register("test.key", message)
        }
    private val translator = TranslatorImpl(registry)

    @Test
    fun `translate with existing key and locale`() {
        assertEquals(message, translator.translate("test.key", Locale.ENGLISH))
    }

    @Test
    fun `translate with existing key and null locale`() {
        assertEquals(message, translator.translate("test.key", null))
    }

    @Test
    fun `translate with non-existing key returns empty message`() {
        assertEquals(EmptyMessage, translator.translate("non.existing.key"))
    }
}
