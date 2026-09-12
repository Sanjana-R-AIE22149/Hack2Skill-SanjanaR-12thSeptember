package com.example.data.repository

import com.example.data.api.GeminiMapsService
import com.example.data.db.EventDao
import com.example.data.db.RegistrationDao
import com.example.data.db.UserFavoriteDao
import com.example.data.db.UserProfileDao
import com.example.data.model.Event
import com.example.data.model.Registration
import com.example.data.model.UserFavorite
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class EventsRepository(
    private val eventDao: EventDao,
    private val registrationDao: RegistrationDao,
    private val userFavoriteDao: UserFavoriteDao? = null,
    private val userProfileDao: UserProfileDao? = null
) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()
    val allRegistrations: Flow<List<Registration>> = registrationDao.getAllRegistrations()
    val organizerEvents: Flow<List<Event>> = eventDao.getOrganizerCreatedEvents()

    val allFavoriteIds: Flow<List<Long>> = userFavoriteDao?.getAllFavoriteIds()
        ?: kotlinx.coroutines.flow.flowOf(emptyList())

    val userProfile: Flow<UserProfile?> = userProfileDao?.getUserProfile()
        ?: kotlinx.coroutines.flow.flowOf(null)

    fun getEventById(id: Long): Flow<Event?> = eventDao.getEventById(id)

    fun getRegistrationsForEvent(eventId: Long): Flow<List<Registration>> =
        registrationDao.getRegistrationsForEvent(eventId)

    suspend fun toggleFavorite(eventId: Long) {
        userFavoriteDao?.let { dao ->
            val isFav = dao.isFavorite(eventId)
            if (isFav) {
                dao.deleteFavorite(eventId)
            } else {
                dao.insertFavorite(UserFavorite(eventId = eventId))
            }
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao?.insertProfile(profile)
    }

    suspend fun updateTheme(themeName: String) {
        userProfileDao?.updateTheme(themeName)
    }

    suspend fun registerForEvent(
        event: Event,
        attendeeName: String,
        attendeeEmail: String,
        attendeePhone: String,
        ticketType: String,
        ticketPrice: Double,
        attendeeDetail: String,
        ageGroup: String = "25-34",
        primaryInterest: String = "Athletics",
        experienceLevel: String = "Intermediate"
    ): Registration {
        val categoryPrefix = when (event.category) {
            "Marathon" -> "MR"
            "Hackathon" -> "HK"
            else -> "CM"
        }
        val randomNum = Random.nextInt(1000, 9999)
        val bookingRef = "BLR-$categoryPrefix-$randomNum"

        val registration = Registration(
            eventId = event.id,
            eventTitle = event.title,
            eventCategory = event.category,
            eventDate = event.dateStr,
            eventTime = event.timeStr,
            eventVenue = "${event.venueName}, ${event.neighborhood}",
            attendeeName = attendeeName,
            attendeeEmail = attendeeEmail,
            attendeePhone = attendeePhone,
            ticketType = ticketType,
            ticketPrice = ticketPrice,
            bookingRef = bookingRef,
            registeredAt = System.currentTimeMillis(),
            isCheckedIn = false,
            attendeeDetail = attendeeDetail,
            ageGroup = ageGroup,
            primaryInterest = primaryInterest,
            experienceLevel = experienceLevel
        )

        val id = registrationDao.insertRegistration(registration)
        eventDao.incrementRegisteredCount(event.id)
        return registration.copy(id = id)
    }

    suspend fun hostEvent(event: Event): Long {
        return eventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(eventId: Long) {
        eventDao.deleteEvent(eventId)
    }

    suspend fun toggleCheckIn(registrationId: Long, newStatus: Boolean) {
        registrationDao.updateCheckInStatus(registrationId, newStatus)
    }

    suspend fun cancelRegistration(registrationId: Long, eventId: Long) {
        registrationDao.deleteRegistration(registrationId)
        eventDao.decrementRegisteredCount(eventId)
    }

    suspend fun updateAllAttendeeNamesAndEmails(name: String, email: String) {
        registrationDao.updateAllAttendeeNamesAndEmails(name, email)
    }

    suspend fun askMapsGrounding(prompt: String, venueContext: String? = null): GeminiMapsService.GroundedResponse {
        return GeminiMapsService.queryWithGoogleMaps(prompt, venueContext)
    }
}
