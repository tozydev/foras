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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.EmptyMessage
import io.github.tozydev.foras.Message
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.ACTIONBAR
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.FADEIN
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.FADEOUT
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.FADE_IN
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.FADE_OUT
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.PITCH
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.SEED
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.SOUND
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.SOURCE
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.STAY
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.SUBTITLE
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.TEXT
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.TITLE
import io.github.tozydev.foras.jackson.MessageNodeParserImpl.Fields.VOLUME
import kotlin.time.Duration

internal object MessageNodeParserImpl : MessageNodeParser {
    override fun isMessageNode(node: JsonNode): Boolean =
        when (node.nodeType) {
            JsonNodeType.ARRAY -> node.all { isMessageNode(node) }
            JsonNodeType.OBJECT -> node.has(TEXT) || node.has(ACTIONBAR) || node.has(SOUND) || node.isTitleNode()
            JsonNodeType.BOOLEAN, JsonNodeType.NULL, JsonNodeType.NUMBER, JsonNodeType.STRING -> true
            else -> false
        }

    private fun JsonNode.isTitleNode() =
        has(TITLE) || has(SUBTITLE) || has(FADEIN) || has(FADE_IN) || has(STAY) || has(FADE_OUT) || has(FADEOUT)

    override fun parse(node: JsonNode): Message =
        when (node) {
            is ArrayNode -> node.map { parse(it) }.let(::Message)
            is ObjectNode -> parseMessageNode(node)
            is BooleanNode, is NullNode, is NumericNode, is TextNode -> parseTextNode(node)
            else -> throw IllegalArgumentException("Unsupported node type: ${node.nodeType}")
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

        return parseTitleNode(node) ?: throw IllegalArgumentException("Cannot parse node: $node")
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
