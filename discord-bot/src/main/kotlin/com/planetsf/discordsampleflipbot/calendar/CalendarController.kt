package com.planetsf.discordsampleflipbot.calendar

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("calendar")
class CalendarController(private val calendarService: CalendarService) {

    @GetMapping("/events")
    fun events(): List<CalendarEvent> {
        return calendarService.getCalendar()
    }
}