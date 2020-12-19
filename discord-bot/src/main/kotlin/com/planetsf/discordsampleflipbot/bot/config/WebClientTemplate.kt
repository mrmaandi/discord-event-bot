package com.planetsf.discordsampleflipbot.bot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientTemplate {
    private val BASE_URL = "http://localhost:8080"

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl(BASE_URL)
            .build()
    }
}