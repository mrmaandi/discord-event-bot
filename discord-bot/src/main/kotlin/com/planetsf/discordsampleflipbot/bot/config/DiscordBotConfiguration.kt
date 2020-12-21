package com.planetsf.discordsampleflipbot.bot.config

import com.planetsf.discordsampleflipbot.bot.eventlistener.EventListener
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.Exception

@Configuration
class DiscordBotConfiguration {
    private val log: Logger = LoggerFactory.getLogger(DiscordBotConfiguration::class.java)

    @Value("\${discord-bot.auth-token}")
    private val token: String? = null

    var client: GatewayDiscordClient? = null

    @Bean
    fun <T : Event> gatewayDiscordClient(eventListeners: List<EventListener<T>>): GatewayDiscordClient? {
        try {
            client = DiscordClientBuilder.create(token!!)
                .build()
                .login()
                .block()
            for (listener in eventListeners) {
                client!!.on(listener.eventType)
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe()
            }
        } catch (exception: Exception) {
            log.error("Be sure to use a valid bot token!", exception)
        }
        return client
    }
}