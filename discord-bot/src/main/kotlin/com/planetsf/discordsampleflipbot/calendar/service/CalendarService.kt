package com.planetsf.discordsampleflipbot.calendar.service

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import com.google.gson.Gson
import com.planetsf.discordsampleflipbot.calendar.helper.CALENDAR_ID
import com.planetsf.discordsampleflipbot.calendar.helper.EVENT_LENGTH
import com.planetsf.discordsampleflipbot.calendar.model.CalendarEvent
import com.planetsf.discordsampleflipbot.calendar.model.CalendarEventJson
import org.springframework.stereotype.Service
import java.io.*
import java.util.Collections.singletonList
import java.text.SimpleDateFormat

import java.text.DateFormat


@Service
class CalendarService {
    private val APPLICATION_NAME = "Google Calendar API"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private val TOKENS_DIRECTORY_PATH = "tokens"
    private val SCOPES = singletonList(CalendarScopes.CALENDAR_READONLY)
    private val CREDENTIALS_FILE_PATH = "/credentials.json"

    fun getNextCalendarEvent(): CalendarEvent? {
        val service: Calendar = getAuthorizedAPICalendarService()
        val now = DateTime(System.currentTimeMillis() - EVENT_LENGTH)
        val events: Events = service.events().list(CALENDAR_ID)
            .setMaxResults(1)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()

        if (events.isEmpty()) {
            return null
        }

        val event = events.items[0]

        return CalendarEvent(
            id = event.id,
            name = event.summary,
            start = event.start.dateTime.value,
            end = event.end.dateTime.value,
            description = null
        )
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

        // List the next 10 events
        val now = DateTime(System.currentTimeMillis() - EVENT_LENGTH)
        val events: Events = service.events().list(CALENDAR_ID)
            .setMaxResults(10)
            .setTimeMax(now)
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

                val parsedDescription = event.description.replace("\n", "")
                val gson = Gson()
                val eventSubmissions: List<CalendarEventJson.EventSubmission> =
                    gson.fromJson(parsedDescription, Array<CalendarEventJson.EventSubmission>::class.java).toList()

                calendarEvents.add(
                    CalendarEvent(
                        id = event.id,
                        name = event.summary,
                        start = event.start.dateTime.value,
                        end = event.end.dateTime.value,
                        description = eventSubmissions
                    )
                )

                System.out.printf("%s (%s)\n", event.summary, startTime)
            }
        }
        return calendarEvents;
    }

    private fun getAuthorizedAPICalendarService(): Calendar {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val service: Calendar = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()
        return service
    }

    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
        // Load client secrets.
        val credentials: InputStream = Calendar::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(credentials))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

}