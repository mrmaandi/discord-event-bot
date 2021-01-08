package com.planetsf.discordsampleflipbot.calendar.scheduled

import com.google.api.client.util.DateTime
import com.planetsf.discordsampleflipbot.bot.config.DiscordBotConfiguration
import com.planetsf.discordsampleflipbot.bot.content.DiscordMessages
import com.planetsf.discordsampleflipbot.calendar.model.CalendarEvent
import com.planetsf.discordsampleflipbot.calendar.model.CurrentEventHolder
import com.planetsf.discordsampleflipbot.calendar.service.CalendarService
import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.rest.util.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class CalendarSync(private val calendarService: CalendarService) {

    @Autowired
    private val gatewayDiscordClient: DiscordBotConfiguration? = null
    @Autowired
    private val discordMessages: DiscordMessages? = null

    @Scheduled(fixedRate = 5000)
    fun syncCalendarEvents() {
        val nextEvents: List<CalendarEvent> = calendarService.getNextCalendarEvent()
        val activeEvent: CalendarEvent? = CurrentEventHolder.currentEvent

        if (nextEvents.isEmpty()) {
            /*needs to be worked on*/
            /*endActiveEvent(nextEvent)*/
        } else {
            val nextEvent: CalendarEvent = nextEvents[0]

            if (isNextCalendarEventNow(nextEvent)) {
                endActiveEvent(nextEvent)

                if (activeEvent == null || (activeEvent.id != nextEvent.id)) {
                    startActiveEvent(nextEvent)
                }
            } else {
                endActiveEvent(nextEvent)
            }
        }
    }

    private fun isNextCalendarEventNow(nextEvent: CalendarEvent): Boolean {
        val currentTime = DateTime(System.currentTimeMillis()).value

        return nextEvent.start < currentTime && currentTime < nextEvent.end
    }

    private fun startActiveEvent(nextEvent: CalendarEvent) {
        val IMG = "https://cdn.discordapp.com/icons/685851452228370501/a_9e5dd2406c01f09bf9226196b3b0e7c2.webp?size=128"

        CurrentEventHolder.currentEvent = nextEvent
        log.info("Current event started: {}", nextEvent.name)

        // start event message
        getDiscordTextChannel().createEmbed { spec ->
            spec.setColor(Color.GREEN)
                .setTitle("Sample Flip Challenge started!")
                .setUrl("https://www.google.com")
                .setThumbnail(IMG)
                .setDescription("The sample flip challenge has started! :)")
                .addField(
                    "⌛️ Watch on Twitch.TV",
                    "https://www.twitch.tv/its_bustre",
                    false
                )
        }.block()
        // print rules
        discordMessages!!.createAboutEmbed(getDiscordTextChannel()).block()
    }

    private fun endActiveEvent(nextEvent: CalendarEvent?) {
        val activeEvent: CalendarEvent? = CurrentEventHolder.currentEvent
        val IMG = "https://cdn.discordapp.com/icons/685851452228370501/a_9e5dd2406c01f09bf9226196b3b0e7c2.webp?size=128"

        if (activeEvent != null) {
            if (nextEvent != null && activeEvent.id != nextEvent.id) {
                CurrentEventHolder.currentEvent = null
                log.info("Event ended: {}", activeEvent.name)

                getDiscordTextChannel().createEmbed { spec ->
                    spec.setColor(Color.RED)
                        .setTitle("Sample Flip Challenge ended!")
                        .setUrl("https://www.google.com")
                        .setThumbnail(IMG)
                        .setDescription("Thank you for participating! :)")
                }.block()
            }
        }
    }

    private fun getDiscordTextChannel(): TextChannel = gatewayDiscordClient!!.client!!
        .guilds
        .filter { guild -> guild.name == "Clubhouse" }
        .blockFirst()
        ?.channels
        ?.filter { channel -> channel.name == "bot-testing" }?.blockFirst() as TextChannel

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CalendarSync::class.java)
    }
}