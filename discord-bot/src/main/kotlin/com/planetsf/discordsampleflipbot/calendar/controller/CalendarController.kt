package com.planetsf.discordsampleflipbot.calendar.controller

import com.planetsf.discordsampleflipbot.calendar.helper.FRONTEND_URL
import com.planetsf.discordsampleflipbot.calendar.model.CalendarEvent
import com.planetsf.discordsampleflipbot.calendar.service.CalendarService
import org.springframework.web.bind.annotation.CrossOrigin
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

    @CrossOrigin(origins = [FRONTEND_URL])
    @GetMapping("/next")
    fun nextEvent(): CalendarEvent? {
        return calendarService.getNextCalendarEvent()
    }

    @CrossOrigin(origins = [FRONTEND_URL])
    @GetMapping("/previous")
    fun previousEvents(): List<CalendarEvent> {
        return calendarService.getPreviousEvents()
    }
}