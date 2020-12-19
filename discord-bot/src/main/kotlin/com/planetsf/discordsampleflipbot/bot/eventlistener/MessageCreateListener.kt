package com.planetsf.discordsampleflipbot.bot.eventlistener

import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service

import reactor.core.publisher.Mono


@Service
class MessageCreateListener : MessageListener(), EventListener<MessageCreateEvent> {
    override val eventType: Class<MessageCreateEvent>
        get() = MessageCreateEvent::class.java

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        return processCommand(event.message)
    }

}