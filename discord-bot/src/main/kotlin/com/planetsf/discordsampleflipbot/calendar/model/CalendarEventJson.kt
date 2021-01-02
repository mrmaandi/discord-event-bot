package com.planetsf.discordsampleflipbot.calendar.model


class CalendarEventJson {

    data class EventSubmission(
        val user: String,
        val type: String,
        val fileUrl: String,
    )
}