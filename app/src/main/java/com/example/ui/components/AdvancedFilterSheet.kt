package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.DateRangeFilter
import com.example.ui.viewmodel.EventSortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdvancedFilterSheet(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    selectedSubCategory: String,
    onSubCategorySelect: (String) -> Unit,
    selectedNeighborhood: String,
    onNeighborhoodSelect: (String) -> Unit,
    selectedDateRange: DateRangeFilter,
    onDateRangeSelect: (DateRangeFilter) -> Unit,
    selectedSortOption: EventSortOption,
    onSortOptionSelect: (EventSortOption) -> Unit,
    filterFavoritesOnly: Boolean,
    onFavoritesToggle: (Boolean) -> Unit,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf("All", "Marathons", "Hackathons", "Community")
    val subCategories = listOf("All", "Running", "Coding", "Design", "Open Source", "Cycling", "Tabletop")
    val neighborhoods = listOf(
        "All", "Cubbon Park", "Koramangala", "HSR Layout",
        "Indiranagar", "Whitefield", "Electronic City", "Malleshwaram"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Filters & Sorting",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onResetFilters,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Favorites Only Switch
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Saved Favorites Only",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Show events you have bookmarked",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = filterFavoritesOnly,
                        onCheckedChange = onFavoritesToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFE11D48)
                        ),
                        modifier = Modifier.testTag("filter_favorites_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sort By Section
            SectionHeader(icon = Icons.Default.Sort, title = "Sort Events By")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EventSortOption.entries.forEach { option ->
                    val isSelected = option == selectedSortOption
                    FilterChipTag(
                        label = option.title,
                        isSelected = isSelected,
                        onClick = { onSortOptionSelect(option) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Date Range Section
            SectionHeader(icon = Icons.Default.CalendarMonth, title = "Date Range")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateRangeFilter.entries.forEach { range ->
                    val isSelected = range == selectedDateRange
                    FilterChipTag(
                        label = range.title,
                        isSelected = isSelected,
                        onClick = { onDateRangeSelect(range) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Section
            SectionHeader(icon = Icons.Default.Category, title = "Event Category")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChipTag(
                        label = cat,
                        isSelected = isSelected,
                        onClick = { onCategorySelect(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subcategory / Track Section
            SectionHeader(icon = Icons.Default.Category, title = "Interest Track (Subcategory)")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subCategories.forEach { sub ->
                    val isSelected = sub == selectedSubCategory
                    FilterChipTag(
                        label = sub,
                        isSelected = isSelected,
                        onClick = { onSubCategorySelect(sub) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bengaluru Neighborhoods
            SectionHeader(icon = Icons.Default.LocationOn, title = "Bengaluru Neighborhoods")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                neighborhoods.forEach { hood ->
                    val isSelected = hood == selectedNeighborhood
                    FilterChipTag(
                        label = hood,
                        isSelected = isSelected,
                        onClick = { onNeighborhoodSelect(hood) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FilterChipTag(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
