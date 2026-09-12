package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Event
import com.example.ui.components.EventCard
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.BlrEmerald
import com.example.ui.theme.BlrSaffron
import com.example.ui.viewmodel.DateRangeFilter
import com.example.ui.viewmodel.EventSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    events: List<Event>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    selectedSubCategory: String,
    onSubCategoryChange: (String) -> Unit,
    selectedNeighborhood: String,
    onNeighborhoodChange: (String) -> Unit,
    selectedDateRange: DateRangeFilter,
    onDateRangeChange: (DateRangeFilter) -> Unit,
    selectedSortOption: EventSortOption,
    onSortOptionChange: (EventSortOption) -> Unit,
    filterFavoritesOnly: Boolean,
    onToggleFavoritesOnly: (Boolean) -> Unit,
    favoriteIds: Set<Long>,
    onToggleFavorite: (Long) -> Unit,
    currentTheme: AppThemeSetting,
    onOpenThemeSheet: () -> Unit,
    onOpenFilterSheet: () -> Unit,
    onResetAllFilters: () -> Unit,
    onEventClick: (Event) -> Unit,
    onRegisterClick: (Event) -> Unit,
    onOpenMapsAssistant: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        Triple("All", "All", null),
        Triple("Marathons", "Marathons", Icons.AutoMirrored.Filled.DirectionsRun),
        Triple("Hackathons", "Hackathons", Icons.Default.Code),
        Triple("Community", "Community", Icons.Default.Groups)
    )

    val activeFiltersCount = (if (selectedCategory != "All") 1 else 0) +
            (if (selectedSubCategory != "All") 1 else 0) +
            (if (selectedNeighborhood != "All") 1 else 0) +
            (if (selectedDateRange != DateRangeFilter.ALL) 1 else 0) +
            (if (selectedSortOption != EventSortOption.DATE_ASC) 1 else 0) +
            (if (filterFavoritesOnly) 1 else 0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Location & Theme Switcher Button
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BlrSaffron,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Bengaluru, Karnataka",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Theme Switcher Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .clickable { onOpenThemeSheet() }
                            .testTag("theme_switcher_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(currentTheme.previewPrimary, currentTheme.previewSecondary)
                                        )
                                    )
                            )
                            Text(
                                text = currentTheme.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Bengaluru Only",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Hero Spotlight Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("featured_hero_banner"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.marathon_hero),
                        contentDescription = "Bengaluru Community",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xEE0F172A),
                                        Color(0x990F172A),
                                        Color(0x33000000)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "COMMUNITY HUB • BENGALURU",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Run Marathons.\nShip Hackathons.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onOpenMapsAssistant,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("hero_maps_ai_btn")
                            ) {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Grounding Venue Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Search Bar with Advanced Filters Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search title, tracks, venues...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )

                // Advanced Filter Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (activeFiltersCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = if (activeFiltersCount == 0) CardDefaults.outlinedCardBorder() else null,
                    modifier = Modifier
                        .height(54.dp)
                        .clickable { onOpenFilterSheet() }
                        .testTag("open_advanced_filters_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = if (activeFiltersCount > 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                        if (activeFiltersCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = "$activeFiltersCount",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catKey, catLabel, icon) ->
                    val isSelected = selectedCategory == catKey
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier
                            .clickable { onCategoryChange(catKey) }
                            .testTag("category_pill_$catKey")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (icon != null) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = catLabel,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Active Quick Filter Tags Row (if any active filters)
        if (activeFiltersCount > 0) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (filterFavoritesOnly) {
                        QuickActiveTag(
                            label = "❤️ Saved Only",
                            onRemove = { onToggleFavoritesOnly(false) }
                        )
                    }
                    if (selectedSubCategory != "All") {
                        QuickActiveTag(
                            label = "Track: $selectedSubCategory",
                            onRemove = { onSubCategoryChange("All") }
                        )
                    }
                    if (selectedDateRange != DateRangeFilter.ALL) {
                        QuickActiveTag(
                            label = selectedDateRange.title,
                            onRemove = { onDateRangeChange(DateRangeFilter.ALL) }
                        )
                    }
                    if (selectedNeighborhood != "All") {
                        QuickActiveTag(
                            label = selectedNeighborhood,
                            onRemove = { onNeighborhoodChange("All") }
                        )
                    }
                    if (selectedSortOption != EventSortOption.DATE_ASC) {
                        QuickActiveTag(
                            label = selectedSortOption.title,
                            onRemove = { onSortOptionChange(EventSortOption.DATE_ASC) }
                        )
                    }

                    Text(
                        text = "Clear All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onResetAllFilters() }
                            .padding(horizontal = 6.dp)
                    )
                }
            }
        }

        // Section Title & Event Count & Current Sort Display
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bengaluru Activities (${events.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = selectedSortOption.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenFilterSheet() }
                )
            }
        }

        // Event List
        if (events.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No events match your current filters",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing your filters, adjusting your date range, or searching for other Bengaluru venues like Kanteerava or HSR Layout.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onResetAllFilters,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset All Filters", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(events, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    isFavorite = favoriteIds.contains(event.id),
                    onToggleFavorite = { onToggleFavorite(event.id) },
                    onEventClick = { onEventClick(event) },
                    onRegisterClick = { onRegisterClick(event) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun QuickActiveTag(
    label: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove filter",
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemove() },
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
