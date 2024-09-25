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

package io.github.tozydev.foras.adventure

import io.github.tozydev.foras.ActionbarMessage
import io.github.tozydev.foras.CompositeMessage
import io.github.tozydev.foras.EmptyMessage
import io.github.tozydev.foras.Message
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.ComponentDecoder

internal object PlainTextComponentDecoder : ComponentDecoder<String, Component> {
    override fun deserialize(input: String): Component = Component.text(input)
}

/**
 * Sends a [Message] to the [Audience] using the provided [MessageDecoder] for deserialization.
 *
 * By default, [decoder] will decode text to plain text component.
 *
 * This function handles different message types:
 * - [ActionbarMessage]: Sends the message to the action bar.
 * - [CompositeMessage]: Sends each individual message within the composite.
 * - [EmptyMessage]: Does nothing.
 * - [SoundMessage]: Plays the specified sound.
 * - [TextMessage]: Sends the message as chat text.
 * - [TitleMessage]: Displays the title or resets the existing one if empty.
 */
fun Audience.sendMessage(
    message: Message,
    decoder: MessageDecoder = PlainTextComponentDecoder,
) = when (message) {
    is ActionbarMessage -> sendActionBar(message.toComponent(decoder))
    is CompositeMessage -> sendMessages(message, decoder)
    EmptyMessage -> Unit
    is SoundMessage -> playSound(message.toAdventureSound())
    is TextMessage -> sendMessage(message.toComponent(decoder))
    is TitleMessage -> sendOrResetTitle(message, decoder)
}

private fun Audience.sendMessages(
    message: CompositeMessage,
    decoder: MessageDecoder,
) {
    for (msg in message.messages) {
        sendMessage(msg, decoder)
    }
}

private fun Audience.sendOrResetTitle(
    title: TitleMessage,
    decoder: MessageDecoder,
) {
    val adventureTitle = title.toAdventureTitle(decoder)
    if (adventureTitle == ResetTitle) {
        resetTitle()
    } else {
        showTitle(adventureTitle)
    }
}
