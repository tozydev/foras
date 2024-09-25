package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.CompositeMessage
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class MessageMapParserTest {
    private val objectMapper = ObjectMapper()

    @Test
    fun `parse null node`() {
        val result = MessageMapParser.parse(objectMapper.readTree("null"))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parse empty node`() {
        val result = MessageMapParser.parse(objectMapper.readTree("{}"))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parse simple message node`() {
        val node: JsonNode = objectMapper.readTree("""{ "message": "Hello, world!" }""")
        val result = MessageMapParser.parse(node)
        assertEquals(1, result.size)
        assertTrue(result.containsKey("message"))
        assertTrue(result["message"] is TextMessage)
        assertEquals("Hello, world!", (result["message"] as TextMessage).text)
    }

    @Test
    fun `parse nested message nodes`() {
        val node: JsonNode =
            objectMapper.readTree(
                """
            {
                "message": "Hello, world!",
                "nested": {
                    "message1": "Nested message 1",
                    "message2": "Nested message 2"
                }
            }
            """,
            )
        val result = MessageMapParser.parse(node)
        assertEquals(3, result.size)

        assertTrue(result.containsKey("message"))
        assertTrue(result["message"] is TextMessage)
        assertEquals("Hello, world!", (result["message"] as TextMessage).text)

        assertTrue(result.containsKey("nested.message1"))
        assertTrue(result["nested.message1"] is TextMessage)
        assertEquals("Nested message 1", (result["nested.message1"] as TextMessage).text)

        assertTrue(result.containsKey("nested.message2"))
        assertTrue(result["nested.message2"] is TextMessage)
        assertEquals("Nested message 2", (result["nested.message2"] as TextMessage).text)
    }

    @Test
    fun `parse different message types`() {
        val node: JsonNode =
            objectMapper.readTree(
                """
            {
                "text_node": "This is a text message",
                "actionbar_node": { "actionbar": "This is an actionbar message" },
                "title_node": {
                    "title": "This is a title",
                    "subtitle": "This is a subtitle",
                    "fade-in": "1s",
                    "stay": "2s",
                    "fade-out": "3s"
                },
                "sound_node": {
                    "sound": "entity.experience_orb.pickup",
                    "source": "master",
                    "volume": 2.0,
                    "pitch": 1.5,
                    "seed": 123456789
                }
            }
            """,
            )
        val result = MessageMapParser.parse(node)
        assertEquals(4, result.size)

        assertTrue(result.containsKey("text_node"))
        assertTrue(result["text_node"] is TextMessage)
        assertEquals("This is a text message", (result["text_node"] as TextMessage).text)

        assertTrue(result.containsKey("actionbar_node"))
        assertTrue(result["actionbar_node"] is ActionbarMessage)
        assertEquals("This is an actionbar message", (result["actionbar_node"] as ActionbarMessage).actionbar)

        assertTrue(result.containsKey("title_node"))
        assertTrue(result["title_node"] is TitleMessage)
        (result["title_node"] as TitleMessage).let {
            assertEquals("This is a title", it.title)
            assertEquals("This is a subtitle", it.subtitle)
            assertEquals(1.seconds, it.fadeIn)
            assertEquals(2.seconds, it.stay)
            assertEquals(3.seconds, it.fadeOut)
        }

        assertTrue(result.containsKey("sound_node"))
        assertTrue(result["sound_node"] is SoundMessage)
        (result["sound_node"] as SoundMessage).let {
            assertEquals("entity.experience_orb.pickup", it.sound)
            assertEquals("master", it.source)
            assertEquals(2.0f, it.volume)
            assertEquals(1.5f, it.pitch)
            assertEquals(123456789L, it.seed)
        }
    }

    @Test
    fun `parse message array`() {
        val node: JsonNode =
            objectMapper.readTree(
                """
            {
                "messages": [
                    "Message 1",
                    { "text": "Message 2" },
                    { "actionbar": "Message 3" }
                ]
            }
            """,
            )
        val result = MessageMapParser.parse(node)
        assertEquals(1, result.size)
        assertTrue(result.containsKey("messages"))
        assertTrue(result["messages"] is CompositeMessage)

        val messages = (result["messages"] as CompositeMessage).messages
        assertEquals(3, messages.size)

        assertTrue(messages[0] is TextMessage)
        assertEquals("Message 1", (messages[0] as TextMessage).text)

        assertTrue(messages[1] is TextMessage)
        assertEquals("Message 2", (messages[1] as TextMessage).text)

        assertTrue(messages[2] is ActionbarMessage)
        assertEquals("Message 3", (messages[2] as ActionbarMessage).actionbar)
    }

    @Test
    fun `parse complex message structure`() {
        val node: JsonNode =
            objectMapper.readTree(
                """
            {
                "message": "Hello, world!",
                "nested": {
                    "message1": "Nested message 1",
                    "message2": {
                        "actionbar": "This is an actionbar message"
                    }
                },
                "messages": [
                    "Message 1",
                    { "text": "Message 2" }
                ]
            }
            """,
            )
        val result = MessageMapParser.parse(node)
        assertEquals(4, result.size)

        assertTrue(result.containsKey("message"))
        assertTrue(result["message"] is TextMessage)
        assertEquals("Hello, world!", (result["message"] as TextMessage).text)

        assertTrue(result.containsKey("nested.message1"))
        assertTrue(result["nested.message1"] is TextMessage)
        assertEquals("Nested message 1", (result["nested.message1"] as TextMessage).text)

        assertTrue(result.containsKey("nested.message2"))
        assertTrue(result["nested.message2"] is ActionbarMessage)
        assertEquals("This is an actionbar message", (result["nested.message2"] as ActionbarMessage).actionbar)

        assertTrue(result.containsKey("messages"))
        assertTrue(result["messages"] is CompositeMessage)

        val messages = (result["messages"] as CompositeMessage).messages
        assertEquals(2, messages.size)

        assertTrue(messages[0] is TextMessage)
        assertEquals("Message 1", (messages[0] as TextMessage).text)

        assertTrue(messages[1] is TextMessage)
        assertEquals("Message 2", (messages[1] as TextMessage).text)
    }
}
