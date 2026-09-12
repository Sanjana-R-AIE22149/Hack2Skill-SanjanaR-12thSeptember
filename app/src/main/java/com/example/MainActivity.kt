package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Event
import com.example.ui.components.AdvancedFilterSheet
import com.example.ui.components.ThemeSelectorSheet
import com.example.ui.screens.EditEventDialog
import com.example.ui.screens.EventDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HostEventDialog
import com.example.ui.screens.MapsGroundingScreen
import com.example.ui.screens.MyTicketsScreen
import com.example.ui.screens.OrganizerDashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegistrationBottomSheet
import com.example.ui.screens.RegistrationSuccessDialog
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.BlrSaffron
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EventsViewModel

enum class AppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    EXPLORE("Explore", Icons.Default.Explore),
    MAPS_AI("Maps AI", Icons.Default.Map),
    PASSES("Passes", Icons.Default.ConfirmationNumber),
    ORGANIZER("Organizer", Icons.Default.Dashboard),
    PROFILE("Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EventsViewModel = viewModel()
            val userProfile by viewModel.userProfile.collectAsState()
            val currentTheme = remember(userProfile.selectedTheme) {
                AppThemeSetting.fromId(userProfile.selectedTheme)
            }

            MyApplicationTheme(themeSetting = currentTheme) {
                BengaluruEventsApp(
                    viewModel = viewModel,
                    currentTheme = currentTheme
                )
            }
        }
    }
}

