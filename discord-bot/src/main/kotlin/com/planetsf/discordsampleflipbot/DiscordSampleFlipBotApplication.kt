package com.planetsf.discordsampleflipbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DiscordSampleFlipBotApplication

fun main(args: Array<String>) {
    runApplication<DiscordSampleFlipBotApplication>(*args)
}
