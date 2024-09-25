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
import io.github.tozydev.foras.SoundMessage
import io.github.tozydev.foras.TextMessage
import io.github.tozydev.foras.TitleMessage
import io.mockk.mockk
import io.mockk.verify
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class MessageSendTest {
    private val audience: Audience = mockk(relaxed = true)

    @Test
    fun `sendMessage should send ActionbarMessage`() {
        val message = ActionbarMessage("Actionbar message!")
        val component = Component.text("Actionbar message!")

        audience.sendMessage(message)

        verify { audience.sendActionBar(component) }
    }

    @Test
    fun `sendMessage should send CompositeMessage`() {
        val message =
            CompositeMessage(
                listOf(
                    TextMessage("Message 1"),
                    ActionbarMessage("Actionbar message!"),
                ),
            )
        val text = Component.text("Message 1")
        val actionbar = Component.text("Actionbar message!")

        audience.sendMessage(message)

        verify { audience.sendMessage(text) }
        verify { audience.sendActionBar(actionbar) }
    }

    @Test
    fun `sendMessage should do nothing for EmptyMessage`() {
        val message = EmptyMessage

        audience.sendMessage(message)

        verify(exactly = 0) { audience.sendMessage(any<Component>()) }
    }

    @Test
    fun `sendMessage should send SoundMessage`() {
        val message =
            SoundMessage(
                sound = "minecraft:entity.pig.ambient",
                source = "player",
                volume = 2f,
                pitch = -1f,
                seed = 123456789L,
            )
        val sound =
            Sound
                .sound()
                .type(Key.key("minecraft:entity.pig.ambient"))
                .source(Sound.Source.PLAYER)
                .volume(2f)
                .pitch(-1f)
                .seed(123456789L)
                .build()

        audience.sendMessage(message)

        verify { audience.playSound(sound) }
    }

    @Test
    fun `sendMessage should send TextMessage`() {
        val message = TextMessage("Hello, world!")
        val component = Component.text("Hello, world!")

        audience.sendMessage(message)

        verify { audience.sendMessage(component) }
    }

    @Test
    fun `sendMessage should send TitleMessage`() {
        val message =
            TitleMessage(
                title = "Title",
                subtitle = "Subtitle",
                fadeIn = 1.seconds,
                stay = 2.seconds,
                fadeOut = 3.seconds,
            )
        val title =
            Title.title(
                Component.text("Title"),
                Component.text("Subtitle"),
                Title.Times.times(1.seconds.toJavaDuration(), 2.seconds.toJavaDuration(), 3.seconds.toJavaDuration()),
            )

        audience.sendMessage(message)

        verify { audience.showTitle(title) }
    }

    @Test
    fun `sendMessage should reset title for empty TitleMessage`() {
        val message = TitleMessage()

        audience.sendMessage(message)

        verify { audience.resetTitle() }
    }
}
