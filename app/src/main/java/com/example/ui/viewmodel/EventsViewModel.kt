package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiMapsService
import com.example.data.db.AppDatabase
import com.example.data.model.Event
import com.example.data.model.Registration
import com.example.data.model.UserProfile
import com.example.data.repository.EventsRepository
import com.example.ui.theme.AppThemeSetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class EventSortOption(val title: String) {
    DATE_ASC("Date (Soonest)"),
    DATE_DESC("Date (Furthest)"),
    POPULARITY("Popularity (Most Booked)"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low")
}

enum class DateRangeFilter(val title: String) {
    ALL("All Dates"),
    THIS_WEEKEND("This Weekend"),
    NEXT_7_DAYS("Next 7 Days"),
    NEXT_30_DAYS("Next 30 Days"),
    OCTOBER("October 2026"),
    NOVEMBER("November 2026")
}

data class MapsChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val placeNames: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class AttendeeAnalytics(
    val totalAttendees: Int = 0,
    val checkedInCount: Int = 0,
    val checkInRate: Float = 0f,
    val ageDemographics: Map<String, Int> = emptyMap(),
    val primaryInterests: Map<String, Int> = emptyMap(),
    val experienceLevels: Map<String, Int> = emptyMap()
)

class EventsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventsRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = EventsRepository(
            eventDao = db.eventDao(),
            registrationDao = db.registrationDao(),
            userFavoriteDao = db.userFavoriteDao(),
            userProfileDao = db.userProfileDao()
        )

        viewModelScope.launch {
            repository.updateAllAttendeeNamesAndEmails("Sanjana R", "sanjana.r@gmail.com")
            userProfile.collect { profile ->
                if (profile.name != "Sanjana R" || profile.email != "sanjana.r@gmail.com") {
                    updateProfile(
                        name = "Sanjana R",
                        email = "sanjana.r@gmail.com",
                        phone = profile.phone,
                        bio = profile.bio,
                        preferredNeighborhood = profile.preferredNeighborhood,
                        interests = profile.interests
                    )
                }
            }
        }
    }

    val rawEvents: StateFlow<List<Event>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRegistrations: StateFlow<List<Registration>> = repository.allRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteIds: StateFlow<Set<Long>> = repository.allFavoriteIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val currentTheme: StateFlow<AppThemeSetting> = userProfile
        .map { AppThemeSetting.fromId(it.selectedTheme) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeSetting.INDIAN_TRADITIONAL)

    // Filters & Sorting state
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedSubCategory = MutableStateFlow("All") // "All", "Running", "Coding", "Design", "Open Source", "Cycling", "Tabletop"
    val selectedNeighborhood = MutableStateFlow("All")
    val selectedDateRange = MutableStateFlow(DateRangeFilter.ALL)
    val selectedSortOption = MutableStateFlow(EventSortOption.DATE_ASC)
    val filterFavoritesOnly = MutableStateFlow(false)

    // Legacy filter compatibility
    val selectedFilter = MutableStateFlow("All")

    // Filtered & Sorted events
    val filteredEvents: StateFlow<List<Event>> = combine(
        rawEvents,
        searchQuery,
        selectedCategory,
        selectedSubCategory,
        selectedNeighborhood,
        selectedDateRange,
        selectedSortOption,
        filterFavoritesOnly,
        favoriteIds
    ) { args ->
        val events = args[0] as List<Event>
        val query = args[1] as String
        val category = args[2] as String
        val subCategory = args[3] as String
        val neighborhood = args[4] as String
        val dateRange = args[5] as DateRangeFilter
        val sortOption = args[6] as EventSortOption
        val favOnly = args[7] as Boolean
        val favIds = args[8] as Set<Long>

        val now = System.currentTimeMillis()
        val dayMs = 86400000L

        events.filter { event ->
            // Keyword search
            val matchesQuery = query.isBlank() ||
                    event.title.contains(query, ignoreCase = true) ||
                    event.neighborhood.contains(query, ignoreCase = true) ||
                    event.venueName.contains(query, ignoreCase = true) ||
                    event.venueAddress.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true) ||
                    event.perks.contains(query, ignoreCase = true) ||
                    event.tags.contains(query, ignoreCase = true) ||
                    event.subCategory.contains(query, ignoreCase = true)

            // Category filter
            val matchesCategory = category == "All" ||
                    (category == "Marathons" && event.category == "Marathon") ||
                    (category == "Hackathons" && event.category == "Hackathon") ||
                    (category == "Community" && event.category == "Community Activity") ||
                    event.category.equals(category, ignoreCase = true)

            // Subcategory filter (e.g. Running, Coding, Design)
            val matchesSubCategory = subCategory == "All" ||
                    event.subCategory.equals(subCategory, ignoreCase = true)

            // Neighborhood filter
            val matchesNeighborhood = neighborhood == "All" ||
                    event.neighborhood.equals(neighborhood, ignoreCase = true)

            // Favorites filter
            val matchesFav = !favOnly || favIds.contains(event.id)

            // Date Range filter
            val matchesDateRange = when (dateRange) {
                DateRangeFilter.ALL -> true
                DateRangeFilter.THIS_WEEKEND -> {
                    // within next 5 days
                    event.eventTimestamp in now..(now + dayMs * 5)
                }
                DateRangeFilter.NEXT_7_DAYS -> {
                    event.eventTimestamp in now..(now + dayMs * 7)
                }
                DateRangeFilter.NEXT_30_DAYS -> {
                    event.eventTimestamp in now..(now + dayMs * 30)
                }
                DateRangeFilter.OCTOBER -> {
                    event.dateStr.contains("Oct", ignoreCase = true)
                }
                DateRangeFilter.NOVEMBER -> {
                    event.dateStr.contains("Nov", ignoreCase = true)
                }
            }

            matchesQuery && matchesCategory && matchesSubCategory && matchesNeighborhood && matchesFav && matchesDateRange
        }.let { filtered ->
            when (sortOption) {
                EventSortOption.DATE_ASC -> filtered.sortedBy { it.eventTimestamp.takeIf { ts -> ts > 0 } ?: Long.MAX_VALUE }
                EventSortOption.DATE_DESC -> filtered.sortedByDescending { it.eventTimestamp }
                EventSortOption.POPULARITY -> filtered.sortedByDescending {
                    if (it.totalCapacity > 0) it.registeredCount.toFloat() / it.totalCapacity else it.registeredCount.toFloat()
                }
                EventSortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
                EventSortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected event for detail view
    private val _selectedEventId = MutableStateFlow<Long?>(null)
    val selectedEventId: StateFlow<Long?> = _selectedEventId.asStateFlow()

    fun selectEvent(eventId: Long?) {
        _selectedEventId.value = eventId
    }

    // Toggle favorite
    fun toggleFavorite(eventId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(eventId)
        }
    }

    // Profile updates
    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        bio: String,
        preferredNeighborhood: String,
        interests: String
    ) {
        viewModelScope.launch {
            val updated = userProfile.value.copy(
                name = name,
                email = email,
                phone = phone,
                bio = bio,
                preferredNeighborhood = preferredNeighborhood,
                interests = interests
            )
            repository.saveUserProfile(updated)
        }
    }

    fun saveProfile(
        name: String,
        email: String,
        phone: String,
        bio: String,
        locality: String,
        interests: String
    ) {
        updateProfile(name, email, phone, bio, locality, interests)
    }

    fun setTheme(theme: AppThemeSetting) {
        viewModelScope.launch {
            repository.updateTheme(theme.id)
        }
    }

    fun updateTheme(themeId: String) {
        viewModelScope.launch {
            repository.updateTheme(themeId)
        }
    }

    fun resetAllFilters() {
        searchQuery.value = ""
        selectedCategory.value = "All"
        selectedSubCategory.value = "All"
        selectedNeighborhood.value = "All"
        selectedDateRange.value = DateRangeFilter.ALL
        selectedSortOption.value = EventSortOption.DATE_ASC
        filterFavoritesOnly.value = false
    }

    // Google Maps Grounding Chat
    private val _mapsChatMessages = MutableStateFlow<List<MapsChatMessage>>(
        listOf(
            MapsChatMessage(
                isUser = false,
                text = "Namaskara! I am your Bengaluru Events & Venue Navigator, grounded directly with Google Maps.\n\n" +
                        "Ask me anything about event locations in Bengaluru — such as:\n" +
                        "• Best Metro line to reach Kanteerava Stadium for midnight run\n" +
                        "• Parking and traffic flow near HSR Layout Sector 1\n" +
                        "• Cubbon Park gates open for sunrise marathon runners\n" +
                        "• Coffee spots and co-working hubs near Koramangala 4th Block"
            )
        )
    )
    val mapsChatMessages: StateFlow<List<MapsChatMessage>> = _mapsChatMessages.asStateFlow()

    private val _isMapsQueryLoading = MutableStateFlow(false)
    val isMapsQueryLoading: StateFlow<Boolean> = _isMapsQueryLoading.asStateFlow()

    fun askMapsAssistant(prompt: String, venueContext: String? = null) {
        if (prompt.isBlank() || _isMapsQueryLoading.value) return

        val userMessage = MapsChatMessage(isUser = true, text = prompt)
        _mapsChatMessages.value = _mapsChatMessages.value + userMessage
        _isMapsQueryLoading.value = true

        viewModelScope.launch {
            val response = repository.askMapsGrounding(prompt, venueContext)
            val aiMessage = MapsChatMessage(
                isUser = false,
                text = response.text,
                placeNames = response.placeNames
            )
            _mapsChatMessages.value = _mapsChatMessages.value + aiMessage
            _isMapsQueryLoading.value = false
        }
    }

    // Registration flow state
    private val _registeringEvent = MutableStateFlow<Event?>(null)
    val registeringEvent: StateFlow<Event?> = _registeringEvent.asStateFlow()

    private val _lastConfirmedTicket = MutableStateFlow<Registration?>(null)
    val lastConfirmedTicket: StateFlow<Registration?> = _lastConfirmedTicket.asStateFlow()

    fun startRegistration(event: Event) {
        _registeringEvent.value = event
    }

    fun dismissRegistration() {
        _registeringEvent.value = null
    }

    fun dismissConfirmedTicket() {
        _lastConfirmedTicket.value = null
    }

    fun submitRegistration(
        event: Event,
        name: String,
        email: String,
        phone: String,
        ticketType: String,
        detail: String,
        ageGroup: String = "25-34",
        primaryInterest: String = "Athletics",
        experienceLevel: String = "Intermediate",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val ticket = repository.registerForEvent(
                event = event,
                attendeeName = name,
                attendeeEmail = email,
                attendeePhone = phone,
                ticketType = ticketType,
                ticketPrice = event.price,
                attendeeDetail = detail,
                ageGroup = ageGroup,
                primaryInterest = primaryInterest,
                experienceLevel = experienceLevel
            )
            _registeringEvent.value = null
            _lastConfirmedTicket.value = ticket
            onSuccess()
        }
    }

    fun cancelRegistration(registration: Registration) {
        viewModelScope.launch {
            repository.cancelRegistration(registration.id, registration.eventId)
        }
    }

    fun toggleCheckIn(registrationId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleCheckIn(registrationId, !currentStatus)
        }
    }

    // Organizer Host & Edit Event
    private val _isHostingEvent = MutableStateFlow(false)
    val isHostingEvent: StateFlow<Boolean> = _isHostingEvent.asStateFlow()

    private val _editingEvent = MutableStateFlow<Event?>(null)
    val editingEvent: StateFlow<Event?> = _editingEvent.asStateFlow()

    fun startEditingEvent(event: Event) {
        _editingEvent.value = event
    }

    fun dismissEditingEvent() {
        _editingEvent.value = null
    }

    fun updateExistingEvent(event: Event, onDone: () -> Unit) {
        viewModelScope.launch {
            _isHostingEvent.value = true
            repository.updateEvent(event)
            _isHostingEvent.value = false
            _editingEvent.value = null
            onDone()
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
        }
    }

    private val _venueVerificationText = MutableStateFlow<String?>(null)
    val venueVerificationText: StateFlow<String?> = _venueVerificationText.asStateFlow()

    private val _isVerifyingVenue = MutableStateFlow(false)
    val isVerifyingVenue: StateFlow<Boolean> = _isVerifyingVenue.asStateFlow()

    fun verifyVenueWithMaps(venue: String, neighborhood: String) {
        if (venue.isBlank()) return
        _isVerifyingVenue.value = true
        _venueVerificationText.value = null

        viewModelScope.launch {
            val prompt = "Verify the exact location, nearest Namma Metro station, landmarks, and parking tips for the venue: '$venue' located in '$neighborhood, Bengaluru'."
            val response = repository.askMapsGrounding(prompt, "$venue, $neighborhood, Bengaluru")
            _venueVerificationText.value = response.text
            _isVerifyingVenue.value = false
        }
    }

    fun clearVenueVerification() {
        _venueVerificationText.value = null
    }

    fun createEvent(
        title: String,
        category: String,
        subCategory: String,
        neighborhood: String,
        venueName: String,
        venueAddress: String,
        dateStr: String,
        timeStr: String,
        price: Double,
        capacity: Int,
        description: String,
        perks: String,
        rules: String,
        tags: String,
        organizerName: String,
        organizerContact: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            _isHostingEvent.value = true
            val bannerType = when (category) {
                "Marathon" -> "marathon"
                "Hackathon" -> "hackathon"
                else -> "community"
            }
            val newEvent = Event(
                title = title,
                category = category,
                subCategory = subCategory,
                neighborhood = neighborhood,
                venueName = venueName,
                venueAddress = venueAddress,
                dateStr = dateStr,
                timeStr = timeStr,
                eventTimestamp = System.currentTimeMillis() + (86400000L * 30),
                price = price,
                totalCapacity = capacity,
                registeredCount = 0,
                description = description,
                perks = perks,
                rules = rules,
                tags = tags,
                organizerName = organizerName,
                organizerContact = organizerContact,
                isOrganizerCreated = true,
                bannerType = bannerType
            )
            repository.hostEvent(newEvent)
            _isHostingEvent.value = false
            _venueVerificationText.value = null
            onDone()
        }
    }

    // Computed Attendee Analytics for Organizer
    fun computeAnalyticsForEvent(eventId: Long? = null): StateFlow<AttendeeAnalytics> {
        return allRegistrations.map { registrations ->
            val targetList = if (eventId != null) {
                registrations.filter { it.eventId == eventId }
            } else {
                registrations
            }

            val total = targetList.size
            val checkedIn = targetList.count { it.isCheckedIn }
            val checkInRate = if (total > 0) (checkedIn.toFloat() / total.toFloat()) * 100f else 0f

            val ageMap = mutableMapOf<String, Int>()
            val interestMap = mutableMapOf<String, Int>()
            val expMap = mutableMapOf<String, Int>()

            targetList.forEach { reg ->
                val age = if (reg.ageGroup.isNotBlank()) reg.ageGroup else "25-34"
                ageMap[age] = (ageMap[age] ?: 0) + 1

                val interest = if (reg.primaryInterest.isNotBlank()) reg.primaryInterest else "General"
                interestMap[interest] = (interestMap[interest] ?: 0) + 1

                val exp = if (reg.experienceLevel.isNotBlank()) reg.experienceLevel else "Intermediate"
                expMap[exp] = (expMap[exp] ?: 0) + 1
            }

            AttendeeAnalytics(
                totalAttendees = total,
                checkedInCount = checkedIn,
                checkInRate = checkInRate,
                ageDemographics = ageMap,
                primaryInterests = interestMap,
                experienceLevels = expMap
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AttendeeAnalytics())
    }
}
