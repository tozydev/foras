package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import io.github.tozydev.foras.Message
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.ACTIONBAR
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADEIN
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADEOUT
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADE_IN
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.FADE_OUT
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.SOUND
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.STAY
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.SUBTITLE
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.TEXT
import io.github.tozydev.foras.jackson.MessageNodeParser.Fields.TITLE

object MessageMapParser {
    fun parse(node: JsonNode): Map<String, Message> {
        val result = mutableMapOf<String, Message>()
        parse0(node, result)
        return result
    }

    private fun parse0(
        node: JsonNode?,
        result: MutableMap<String, Message>,
        key: String = "",
    ) {
        if (node == null || node.isNull || node.isMissingNode) {
            return
        }

        if (node.isMessageNode()) {
            result[key] = MessageNodeParser.parse(node)
            return
        }

        for ((field, value) in node.properties()) {
            parse0(value, result, if (key.isEmpty()) field else "$key.$field")
        }
    }

    private fun JsonNode.isMessageNode() =
        when (this) {
            is ArrayNode, is ValueNode -> true
            is ObjectNode -> has(TEXT) || has(ACTIONBAR) || has(SOUND) || isTitleNode()
            else -> false
        }

    private fun JsonNode.isTitleNode() =
        has(TITLE) || has(SUBTITLE) || has(FADEIN) || has(FADE_IN) || has(STAY) || has(FADE_OUT) || has(FADEOUT)
}
