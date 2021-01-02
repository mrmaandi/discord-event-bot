package com.planetsf.discordsampleflipbot.bot.content

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.rest.util.Color
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DiscordMessages {
    fun createAboutEmbed(channel: MessageChannel): Mono<Message> {
        val URL = "https://i.imgur.com/2FzN6vM.png"
        val TITLE = "Sample Flip Challenge Reminder"
        val IMG = "https://cdn.discordapp.com/icons/685851452228370501/a_9e5dd2406c01f09bf9226196b3b0e7c2.webp?size=128"
        val IMG2 = "https://i.imgur.com/iqKU9ty.png"

        return channel.createEmbed { spec ->
            spec.setColor(Color.VIVID_VIOLET)
                .setTitle(TITLE.toUpperCase())
                .setUrl(URL)
                .setThumbnail(IMG)
                //.setDescription("You must follow these rules.")
                //.setFooter("Your truly, Sample Flip Bot", IMG2)
                .addField(
                    "⌛️ Time Limit",
                    "The time limit for the challenge is `2h30m`. Your sample flip must be submitted before the end of the challenge.",
                    false
                )
                .addField(
                    "\uD83C\uDFB6  Flip Length",
                    "Your submission length must be under `2m30s`.",
                    false
                )
                .addField(
                    "\uD83C\uDFA7  Sample Usage",
                    "Your submission must include the sample and it should be recognizable.",
                    false
                )
                .addField(
                    "\uD83C\uDF9F  Submissions",
                    "Submission have to be uploaded to this Discord channel. The submissions will be listened to in their upload order.",
                    false
                )
        }
    }
}