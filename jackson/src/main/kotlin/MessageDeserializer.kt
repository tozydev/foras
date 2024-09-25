package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import io.github.tozydev.foras.Message

object MessageDeserializer : StdDeserializer<Message>(Message::class.java) {
    private fun readResolve(): Any = MessageDeserializer

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ) = MessageNodeParser.parse(p.codec.readTree<JsonNode>(p))
}
