package com.planetsf.discordsampleflipbot.bot.events

import com.planetsf.discordsampleflipbot.bot.config.WebClientTemplate
import com.planetsf.discordsampleflipbot.bot.model.CalendarEvent
import discord4j.core.`object`.entity.Message
import discord4j.rest.util.Color
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.springframework.web.client.RestTemplate

import org.springframework.beans.factory.annotation.Autowired


abstract class MessageListener {
    @Autowired
    private val webClientTemplate: WebClientTemplate? = null

    fun processCommand(eventMessage: Message): Mono<Void> {
        return when (getCommandValue(eventMessage)) {
            "!challenge" -> onChallengeCommand(eventMessage)
            "!events" -> onEventsCommand(eventMessage)
            else -> onUnrecognizedCommand(eventMessage)
        }
    }

    private fun getCommandValue(eventMessage: Message): String {
        var command = "";
        Mono.just<Message>(eventMessage).map { message -> command = message.content }.block()
        return command
    }

    private fun onEventsCommand(eventMessage: Message): Mono<Void> {
        val URL = "https://i.imgur.com/2FzN6vM.png"
        val TITLE = "Sample Flip Challenge"
        val IMG = "https://cdn.discordapp.com/icons/685851452228370501/a_9e5dd2406c01f09bf9226196b3b0e7c2.webp?size=128"
        val IMG2 = "https://i.imgur.com/iqKU9ty.png"

        // get events
        val event: CalendarEvent = webClientTemplate!!.webClient()
            .get()
            .uri("/calendar/events")
            .exchange()
            .block()
            ?.bodyToFlux(CalendarEvent::class.java)
            ?.blockLast()!!

        return Mono.just<Message>(eventMessage)
            .filter { message -> message.author.map { user -> !user.isBot }.orElse(false) }
            .flatMap(Message::getChannel)
            .flatMap { channel ->
                channel.createEmbed { spec ->
                    // for each event print line
                    spec.setColor(Color.VIVID_VIOLET)
                        .setTitle(TITLE.toUpperCase())
                        .setUrl(URL)
                        .setThumbnail(IMG)
                        .setDescription("Here are the next events!")
                        .setFooter("Your truly, Sample Flip Bot", IMG2)
                        .addField(event.name, event.time, false)
                }
            }
            .then()
    }

    private fun onChallengeCommand(eventMessage: Message): Mono<Void> {
        val URL = "https://i.imgur.com/2FzN6vM.png"
        val TITLE = "Sample Flip Challenge"
        val IMG = "https://cdn.discordapp.com/icons/685851452228370501/a_9e5dd2406c01f09bf9226196b3b0e7c2.webp?size=128"
        val IMG2 = "https://i.imgur.com/iqKU9ty.png"

        return Mono.just<Message>(eventMessage)
            .filter { message -> message.author.map { user -> !user.isBot }.orElse(false) }
            .flatMap(Message::getChannel)
            .flatMap { channel ->
                channel.createEmbed { spec ->
                    spec.setColor(Color.VIVID_VIOLET)
                        .setTitle(TITLE.toUpperCase())
                        .setUrl(URL)
                        .setThumbnail(IMG)
                        .setDescription("You must follow these rules.")
                        .setFooter("Your truly, Sample Flip Bot", IMG2)
                        .addField(
                            "⌛️Time Limit",
                            "The time limit for the challenge is `2h30m`. Your sample flip must be submitted before the end of the challenge.\n.",
                            false
                        )
                        .addField(
                            "\uD83E\uDD75 Flip Length",
                            "Your submission length must be under `2m30s`.\n.",
                            false
                        )
                        .addField("\uD83E\uDD2A Sample Usage", "Your submission must include the sample.\n.", false)
                        .addField(
                            "\uD83D\uDC98 Submittions",
                            "Submission have to be uploaded to this Discord channel. The submissions will be listened to in the upload order.\n.",
                            false
                        )
                        .addField(
                            "\uD83D\uDC98 Rewards",
                            "Dubs will be automatically distributed to all participants who have linked their Discord account with Twitch.",
                            false
                        )
                }
            }
            .then()
    }

    private fun onUnrecognizedCommand(eventMessage: Message): Mono<Void> {
        return Mono.just<Message>(eventMessage).then()
    }
}