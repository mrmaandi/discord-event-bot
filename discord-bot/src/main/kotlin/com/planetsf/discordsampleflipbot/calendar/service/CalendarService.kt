package com.planetsf.discordsampleflipbot.calendar.service

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import com.google.gson.Gson
import com.planetsf.discordsampleflipbot.calendar.helper.CALENDAR_ID
import com.planetsf.discordsampleflipbot.calendar.helper.EVENT_LENGTH
import com.planetsf.discordsampleflipbot.calendar.model.CalendarEvent
import com.planetsf.discordsampleflipbot.calendar.model.CalendarEventJson
import org.springframework.stereotype.Service
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.singletonList


@Service
class CalendarService {
    private val APPLICATION_NAME = "PlanetSF"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private val SERVICE_ACCOUNT_PK = "/planetsf-1608298242226-fbd01403de09.p12"

    fun getNextCalendarEvent(): List<CalendarEvent> {
        val service: Calendar = getAuthorizedAPICalendarService()
        val now = DateTime(System.currentTimeMillis() - EVENT_LENGTH)
        val events: Events = service.events().list(CALENDAR_ID)
            .setMaxResults(1)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()

        if (events.items.isEmpty()) {
            return emptyList()
        }

        val event = events.items[0]

        return singletonList(CalendarEvent(
            id = event.id,
            name = event.summary,
            start = event.start.dateTime.value,
            end = event.end.dateTime.value,
            description = null
        ))
    }

    fun getCalendarEvents(): List<CalendarEvent> {
        val calendarEvents = mutableListOf<CalendarEvent>()
        val service: Calendar = getAuthorizedAPICalendarService()

        // List the next 10 events
        val now = DateTime(System.currentTimeMillis() - EVENT_LENGTH)
        val events: Events = service.events().list(CALENDAR_ID)
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items: List<Event> = events.items
        if (items.isEmpty()) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")

            for (event in items) {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")
                val startTime: String = df.format(event.start.dateTime.value)

                calendarEvents.add(
                    CalendarEvent(
                        id = event.id,
                        name = event.summary,
                        start = event.start.dateTime.value,
                        end = event.end.dateTime.value,
                        description = null
                    )
                )

                System.out.printf("%s (%s)\n", event.summary, startTime)
            }
        }
        return calendarEvents;
    }

    fun getPreviousEvents(): List<CalendarEvent> {
        val calendarEvents = mutableListOf<CalendarEvent>()
        val service: Calendar = getAuthorizedAPICalendarService()

        // List the next events
        val now = DateTime(System.currentTimeMillis() - EVENT_LENGTH)
        val events: Events = service.events().list(CALENDAR_ID)
            .setTimeMax(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items: List<Event> = events.items
        if (items.isEmpty()) {
            println("No previous events found.")
        } else {
            for (event in items) {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")
                val startTime: String = df.format(event.start.dateTime.value)

                calendarEvents.add(
                    CalendarEvent(
                        id = event.id,
                        name = event.summary,
                        start = event.start.dateTime.value,
                        end = event.end.dateTime.value,
                        description = getEventDescription(event)
                    )
                )

                System.out.printf("%s (%s)\n", event.summary, startTime)
            }
        }
        return calendarEvents;
    }

    private fun getEventDescription(event: Event): List<CalendarEventJson.EventSubmission> {
        val gson = Gson()
        if (event.description != null) {
            return gson.fromJson(event.description, Array<CalendarEventJson.EventSubmission>::class.java).toList()
        }
        return emptyList();
    }

    private fun getAuthorizedAPICalendarService(): Calendar {
        val key: InputStream = CalendarService::class.java.getResourceAsStream(SERVICE_ACCOUNT_PK)

        val credentials = GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountId("clubhouse@planetsf-1608298242226.iam.gserviceaccount.com")
            .setServiceAccountScopes(listOf("https://www.googleapis.com/auth/calendar.readonly"))
            .setServiceAccountPrivateKeyFromP12File(key)
            .build()
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val service: Calendar = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
            .setApplicationName(APPLICATION_NAME)
            .build()
        return service
    }
}