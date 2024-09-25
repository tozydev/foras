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
import com.fasterxml.jackson.databind.node.MissingNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultMapParserTest {
    private val mapParser = MessageNodeParser.MapParser.Default
    private val objectMapper = ObjectMapper()

    @Test
    fun `parse should return empty map for null or missing node`() {
        assertTrue(mapParser.parse(NullNode.getInstance()).isEmpty())
        assertTrue(mapParser.parse(MissingNode.getInstance()).isEmpty())
    }

    @Test
    fun `parse should parse top-level message node correctly`() {
        val message = TextMessage("Hello, world!")

        val result = mapParser.parse(TextNode.valueOf("Hello, world!"))

        assertEquals(mapOf("" to message), result)
    }

    @Test
    fun `parse should parse nested nodes correctly`() {
        val objectMapper = ObjectMapper()
        val node =
            objectMapper.readTree(
                """
                {
                  "message1": { "text": "Hello" },
                  "nested": {
                    "message2": { "actionbar": "World" },
                    "message3": {
                      "title": "Title",
                      "subtitle": "Subtitle"
                    }
                  }
                }
                """.trimIndent(),
            )

        val expected =
            mapOf(
                "message1" to TextMessage("Hello"),
                "nested.message2" to ActionbarMessage("World"),
                "nested.message3" to TitleMessage("Title", "Subtitle"),
            )
        assertEquals(expected, mapParser.parse(node))
    }

    @Test
    fun `parse should handle empty object node`() {
        val node = objectMapper.createObjectNode()

        val result = mapParser.parse(node)

        assertTrue(result.isEmpty())
    }
}
