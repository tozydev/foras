package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.EmptyMessage
import io.github.tozydev.foras.Message
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.ACTIONBAR
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADEIN
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADEOUT
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADE_IN
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADE_OUT
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.PITCH
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.SEED
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.SOUND
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.SOURCE
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.STAY
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.SUBTITLE
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.TEXT
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.TITLE
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.VOLUME
import kotlin.time.Duration

internal object MessageNodeParser {
    fun parse(node: JsonNode?): Message =
        when (node) {
            is ValueNode -> parseTextNode(node)
            is ArrayNode -> node.map { parse(it) }.let(::Message)
            is ObjectNode -> parseMessageNode(node)
            else -> EmptyMessage
        }

    private fun parseTextNode(node: JsonNode) = if (node is NullNode) EmptyMessage else TextMessage(node.asText())

    private fun parseMessageNode(node: ObjectNode): Message {
        var contentNode = node[TEXT]
        if (contentNode != null) {
            return parseTextNode(contentNode)
        }

        contentNode = node[ACTIONBAR]
        if (contentNode != null) {
            return parseActionbarNode(contentNode)
        }

        contentNode = node[SOUND]
        if (contentNode != null) {
            return parseSoundNode(node)
        }

        return parseTitleNode(node) ?: EmptyMessage
    }

    private fun parseTitleNode(node: JsonNode): TitleMessage? {
        val title = node[TITLE]
        val subtitle = node[SUBTITLE]
        val fadeIn = node[FADE_IN] ?: node[FADEIN]
        val stay = node[STAY]
        val fadeOut = node[FADE_OUT] ?: node[FADEOUT]
        if (title == null && subtitle == null && fadeIn == null && stay == null && fadeOut == null) {
            return null
        }
        return TitleMessage(
            title = title.nullOrText(),
            subtitle = subtitle.nullOrText(),
            fadeIn = fadeIn.nullOrText()?.let(Duration::parse),
            stay = stay.nullOrText()?.let(Duration::parse),
            fadeOut = fadeOut.nullOrText()?.let(Duration::parse),
        )
    }

    private fun parseActionbarNode(node: JsonNode) = ActionbarMessage(node.asText())

    private fun parseSoundNode(node: JsonNode): SoundMessage {
        val sound = node[SOUND]
        val source = node[SOURCE]
        val volume = node[VOLUME]
        val pitch = node[PITCH]
        val seed = node[SEED]
        return SoundMessage(
            sound = sound.asText(),
            source = source.nullOrText(),
            volume = volume.nullOrFloat() ?: 1f,
            pitch = pitch.nullOrFloat() ?: 1f,
            seed = seed.nullOrLong(),
        )
    }

    private fun JsonNode?.nullOrText() = if (this == null || this is NullNode) null else asText()

    private fun JsonNode?.nullOrFloat() = if (this == null || this is NullNode) null else asText().toFloat()

    private fun JsonNode?.nullOrLong() = if (this == null || this is NullNode) null else asText().toLong()

    object Fields {
        const val TEXT = "text"

        const val ACTIONBAR = "actionbar"

        const val TITLE = "title"
        const val SUBTITLE = "subtitle"
        const val FADEIN = "fadein"
        const val FADE_IN = "fade-in"
        const val STAY = "stay"
        const val FADEOUT = "fadeout"
        const val FADE_OUT = "fade-out"

        const val SOUND = "sound"
        const val SOURCE = "source"
        const val VOLUME = "volume"
        const val PITCH = "pitch"
        const val SEED = "seed"
    }
}
