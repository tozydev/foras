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
import net.kyori.adventure.title.Title
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class MessageConversionsTest {
    private val decoder = PlainTextComponentDecoder

    @Test
    fun `test TextMessage to Component`() {
        val message = TextMessage("Hello, world!")
        val component = Component.text("Hello, world!")

        assertEquals(component, message.toComponent(decoder))
    }

    @Test
    fun `test ActionbarMessage to Component`() {
        val message = ActionbarMessage("Actionbar message!")
        val component = Component.text("Actionbar message!")

        assertEquals(component, message.toComponent(decoder))
    }

    @Test
    fun `test TitleMessage to AdventureTitle with all components`() {
        val message =
            TitleMessage(
                title = "Title",
                subtitle = "Subtitle",
                fadeIn = 1.seconds,
                stay = 2.seconds,
                fadeOut = 3.seconds,
            )
        val titleComponent = Component.text("Title")
        val subtitleComponent = Component.text("Subtitle")

        val adventureTitle = message.toAdventureTitle(decoder)

        assertEquals(titleComponent, adventureTitle.title())
        assertEquals(subtitleComponent, adventureTitle.subtitle())
        assertEquals(1000, adventureTitle.times()?.fadeIn()?.toMillis())
        assertEquals(2000, adventureTitle.times()?.stay()?.toMillis())
        assertEquals(3000, adventureTitle.times()?.fadeOut()?.toMillis())
    }

    @Test
    fun `test TitleMessage to AdventureTitle with some components`() {
        val message =
            TitleMessage(
                title = "Title",
                fadeOut = 3.seconds,
            )
        val titleComponent = Component.text("Title")

        val adventureTitle = message.toAdventureTitle(decoder)

        assertEquals(titleComponent, adventureTitle.title())
        assertEquals(Component.empty(), adventureTitle.subtitle())
        assertEquals(Title.DEFAULT_TIMES.fadeIn().toMillis(), adventureTitle.times()?.fadeIn()?.toMillis())
        assertEquals(Title.DEFAULT_TIMES.stay().toMillis(), adventureTitle.times()?.stay()?.toMillis())
        assertEquals(3000, adventureTitle.times()?.fadeOut()?.toMillis())
    }

    @Test
    fun `test TitleMessage to ResetTitle`() {
        val message =
            TitleMessage(
                title = null,
                subtitle = null,
                fadeIn = null,
                stay = null,
                fadeOut = null,
            )

        val adventureTitle = message.toAdventureTitle(decoder)

        assertEquals(ResetTitle, adventureTitle)
    }

    @Test
    fun `test SoundMessage to AdventureSound with all components`() {
        val message =
            SoundMessage(
                sound = "minecraft:entity.pig.ambient",
                source = "player",
                volume = 2f,
                pitch = -1f,
                seed = 123456789L,
            )

        val adventureSound = message.toAdventureSound()

        assertEquals(Key.key("minecraft:entity.pig.ambient"), adventureSound.name())
        assertEquals(Sound.Source.PLAYER, adventureSound.source())
        assertEquals(2f, adventureSound.volume())
        assertEquals(-1f, adventureSound.pitch())
        assertTrue(adventureSound.seed().isPresent)
        assertEquals(123456789L, adventureSound.seed().asLong)
    }

    @Test
    fun `test SoundMessage to AdventureSound with some components`() {
        val message =
            SoundMessage(
                sound = "minecraft:entity.pig.ambient",
                volume = 2f,
            )

        val adventureSound = message.toAdventureSound()

        assertEquals(Key.key("minecraft:entity.pig.ambient"), adventureSound.name())
        assertEquals(Sound.Source.MASTER, adventureSound.source())
        assertEquals(2f, adventureSound.volume())
        assertEquals(1f, adventureSound.pitch())
        assertTrue(adventureSound.seed().isEmpty)
    }
}
