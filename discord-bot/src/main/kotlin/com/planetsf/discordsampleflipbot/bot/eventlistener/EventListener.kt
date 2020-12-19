package com.planetsf.discordsampleflipbot.bot.eventlistener

import discord4j.core.event.domain.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono


interface EventListener<T : Event> {
    val eventType: Class<T>

    fun execute(event: T): Mono<Void>
    fun handleError(error: Throwable): Mono<Void> {
        LOG.error("Unable to process " + eventType.simpleName, error)
        return Mono.empty()
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(EventListener::class.java)
    }
}