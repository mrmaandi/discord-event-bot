package com.planetsf.discordsampleflipbot.bot.eventlistener

import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageUpdateEvent
import org.springframework.stereotype.Service

import reactor.core.publisher.Mono


@Service
class MessageUpdateListener : MessageListener(), EventListener<MessageUpdateEvent> {
    override val eventType: Class<MessageUpdateEvent>
        get() = MessageUpdateEvent::class.java

    override fun execute(event: MessageUpdateEvent): Mono<Void> {
        return Mono.just(event)
            .filter { obj: MessageUpdateEvent -> obj.isContentChanged }
            .flatMap { obj: MessageUpdateEvent -> obj.message }
            .flatMap { eventMessage: Message -> super.processCommand(eventMessage) }
    }
}