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
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import io.github.tozydev.foras.translation.TranslationRegistry
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.createFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

class TranslationRegisterFromFileTest {
    @TempDir
    lateinit var tempDir: Path

    private val mapper = ObjectMapper()
    private val registry = TranslationRegistry(Locale.ENGLISH)

    @Test
    fun `registerFromDirectory should register translations from all files`() {
        val enUsFile = tempDir.resolve("en_US.json").createFile()
        val frCaFile = tempDir.resolve("fr_CA.json").createFile()

        enUsFile.writeText(
            """
            {
              "message1": { "text": "Hello" },
              "nested": {
                "message2": { "actionbar": "World" }
              }
            }
            """.trimIndent(),
        )
        frCaFile.writeText(
            """
            {
              "message1": { "text": "Bonjour" },
              "nested": {
                "message3": {
                  "title": "Titre",
                  "subtitle": "Sous-titre"
                }
              }
            }
            """.trimIndent(),
        )

        registry.registerFromDirectory(tempDir, mapper)

        assertEquals(TextMessage("Hello"), registry["message1", Locale.US])
        assertEquals(ActionbarMessage("World"), registry["nested.message2", Locale.US])
        assertEquals(TextMessage("Bonjour"), registry["message1", Locale.CANADA_FRENCH])
        assertEquals(TitleMessage("Titre", "Sous-titre"), registry["nested.message3", Locale.CANADA_FRENCH])
    }

    @Test
    fun `registerFromFile should register translations from file`() {
        val file = tempDir.resolve("messages.json").createFile()
        file.writeText(
            """
            {
              "message1": { "text": "Hello" },
              "nested": {
                "message2": { "actionbar": "World" }
              }
            }
            """.trimIndent(),
        )

        registry.registerFromFile(file, mapper, Locale.FRENCH)

        assertEquals(TextMessage("Hello"), registry["message1", Locale.FRENCH])
        assertEquals(ActionbarMessage("World"), registry["nested.message2", Locale.FRENCH])
    }

    @Test
    fun `registerFromFile should use default locale if not specified`() {
        val file = tempDir.resolve("messages.json").createFile()
        file.writeText(
            """
            {
              "message1": { "text": "Hello" }
            }
            """.trimIndent(),
        )

        registry.registerFromFile(file, mapper)

        assertEquals(TextMessage("Hello"), registry["message1"])
    }
}
