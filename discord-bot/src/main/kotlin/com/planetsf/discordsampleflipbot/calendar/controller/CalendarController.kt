package com.planetsf.discordsampleflipbot.calendar.controller

import com.planetsf.discordsampleflipbot.calendar.model.CalendarEvent
import com.planetsf.discordsampleflipbot.calendar.service.CalendarService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("calendar")
class CalendarController(private val calendarService: CalendarService) {

    @GetMapping("/events")
    fun events(): List<CalendarEvent> {
        return calendarService.getCalendarEvents()
    }

    @GetMapping("/next")
    fun nextEvent(): List<CalendarEvent> {
        return calendarService.getNextCalendarEvent()
    }

    @GetMapping("/previous")
    fun previousEvents(): List<CalendarEvent> {
        return calendarService.getPreviousEvents()
    }
}