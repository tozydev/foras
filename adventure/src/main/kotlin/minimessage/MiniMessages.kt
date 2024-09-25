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

package io.github.tozydev.foras.adventure.minimessage

import io.github.tozydev.foras.Message
import io.github.tozydev.foras.adventure.sendMessage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * The default [MiniMessage] instance used for rich message deserialization.
 *
 * This can be customized to use different MiniMessage settings or to provide a pre-configured
 * instance with custom tags and other options.
 */
var miniMessage = MiniMessage.miniMessage()

/**
 * Sends a rich [Message] to the [Audience] using MiniMessage for deserialization.
 *
 * This function utilizes the provided [miniMessage] instance and [tagResolver] to deserialize
 * the message content before sending it to the audience.
 *
 * @see sendMessage
 */
fun Audience.sendRichMessage(
    message: Message,
    tagResolver: TagResolver = TagResolver.empty(),
    miniMessage: MiniMessage = io.github.tozydev.foras.adventure.minimessage.miniMessage,
) = sendMessage(message) { input -> miniMessage.deserialize(input, tagResolver) }
