package com.planetsf.discordsampleflipbot.calendar.model


data class CalendarEvent(
    val id: String,
    val name: String,
    val start: Long,
    val end: Long,
    val description: List<CalendarEventJson.EventSubmission>?,
)
