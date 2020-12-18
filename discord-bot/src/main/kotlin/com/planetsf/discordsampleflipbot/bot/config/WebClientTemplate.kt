package com.planetsf.discordsampleflipbot.bot.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


@Service
class WebClientTemplate {
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl(BASE_URL)
            .build()
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080"
    }

}