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
import io.github.tozydev.foras.Message

internal class MapParserImpl(
    val parser: MessageNodeParser,
    override val pathSeparator: Char,
) : MessageNodeParser.MapParser {
    override fun parse(node: JsonNode): Map<String, Message> {
        val result = mutableMapOf<String, Message>()
        parse0(node, result)
        return result
    }

    private fun parse0(
        node: JsonNode,
        result: MutableMap<String, Message>,
        path: String = "",
    ) {
        if (node.isNull || node.isMissingNode) {
            return
        }

        if (parser.isMessageNode(node)) {
            result[path] = parser.parse(node)
            return
        }

        for ((field, value) in node.properties()) {
            parse0(value, result, if (path.isEmpty()) field else "$path$pathSeparator$field")
        }
    }
}
