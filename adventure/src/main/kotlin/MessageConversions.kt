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
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.ComponentDecoder
import net.kyori.adventure.title.Title
import kotlin.time.toJavaDuration

/** Represents a function that decodes a [String] into a [Component]. */
typealias MessageDecoder = ComponentDecoder<String, in Component>

internal val ResetTitle = Title.title(Component.empty(), Component.empty(), null)

/** Converts this [TextMessage] to a [Component] using the provided [decoder]. */
fun TextMessage.toComponent(decoder: MessageDecoder) = decoder.deserialize(text)

/** Converts this [ActionbarMessage] to a [Component] using the provided [decoder]. */
fun ActionbarMessage.toComponent(decoder: MessageDecoder) = decoder.deserialize(actionbar)

/** Converts this [TitleMessage] to a [Title] using the provided [decoder]. */
fun TitleMessage.toAdventureTitle(decoder: MessageDecoder): Title {
    if (title == null && subtitle == null && fadeIn == null && stay == null && fadeOut == null) {
        return ResetTitle
    }
    return Title.title(
        decoder.deserializeOrNull(title).orEmpty(),
        decoder.deserializeOrNull(subtitle).orEmpty(),
        Title.Times.times(
            fadeIn?.toJavaDuration() ?: Title.DEFAULT_TIMES.fadeIn(),
            stay?.toJavaDuration() ?: Title.DEFAULT_TIMES.stay(),
            fadeOut?.toJavaDuration() ?: Title.DEFAULT_TIMES.fadeOut(),
        ),
    )
}

/** Converts this [SoundMessage] to an Adventure [Sound] object. */
fun SoundMessage.toAdventureSound() =
    Sound.sound { builder ->
        builder.type(Key.key(sound))
        if (source != null) {
            builder.source(Sound.Source.NAMES.valueOrThrow(source!!.lowercase()))
        }
        builder.volume(volume).pitch(pitch)
        if (seed != null) {
            builder.seed(seed!!)
        }
    }
