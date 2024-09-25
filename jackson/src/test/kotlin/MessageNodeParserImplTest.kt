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
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.CompositeMessage
import io.github.tozydev.foras.EmptyMessage
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class MessageNodeParserImplTest {
    private val parser = MessageNodeParserImpl
    private val objectMapper = ObjectMapper()

    @Test
    fun `isMessageNode returns true for valid message nodes`() {
        assertTrue(parser.isMessageNode(objectMapper.createArrayNode()))
        assertTrue(parser.isMessageNode(objectMapper.createObjectNode().put("text", "Hello")))
        assertTrue(parser.isMessageNode(objectMapper.createObjectNode().put("actionbar", "Hello")))
        assertTrue(parser.isMessageNode(objectMapper.createObjectNode().put("sound", "entity.pig.ambient")))
        assertTrue(
            parser.isMessageNode(
                objectMapper
                    .createObjectNode()
                    .put("title", "Title")
                    .put("subtitle", "Subtitle"),
            ),
        )
        assertTrue(parser.isMessageNode(objectMapper.valueToTree(true)))
        assertTrue(parser.isMessageNode(objectMapper.valueToTree(123)))
        assertTrue(parser.isMessageNode(objectMapper.valueToTree("Hello")))
        assertTrue(parser.isMessageNode(objectMapper.nullNode()))
    }

    @Test
    fun `isMessageNode returns false for invalid message nodes`() {
        assertFalse(parser.isMessageNode(objectMapper.createObjectNode()))
        assertFalse(parser.isMessageNode(objectMapper.createObjectNode().put("invalid", "value")))
    }

    @Test
    fun `parse parses ArrayNode correctly`() {
        val node = objectMapper.createArrayNode() as ArrayNode
        node.add(objectMapper.createObjectNode().put("text", "Hello"))
        node.add(objectMapper.createObjectNode().put("actionbar", "World"))

        val message = parser.parse(node)

        assertTrue(message is CompositeMessage)
        assertEquals(2, message.messages.size)
        assertEquals(TextMessage("Hello"), message.messages[0])
        assertEquals(ActionbarMessage("World"), message.messages[1])
    }

    @Test
    fun `parse parses ObjectNode with text correctly`() {
        val node = objectMapper.createObjectNode() as ObjectNode
        node.put("text", "Hello, world!")

        val message = parser.parse(node)

        assertTrue(message is TextMessage)
        assertEquals("Hello, world!", message.text)
    }

    @Test
    fun `parse parses ObjectNode with actionbar correctly`() {
        val node = objectMapper.createObjectNode() as ObjectNode
        node.put("actionbar", "This is an action bar message.")

        val message = parser.parse(node)

        assertTrue(message is ActionbarMessage)
        assertEquals("This is an action bar message.", message.actionbar)
    }

    @Test
    fun `parse parses ObjectNode with sound correctly`() {
        val node = objectMapper.createObjectNode() as ObjectNode
        node.put("sound", "minecraft:entity.pig.ambient")
        node.put("source", "player")
        node.put("volume", "2.0")
        node.put("pitch", "1.5")
        node.put("seed", "123456789")

        val message = parser.parse(node)

        assertTrue(message is SoundMessage)
        with(message) {
            assertEquals("minecraft:entity.pig.ambient", sound)
            assertEquals("player", source)
            assertEquals(2.0f, volume)
            assertEquals(1.5f, pitch)
            assertEquals(123456789, seed)
        }
    }

    @Test
    fun `parse parses ObjectNode with title correctly`() {
        val node = objectMapper.createObjectNode() as ObjectNode
        node.put("title", "Welcome!")
        node.put("subtitle", "To this amazing server")
        node.put("fade-in", "1s")
        node.put("stay", "2s")
        node.put("fade-out", "3s")

        val message = parser.parse(node)

        assertTrue(message is TitleMessage)
        with(message) {
            assertEquals("Welcome!", title)
            assertEquals("To this amazing server", subtitle)
            assertEquals(1.seconds, fadeIn)
            assertEquals(2.seconds, stay)
            assertEquals(3.seconds, fadeOut)
        }
    }

    @Test
    fun `parse parses ObjectNode with title using short-form field names correctly`() {
        val node = objectMapper.createObjectNode() as ObjectNode
        node.put("title", "Welcome!")
        node.put("subtitle", "To this amazing server")
        node.put("fadein", "1s")
        node.put("stay", "2s")
        node.put("fadeout", "3s")

        val message = parser.parse(node)

        assertTrue(message is TitleMessage)
        with(message) {
            assertEquals("Welcome!", title)
            assertEquals("To this amazing server", subtitle)
            assertEquals(1.seconds, fadeIn)
            assertEquals(2.seconds, stay)
            assertEquals(3.seconds, fadeOut)
        }
    }

    @Test
    fun `parse parses other JsonNode types correctly`() {
        assertEquals(TextMessage("true"), parser.parse(objectMapper.valueToTree(true)))
        assertEquals(EmptyMessage, parser.parse(objectMapper.nullNode()))
        assertEquals(TextMessage("123"), parser.parse(objectMapper.valueToTree(123)))
        assertEquals(TextMessage("Hello"), parser.parse(objectMapper.valueToTree("Hello")))
    }

    @Test
    fun `parse throws IllegalArgumentException for unsupported node types`() {
        assertFailsWith<IllegalArgumentException> {
            parser.parse(
                objectMapper.createObjectNode().put("test", 1),
            )
        }
    }
}
