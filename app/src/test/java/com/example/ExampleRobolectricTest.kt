package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.Event
import com.example.data.model.Registration
import com.example.data.repository.EventsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: EventsRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = EventsRepository(db.eventDao(), db.registrationDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Bengaluru Events", appName)
    }

    @Test
    fun testEventInsertionAndRegistrationFlow() = runBlocking {
        val event = Event(
            id = 1,
            title = "Namma Bengaluru Midnight Marathon 2026",
            category = "Marathon",
            neighborhood = "Cubbon Park",
            venueName = "Sri Kanteerava Stadium",
            venueAddress = "Kasturba Rd, Bengaluru",
            dateStr = "Oct 24, 2026",
            timeStr = "11:00 PM IST",
            price = 899.0,
            totalCapacity = 500,
            registeredCount = 10,
            description = "Bengaluru night marathon",
            perks = "Medal, T-Shirt",
            rules = "Photo ID required",
            organizerName = "Bengaluru Runners Collective",
            organizerContact = "contact@blrrunners.org"
        )

        db.eventDao().insertEvent(event)
        val loadedEvents = repository.allEvents.first()
        assertEquals(1, loadedEvents.size)
        assertEquals("Namma Bengaluru Midnight Marathon 2026", loadedEvents[0].title)

        // Register attendee
        val registration = repository.registerForEvent(
            event = event,
            attendeeName = "Sanjana R",
            attendeeEmail = "sanjana.r@gmail.com",
            attendeePhone = "+91 98451 12345",
            ticketType = "21K Half Marathon",
            ticketPrice = 899.0,
            attendeeDetail = "Size M"
        )

        assertNotNull(registration.bookingRef)
        assertTrue(registration.bookingRef.startsWith("BLR-MR-"))

        val updatedEvent = repository.getEventById(1).first()
        assertEquals(11, updatedEvent?.registeredCount)

        // Check registrations list
        val registrations = repository.allRegistrations.first()
        assertEquals(1, registrations.size)
        assertEquals("Sanjana R", registrations[0].attendeeName)
        assertEquals(false, registrations[0].isCheckedIn)

        // Toggle Check-in status
        repository.toggleCheckIn(registrations[0].id, true)
        val checkedInRegistrations = repository.allRegistrations.first()
        assertEquals(true, checkedInRegistrations[0].isCheckedIn)
    }
}
