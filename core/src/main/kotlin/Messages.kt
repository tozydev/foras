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

package io.github.tozydev.foras

import kotlin.time.Duration

/**
 * Represents a message that can be sent to entities.
 *
 * @see EmptyMessage
 * @see TextMessage
 * @see ActionbarMessage
 * @see TitleMessage
 * @see SoundMessage
 * @see CompositeMessage
 */
sealed interface Message

/** An empty message, representing no action. */
data object EmptyMessage : Message

/**
 * A text message that can be sent to entities.
 *
 * @property text The text of the message.
 */
@JvmInline
value class TextMessage(
    val text: String,
) : Message

/**
 * An action bar message that can be sent to entities.
 *
 * @property actionbar The text to display in the action bar.
 */
data class ActionbarMessage(
    val actionbar: String,
) : Message

/**
 * A title message that can be sent to entities.
 *
 * If all properties are null, the reset title message will be sent.
 *
 * @property title The title text to display. Can be null.
 * @property subtitle The subtitle text to display. Can be null.
 * @property fadeIn The duration of the fade-in effect. Can be null.
 * @property stay The duration to display the title. Can be null.
 * @property fadeOut The duration of the fade-out effect. Can be null.
 */
data class TitleMessage(
    val title: String? = null,
    val subtitle: String? = null,
    val fadeIn: Duration? = null,
    val stay: Duration? = null,
    val fadeOut: Duration? = null,
) : Message

/**
 * A sound message that can be sent to entities.
 *
 * @property sound The name of the sound to play.
 * @property source The source of the sound. Can be null.
 * @property volume The volume of the sound.
 * @property pitch The pitch of the sound.
 * @property seed The seed for the sound. Can be null.
 */
data class SoundMessage(
    val sound: String,
    val source: String? = null,
    val volume: Float = 1f,
    val pitch: Float = 1f,
    val seed: Long? = null,
) : Message

/**
 * A composite message that contains a list of other messages.
 *
 * @property messages The list of messages to send.
 */
data class CompositeMessage(
    val messages: List<Message>,
) : Message

/** Returns this message if it's not null, otherwise returns [EmptyMessage]. */
fun Message?.orEmpty() = this ?: EmptyMessage

/**
 * Creates a [CompositeMessage] from a list of messages.
 *
 * @param messages The list of messages to include in the composite message.
 */
fun Message(messages: List<Message>): Message = CompositeMessage(messages)
