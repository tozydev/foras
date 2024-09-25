package io.github.tozydev.foras.jackson

import com.fasterxml.jackson.databind.JsonNode
import io.github.tozydev.foras.Message

/** A parser that can determine if a [JsonNode] represents a [Message] and parse it accordingly. */
interface MessageNodeParser {
    /** Whether the given [JsonNode] can be parsed as a [Message]. */
    fun isMessageNode(node: JsonNode): Boolean

    /**
     * Parses the given [JsonNode] into a [Message].
     *
     * @throws IllegalArgumentException If the node cannot be parsed as a [Message].
     */
    fun parse(node: JsonNode): Message

    /**
     * A default implementation of [MessageNodeParser] that parses [JsonNode] according to the
     * following rules:
     *
     * - **Arrays:** Treated as [Message] instances containing multiple sub-messages.
     * - **Objects:** Parsed based on the presence of specific fields:
     *    - `"text"`: Parsed as a [TextMessage].
     *    - `"actionbar"`: Parsed as an [ActionbarMessage].
     *    - `"sound"`: Parsed as a [SoundMessage] along with optional fields
     *       (`"source"`, `"volume"`, `"pitch"`, `"seed"`).
     *    - `"title"`, `"subtitle"`, `"fade-in"`, `"fade-out"`, `"stay"`: Parsed as a
     *       [TitleMessage].
     * - **Booleans, Nulls, Numbers, Strings:** Parsed as [io.github.tozydev.foras.TextMessage] instances, with null
     *   values becoming [io.github.tozydev.foras.EmptyMessage].
     * - **Other Node Types:** Throw an [IllegalArgumentException].
     *
     * For object nodes representing messages with content, the content can be specified
     * using either short-form field names (`"fadein"`) or kebab-case names (`"fade-in"`).
     *
     * **Example JSON:**
     * ```json
     * {
     *   "message": { "text": "Hello, world!" },
     *   "actionbar_message": { "actionbar": "This is an action bar message." },
     *   "sound_message": {
     *     "sound": "minecraft:entity.pig.ambient",
     *     "source": "player",
     *     "volume": 2.0,
     *     "pitch": 1.5
     *   },
     *   "title_message": {
     *     "title": "Welcome!",
     *     "subtitle": "To this amazing server",
     *     "fade-in": "1s",
     *     "stay": "2s",
     *     "fade-out": "1s"
     *   }
     * }
     * ```
     */
    companion object Default : MessageNodeParser by MessageNodeParserImpl

    /**
     * A parser that can parse a [JsonNode] into a [Map] of [String] keys to [Message] values.
     */
    interface MapParser {
        /**
         * The separator used to delimit nested keys in the Jackson supported structure.
         */
        val pathSeparator: Char

        /** Parses the given [JsonNode] into a [Map] of [String] keys to [Message] values. */
        fun parse(node: JsonNode): Map<String, Message>

        /**
         * A default implementation of [MapParser] that parses a [JsonNode] into a [Map] of
         * [String] keys to [Message] values. It utilizes [MessageNodeParser.Default] to handle
         * individual message nodes and uses a dot ('.') to separate nested keys within the
         * Jackson supported structure.
         *
         * This implementation recursively traverses the tree, concatenating nested object
         * keys with dots to create a flat map of key-value pairs, where the keys represent the
         * full path to a message node and the values are the parsed [Message] instances.
         *
         * **Note:** The keys of nested objects cannot have the same names as the fields used
         * to define message content (e.g., "text", "actionbar", "sound", "title", etc.).
         *
         * **Example JSON:**
         * ```json
         * {
         *   "message": { "text": "Hello, world!" },
         *   "nested": {
         *     "_actionbar": { "actionbar": "This is an action bar message." },
         *     "_title": {
         *       "title": "Welcome!",
         *       "subtitle": "To this amazing server"
         *     }
         *   }
         * }
         * ```
         *
         * **Resulting Map:**
         * ```
         * {
         *   "message": TextMessage(text="Hello, world!"),
         *   "nested._actionbar": ActionbarMessage(actionbar="This is an action bar message."),
         *   "nested._title": TitleMessage(title="Welcome!", subtitle="To this amazing server")
         * }
         * ```
         */
        companion object Default : MapParser {
            /**
             * The default path separator used by [Default], which is a dot ('.').
             */
            const val DOT_PATH_SEPARATOR = '.'

            private val delegate by lazy { MapParserImpl(MessageNodeParser.Default, DOT_PATH_SEPARATOR) }

            override val pathSeparator = delegate.pathSeparator

            override fun parse(node: JsonNode) = delegate.parse(node)
        }
    }
}
