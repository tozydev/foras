package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.CompositeMessage
import io.github.tozydev.foras.EmptyMessage
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class MessageNodeParserTest {
    private val objectMapper = ObjectMapper()

    @Test
    fun `parse null node`() {
        val result = MessageNodeParser.parse(null)
        assertTrue(result is EmptyMessage)
    }

    @Test
    fun `parse text node`() {
        val node: JsonNode = objectMapper.readTree(""""Hello, world!"""")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is TextMessage)
        assertEquals("Hello, world!", result.text)
    }

    @Test
    fun `parse text node with null value`() {
        val node: JsonNode = objectMapper.readTree("null")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is EmptyMessage)
    }

    @Test
    fun `parse array node`() {
        val node: JsonNode = objectMapper.readTree("""["Hello", "world!"]""")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is CompositeMessage)
        assertEquals(2, result.messages.size)
        assertTrue(result.messages[0] is TextMessage)
        assertTrue(result.messages[1] is TextMessage)
        assertEquals("Hello", (result.messages[0] as TextMessage).text)
        assertEquals("world!", (result.messages[1] as TextMessage).text)
    }

    @Test
    fun `parse object node with text field`() {
        val node: JsonNode = objectMapper.readTree("""{ "text": "Hello, world!" }""")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is TextMessage)
        assertEquals("Hello, world!", result.text)
    }

    @Test
    fun `parse object node with actionbar field`() {
        val node: JsonNode = objectMapper.readTree("""{ "actionbar": "Hello, world!" }""")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is ActionbarMessage)
        assertEquals("Hello, world!", result.actionbar)
    }

    @Test
    fun `parse object node with sound field`() {
        val node: JsonNode =
            objectMapper
                .readTree(
                    """{ "sound": "entity.experience_orb.pickup", "source": "master", "volume": 2.0, "pitch": 1.5, "seed": 123456789 }""",
                )
        val result = MessageNodeParser.parse(node)
        assertTrue(result is SoundMessage)
        assertEquals("entity.experience_orb.pickup", result.sound)
        assertEquals("master", result.source)
        assertEquals(2.0f, result.volume)
        assertEquals(1.5f, result.pitch)
        assertEquals(123456789L, result.seed)
    }

    @Test
    fun `parse object node with sound field with default values`() {
        val node: JsonNode = objectMapper.readTree("""{ "sound": "entity.experience_orb.pickup" }""")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is SoundMessage)
        assertEquals("entity.experience_orb.pickup", result.sound)
        assertNull(result.source)
        assertEquals(1.0f, result.volume)
        assertEquals(1.0f, result.pitch)
        assertNull(result.seed)
    }

    @Test
    fun `parse object node with title field`() {
        val node: JsonNode =
            objectMapper.readTree(
                """{ "title": "Hello", "subtitle": "world!", "fade-in": "1s", "stay": "2s", "fade-out": "3s" }""",
            )
        val result = MessageNodeParser.parse(node)
        assertTrue(result is TitleMessage)
        assertEquals("Hello", result.title)
        assertEquals("world!", result.subtitle)
        assertEquals(1.seconds, result.fadeIn)
        assertEquals(2.seconds, result.stay)
        assertEquals(3.seconds, result.fadeOut)
    }

    @Test
    fun `parse object node with title field with alternative names`() {
        val node: JsonNode =
            objectMapper.readTree(
                """{ "title": "Hello", "subtitle": "world!", "fadein": "1s", "stay": "2s", "fadeout": "3s" }""",
            )
        val result = MessageNodeParser.parse(node)
        assertTrue(result is TitleMessage)
        assertEquals("Hello", result.title)
        assertEquals("world!", result.subtitle)
        assertEquals(1.seconds, result.fadeIn)
        assertEquals(2.seconds, result.stay)
        assertEquals(3.seconds, result.fadeOut)
    }

    @Test
    fun `parse object node with title field with missing fields`() {
        val node: JsonNode = objectMapper.readTree("""{ "title": "Hello" }""")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is TitleMessage)
        assertEquals("Hello", result.title)
        assertNull(result.subtitle)
        assertNull(result.fadeIn)
        assertNull(result.stay)
        assertNull(result.fadeOut)
    }

    @Test
    fun `parse object node with empty object`() {
        val node: JsonNode = objectMapper.readTree("{}")
        val result = MessageNodeParser.parse(node)
        assertTrue(result is EmptyMessage)
    }
}
