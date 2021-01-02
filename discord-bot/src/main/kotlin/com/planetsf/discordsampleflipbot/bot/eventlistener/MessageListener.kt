package com.planetsf.discordsampleflipbot.bot.eventlistener

import com.planetsf.discordsampleflipbot.bot.config.WebClientTemplate
import com.planetsf.discordsampleflipbot.bot.content.DiscordMessages
import com.planetsf.discordsampleflipbot.bot.model.CalendarEvent
import discord4j.core.`object`.entity.Message
import discord4j.rest.util.Color
import reactor.core.publisher.Mono

import org.springframework.beans.factory.annotation.Autowired
import java.text.DateFormat
import java.text.SimpleDateFormat


abstract class MessageListener {
    @Autowired
    private val webClientTemplate: WebClientTemplate? = null
    @Autowired
    private val discordMessages: DiscordMessages? = null

    fun processCommand(eventMessage: Message): Mono<Void> {
        return when (getCommandValue(eventMessage)) {
            "!about" -> onAboutCommand(eventMessage)
            "!event" -> onEventCommand(eventMessage)
            else -> onUnrecognizedCommand(eventMessage)
        }
    }

    private fun getCommandValue(eventMessage: Message): String {
        var command = "";
        Mono.just<Message>(eventMessage).map { message -> command = message.content }.block()
        return command
    }

    private fun onEventCommand(eventMessage: Message): Mono<Void> {
        val URL = "https://i.imgur.com/2FzN6vM.png"
        val TITLE = "Sample Flip Challenge Next Events"
        val IMG = "https://cdn.discordapp.com/icons/685851452228370501/a_9e5dd2406c01f09bf9226196b3b0e7c2.webp?size=128"
        val IMG2 = "https://i.imgur.com/iqKU9ty.png"
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")

        val eventsResponse: MutableList<CalendarEvent>? = webClientTemplate!!.webClient()
            .get()
            .uri("/calendar/events")
            .exchange()
            .flatMapMany { response -> response.bodyToFlux(CalendarEvent::class.java) }
            .collectList()
            .block()

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
                        .setDescription("Here are the next planned events!")
                        //.setFooter("Your truly, Sample Flip Bot", IMG2)

                    if (eventsResponse != null) {
                        for (event in eventsResponse) {
                            spec.addField(event.name, dateFormat.format(event.start.toLong()), true)
                        }
                    }
                }
            }
            .then()
    }

    private fun onAboutCommand(eventMessage: Message): Mono<Void> {
        return Mono.just<Message>(eventMessage)
            .filter { message -> message.author.map { user -> !user.isBot }.orElse(false) }
            .flatMap(Message::getChannel)
            .flatMap { channel -> discordMessages!!.createAboutEmbed(channel) }
            .then()
    }

    private fun onUnrecognizedCommand(eventMessage: Message): Mono<Void> {
        return Mono.just<Message>(eventMessage).then()
    }
}