@Composable
fun BengaluruEventsApp(
    viewModel: EventsViewModel,
    currentTheme: AppThemeSetting
) {
    var currentTab by remember { mutableStateOf(AppTab.EXPLORE) }
    var selectedEventForDetail by remember { mutableStateOf<Event?>(null) }
    var isHostDialogOpen by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }
    var isThemeSheetOpen by remember { mutableStateOf(false) }
    var isFilterSheetOpen by remember { mutableStateOf(false) }

    val filteredEvents by viewModel.filteredEvents.collectAsState()
    val rawEvents by viewModel.rawEvents.collectAsState()
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSubCategory by viewModel.selectedSubCategory.collectAsState()
    val selectedNeighborhood by viewModel.selectedNeighborhood.collectAsState()
    val selectedDateRange by viewModel.selectedDateRange.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()
    val filterFavoritesOnly by viewModel.filterFavoritesOnly.collectAsState()

    val registeringEvent by viewModel.registeringEvent.collectAsState()
    val lastConfirmedTicket by viewModel.lastConfirmedTicket.collectAsState()

    val mapsChatMessages by viewModel.mapsChatMessages.collectAsState()
    val isMapsQueryLoading by viewModel.isMapsQueryLoading.collectAsState()

    val venueVerificationText by viewModel.venueVerificationText.collectAsState()
    val isVerifyingVenue by viewModel.isVerifyingVenue.collectAsState()

    // 1. Theme Selector Bottom Sheet
    if (isThemeSheetOpen) {
        ThemeSelectorSheet(
            currentTheme = currentTheme,
            onThemeSelect = { selectedSetting ->
                viewModel.updateTheme(selectedSetting.id)
                isThemeSheetOpen = false
            },
            onDismiss = { isThemeSheetOpen = false }
        )
    }

    // 2. Advanced Filter & Sort Sheet
    if (isFilterSheetOpen) {
        AdvancedFilterSheet(
            selectedCategory = selectedCategory,
            onCategorySelect = { viewModel.selectedCategory.value = it },
            selectedSubCategory = selectedSubCategory,
            onSubCategorySelect = { viewModel.selectedSubCategory.value = it },
            selectedNeighborhood = selectedNeighborhood,
            onNeighborhoodSelect = { viewModel.selectedNeighborhood.value = it },
            selectedDateRange = selectedDateRange,
            onDateRangeSelect = { viewModel.selectedDateRange.value = it },
            selectedSortOption = selectedSortOption,
            onSortOptionSelect = { viewModel.selectedSortOption.value = it },
            filterFavoritesOnly = filterFavoritesOnly,
            onFavoritesToggle = { viewModel.filterFavoritesOnly.value = it },
            onResetFilters = { viewModel.resetAllFilters() },
            onDismiss = { isFilterSheetOpen = false }
        )
    }

    // 3. Host Event Modal Dialog
    if (isHostDialogOpen) {
        HostEventDialog(
            onDismiss = {
                isHostDialogOpen = false
                viewModel.clearVenueVerification()
            },
            onSubmit = { title, category, subCategory, neighborhood, venueName, venueAddress, dateStr, timeStr, price, capacity, desc, perks, rules, tags, orgName, orgContact ->
                viewModel.createEvent(
                    title = title,
                    category = category,
                    subCategory = subCategory,
                    neighborhood = neighborhood,
                    venueName = venueName,
                    venueAddress = venueAddress,
                    dateStr = dateStr,
                    timeStr = timeStr,
                    price = price,
                    capacity = capacity,
                    description = desc,
                    perks = perks,
                    rules = rules,
                    tags = tags,
                    organizerName = orgName,
                    organizerContact = orgContact
                ) {
                    isHostDialogOpen = false
                }
            },
            onVerifyVenueWithMaps = { venue, neighborhood ->
                viewModel.verifyVenueWithMaps(venue, neighborhood)
            },
            venueVerificationText = venueVerificationText,
            isVerifyingVenue = isVerifyingVenue
        )
    }

    // 4. Edit Existing Event Modal Dialog
    if (editingEvent != null) {
        EditEventDialog(
            event = editingEvent!!,
            onDismiss = {
                editingEvent = null
                viewModel.clearVenueVerification()
            },
            onSave = { updatedEvent ->
                viewModel.updateExistingEvent(updatedEvent) {
                    editingEvent = null
                }
            },
            onDelete = { eventId ->
                viewModel.deleteEvent(eventId)
                editingEvent = null
            },
            onVerifyVenueWithMaps = { venue, neighborhood ->
                viewModel.verifyVenueWithMaps(venue, neighborhood)
            },
            venueVerificationText = venueVerificationText,
            isVerifyingVenue = isVerifyingVenue
        )
    }

    // 5. Registration Bottom Sheet with Profile Pre-Fill
    if (registeringEvent != null) {
        RegistrationBottomSheet(
            event = registeringEvent!!,
            userProfile = userProfile,
            onDismiss = { viewModel.dismissRegistration() },
            onSubmit = { name, email, phone, ticketType, detail, ageGroup, primaryInterest, experienceLevel ->
                viewModel.submitRegistration(
                    event = registeringEvent!!,
                    name = name,
                    email = email,
                    phone = phone,
                    ticketType = ticketType,
                    detail = detail,
                    ageGroup = ageGroup,
                    primaryInterest = primaryInterest,
                    experienceLevel = experienceLevel,
                    onSuccess = {
                        // Handled via lastConfirmedTicket
                    }
                )
            }
        )
    }

    // 6. Registration Success Confirmation Dialog
    if (lastConfirmedTicket != null) {
        RegistrationSuccessDialog(
            registration = lastConfirmedTicket!!,
            onDismiss = { viewModel.dismissConfirmedTicket() },
            onViewTickets = {
                viewModel.dismissConfirmedTicket()
                selectedEventForDetail = null
                currentTab = AppTab.PASSES
            }
        )
    }

    // Detail Screen or Main Navigation Scaffold
    if (selectedEventForDetail != null) {
        val currentEvent = rawEvents.find { it.id == selectedEventForDetail!!.id } ?: selectedEventForDetail!!
        EventDetailScreen(
            event = currentEvent,
            onBackClick = { selectedEventForDetail = null },
            onRegisterClick = { viewModel.startRegistration(currentEvent) }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    AppTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                if (tab == AppTab.PASSES && allRegistrations.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = BlrSaffron,
                                                contentColor = androidx.compose.ui.graphics.Color.White
                                            ) {
                                                Text("${allRegistrations.size}", fontSize = 10.sp)
                                            }
                                        }
                                    ) {
                                        Icon(tab.icon, contentDescription = tab.title, modifier = Modifier.size(22.dp))
                                    }
                                } else {
                                    Icon(tab.icon, contentDescription = tab.title, modifier = Modifier.size(22.dp))
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentTab) {
                    AppTab.EXPLORE -> {
                        HomeScreen(
                            events = filteredEvents,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.searchQuery.value = it },
                            selectedCategory = selectedCategory,
                            onCategoryChange = { viewModel.selectedCategory.value = it },
                            selectedSubCategory = selectedSubCategory,
                            onSubCategoryChange = { viewModel.selectedSubCategory.value = it },
                            selectedNeighborhood = selectedNeighborhood,
                            onNeighborhoodChange = { viewModel.selectedNeighborhood.value = it },
                            selectedDateRange = selectedDateRange,
                            onDateRangeChange = { viewModel.selectedDateRange.value = it },
                            selectedSortOption = selectedSortOption,
                            onSortOptionChange = { viewModel.selectedSortOption.value = it },
                            filterFavoritesOnly = filterFavoritesOnly,
                            onToggleFavoritesOnly = { viewModel.filterFavoritesOnly.value = it },
                            favoriteIds = favoriteIds,
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            currentTheme = currentTheme,
                            onOpenThemeSheet = { isThemeSheetOpen = true },
                            onOpenFilterSheet = { isFilterSheetOpen = true },
                            onResetAllFilters = { viewModel.resetAllFilters() },
                            onEventClick = { selectedEventForDetail = it },
                            onRegisterClick = { viewModel.startRegistration(it) },
                            onOpenMapsAssistant = { currentTab = AppTab.MAPS_AI }
                        )
                    }

                    AppTab.MAPS_AI -> {
                        MapsGroundingScreen(
                            chatMessages = mapsChatMessages,
                            isLoading = isMapsQueryLoading,
                            onSendMessage = { prompt ->
                                viewModel.askMapsAssistant(prompt)
                            }
                        )
                    }

                    AppTab.PASSES -> {
                        MyTicketsScreen(
                            registrations = allRegistrations,
                            onCancelRegistration = { reg ->
                                viewModel.cancelRegistration(reg)
                            },
                            onExploreEvents = {
                                currentTab = AppTab.EXPLORE
                            }
                        )
                    }

                    AppTab.ORGANIZER -> {
                        OrganizerDashboardScreen(
                            events = rawEvents,
                            allRegistrations = allRegistrations,
                            onToggleCheckIn = { id, currentStatus ->
                                viewModel.toggleCheckIn(id, currentStatus)
                            },
                            onHostNewEvent = {
                                isHostDialogOpen = true
                            },
                            onEditEvent = { event ->
                                editingEvent = event
                            },
                            onVerifyVenueWithMaps = { venue, neighborhood ->
                                viewModel.verifyVenueWithMaps(venue, neighborhood)
                            },
                            venueVerificationText = venueVerificationText,
                            isVerifyingVenue = isVerifyingVenue
                        )
                    }

                    AppTab.PROFILE -> {
                        ProfileScreen(
                            userProfile = userProfile,
                            registrations = allRegistrations,
                            allEvents = rawEvents,
                            favoriteIds = favoriteIds,
                            currentTheme = currentTheme,
                            onSaveProfile = { name, email, phone, bio, locality, interests ->
                                viewModel.saveProfile(name, email, phone, bio, locality, interests)
                            },
                            onSelectTheme = { setting ->
                                viewModel.updateTheme(setting.id)
                            },
                            onToggleFavorite = { eventId ->
                                viewModel.toggleFavorite(eventId)
                            },
                            onEventClick = { event ->
                                selectedEventForDetail = event
                            },
                            onRegisterClick = { event ->
                                viewModel.startRegistration(event)
                            },
                            onOpenThemeSheet = { isThemeSheetOpen = true }
                        )
                    }
                }
            }
        }
    }
}
