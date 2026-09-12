package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Event
import com.example.data.model.Registration
import com.example.ui.theme.BlrCobalt
import com.example.ui.theme.BlrEmerald
import com.example.ui.theme.BlrSaffron
import com.example.ui.theme.CommunityTeal
import com.example.ui.theme.HackathonPurple
import com.example.ui.theme.MarathonOrange

@Composable
fun OrganizerDashboardScreen(
    events: List<Event>,
    allRegistrations: List<Registration>,
    onToggleCheckIn: (registrationId: Long, currentStatus: Boolean) -> Unit,
    onHostNewEvent: () -> Unit,
    onEditEvent: (Event) -> Unit,
    onVerifyVenueWithMaps: (venue: String, neighborhood: String) -> Unit,
    venueVerificationText: String?,
    isVerifyingVenue: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedEventForAttendees by remember { mutableStateOf<Event?>(null) }

    val totalEvents = events.size
    val totalRegistrations = events.sumOf { it.registeredCount }
    val totalRevenue = events.sumOf { it.price * it.registeredCount }

    val checkedInCount = allRegistrations.count { it.isCheckedIn }
    val checkInRate = if (allRegistrations.isNotEmpty()) {
        (checkedInCount.toFloat() / allRegistrations.size.toFloat() * 100).toInt()
    } else 0

    val marathonCount = events.count { it.category == "Marathon" }
    val hackathonCount = events.count { it.category == "Hackathon" }
    val communityCount = events.count { it.category == "Community Activity" }

    // Aggregate demographics
    val ageGroupsMap = remember(allRegistrations) {
        val map = mutableMapOf("18-24" to 0, "25-34" to 0, "35-44" to 0, "45+" to 0)
        allRegistrations.forEach { reg ->
            val group = if (reg.ageGroup.isNotBlank()) reg.ageGroup else "25-34"
            map[group] = (map[group] ?: 0) + 1
        }
        map
    }

    val interestsMap = remember(allRegistrations) {
        val map = mutableMapOf<String, Int>()
        allRegistrations.forEach { reg ->
            val interest = if (reg.primaryInterest.isNotBlank()) reg.primaryInterest else "General"
            map[interest] = (map[interest] ?: 0) + 1
        }
        map
    }

    if (selectedEventForAttendees != null) {
        val event = selectedEventForAttendees!!
        val eventRegistrations = allRegistrations.filter { it.eventId == event.id }

        AttendeeManagementSheet(
            event = event,
            registrations = eventRegistrations,
            onClose = { selectedEventForAttendees = null },
            onToggleCheckIn = onToggleCheckIn
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Organizer Dashboard",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Centralized Bengaluru Events & Attendee Analytics",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4 Centralized KPI Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "TOTAL EVENTS",
                        value = "$totalEvents",
                        subtitle = "Active in Bengaluru",
                        icon = Icons.Default.Event,
                        tint = BlrCobalt,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "REGISTRATIONS",
                        value = "$totalRegistrations",
                        subtitle = "Total attendees booked",
                        icon = Icons.Default.Groups,
                        tint = BlrSaffron,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "EST. REVENUE",
                        value = if (totalRevenue > 0) "₹${totalRevenue.toInt()}" else "₹0",
                        subtitle = "Direct in-app bookings",
                        icon = Icons.Default.CurrencyRupee,
                        tint = BlrEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "CHECK-IN RATE",
                        value = "$checkInRate%",
                        subtitle = "$checkedInCount confirmed attended",
                        icon = Icons.Default.HowToReg,
                        tint = HackathonPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Attendee Demographics & Interests Analytics Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Attendee Analytics & Demographics",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "AGE DEMOGRAPHICS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ageGroupsMap.forEach { (age, count) ->
                            CategoryStatRow(
                                name = "$age Years",
                                count = count,
                                total = allRegistrations.size.coerceAtLeast(1),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "PRIMARY ATTENDEE INTERESTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlrEmerald,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        interestsMap.entries.take(4).forEach { (interest, count) ->
                            CategoryStatRow(
                                name = interest,
                                count = count,
                                total = allRegistrations.size.coerceAtLeast(1),
                                color = BlrEmerald
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            // Category Distribution Analytics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = BlrCobalt,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Event Category Breakdown",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CategoryStatRow(
                            name = "Marathons & Running",
                            count = marathonCount,
                            total = totalEvents,
                            color = MarathonOrange
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CategoryStatRow(
                            name = "Hackathons & Dev Sprints",
                            count = hackathonCount,
                            total = totalEvents,
                            color = HackathonPurple
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CategoryStatRow(
                            name = "Community Meetups & Activities",
                            count = communityCount,
                            total = totalEvents,
                            color = CommunityTeal
                        )
                    }
                }
            }

            // Hosted Events & Attendee Management Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage Hosted Events",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${events.size} Listed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Event Cards with Attendee Action & Edit
            items(events, key = { it.id }) { event ->
                val capacityRatio = if (event.totalCapacity > 0) {
                    (event.registeredCount.toFloat() / event.totalCapacity.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("organizer_event_card_${event.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (event.category) {
                                            "Marathon" -> MarathonOrange.copy(alpha = 0.15f)
                                            "Hackathon" -> HackathonPurple.copy(alpha = 0.15f)
                                            else -> CommunityTeal.copy(alpha = 0.15f)
                                        }
                                    ) {
                                        Text(
                                            text = event.category.uppercase(),
                                            color = when (event.category) {
                                                "Marathon" -> MarathonOrange
                                                "Hackathon" -> HackathonPurple
                                                else -> CommunityTeal
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    if (event.subCategory.isNotBlank() && event.subCategory != "All") {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = event.subCategory,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = event.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "📍 ${event.venueName} • ${event.neighborhood}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (event.price == 0.0) "FREE" else "₹${event.price.toInt()}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = if (event.price == 0.0) BlrEmerald else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = event.dateStr,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Capacity Metric
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Registered Attendees: ${event.registeredCount}/${event.totalCapacity}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${(capacityRatio * 100).toInt()}% Capacity",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { capacityRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions: Edit and Manage Attendees
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onEditEvent(event) },
                                modifier = Modifier
                                    .weight(0.4f)
                                    .height(42.dp)
                                    .testTag("edit_event_btn_${event.id}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Event",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { selectedEventForAttendees = event },
                                modifier = Modifier
                                    .weight(0.6f)
                                    .height(42.dp)
                                    .testTag("view_attendees_btn_${event.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Attendees (${event.registeredCount})",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Floating Action Button to Host / Advertise Event
        FloatingActionButton(
            onClick = onHostNewEvent,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("host_new_event_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Host Event")
                Text("Host Event", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    shape = CircleShape,
                    color = tint.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryStatRow(
    name: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) (count.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count (${(fraction * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendeeManagementSheet(
    event: Event,
    registrations: List<Registration>,
    onClose: () -> Unit,
    onToggleCheckIn: (registrationId: Long, currentStatus: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val checkedInCount = registrations.count { it.isCheckedIn }

    // Demographics for this event
    val ageDist = remember(registrations) {
        val m = mutableMapOf("18-24" to 0, "25-34" to 0, "35-44" to 0, "45+" to 0)
        registrations.forEach {
            val a = if (it.ageGroup.isNotBlank()) it.ageGroup else "25-34"
            m[a] = (m[a] ?: 0) + 1
        }
        m
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attendee Management & Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Check-in Status",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$checkedInCount of ${registrations.size} Checked In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlrEmerald
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BlrEmerald.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (registrations.isNotEmpty()) "${(checkedInCount * 100 / registrations.size)}%" else "0%",
                            color = BlrEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Demographics breakdown in attendee sheet
            if (registrations.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Attendee Age Demographics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ageDist.forEach { (age, count) ->
                                Text(
                                    text = "$age: $count",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (registrations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No attendees registered yet for this event.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(registrations, key = { it.id }) { reg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (reg.isCheckedIn) BlrEmerald.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (reg.isCheckedIn) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BlrEmerald)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reg.attendeeName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${reg.attendeeEmail} • ${reg.attendeePhone}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = reg.ticketType,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "•",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = reg.bookingRef,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onToggleCheckIn(reg.id, reg.isCheckedIn) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (reg.isCheckedIn) BlrEmerald else MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("check_in_toggle_${reg.id}")
                                ) {
                                    Icon(
                                        imageVector = if (reg.isCheckedIn) Icons.Default.CheckCircle else Icons.Default.HowToReg,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (reg.isCheckedIn) "Checked In" else "Check In",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HostEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (
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
        organizerContact: String
    ) -> Unit,
    onVerifyVenueWithMaps: (venue: String, neighborhood: String) -> Unit,
    venueVerificationText: String?,
    isVerifyingVenue: Boolean
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Marathon") }
    var subCategory by remember { mutableStateOf("Running") }
    var neighborhood by remember { mutableStateOf("Cubbon Park") }
    var venueName by remember { mutableStateOf("") }
    var venueAddress by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf("Nov 22, 2026") }
    var timeStr by remember { mutableStateOf("06:00 AM IST") }
    var priceStr by remember { mutableStateOf("499") }
    var capacityStr by remember { mutableStateOf("300") }
    var description by remember { mutableStateOf("") }
    var perks by remember { mutableStateOf("Finisher Medal, Dry-fit T-shirt, Refreshments, RFID Timing") }
    var rules by remember { mutableStateOf("Valid photo ID required at entry. Follow venue guidelines.") }
    var tags by remember { mutableStateOf("running, fitness, morning, bengaluru") }
    var organizerName by remember { mutableStateOf("Sanjana R") }
    var organizerContact by remember { mutableStateOf("sanjana.r@gmail.com | +91 98452 33445") }

    var error by remember { mutableStateOf<String?>(null) }

    val bengaluruNeighborhoods = listOf(
        "Cubbon Park",
        "Koramangala",
        "HSR Layout",
        "Indiranagar",
        "Whitefield",
        "Electronic City",
        "Malleshwaram",
        "Jayanagar",
        "Bellandur"
    )

    val subCategories = listOf("Running", "Coding", "Design", "Open Source", "Cycling", "Tabletop")

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Host & Advertise Event",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Post in Bengaluru • Direct Registrations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = "Event Category",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Marathon", "Hackathon", "Community Activity").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                subCategory = when (cat) {
                                    "Marathon" -> "Running"
                                    "Hackathon" -> "Coding"
                                    else -> "Open Source"
                                }
                            },
                            label = { Text(cat, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subcategory Selection
                Text(
                    text = "Subcategory / Primary Track",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subCategories.forEach { sub ->
                        FilterChip(
                            selected = subCategory == sub,
                            onClick = { subCategory = sub },
                            label = { Text(sub, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Event Title (e.g. Namma Bengaluru 10K)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("host_input_title"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bengaluru Neighborhood selector
                Text(
                    text = "Bengaluru Neighborhood",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bengaluruNeighborhoods.forEach { hood ->
                        val isSelected = neighborhood == hood
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { neighborhood = hood }
                        ) {
                            Text(
                                text = hood,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = venueName,
                    onValueChange = { venueName = it; error = null },
                    label = { Text("Venue Name (e.g. Kanteerava Stadium)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("host_input_venue"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Maps AI Location Verification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verify Location with Google Maps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = { onVerifyVenueWithMaps(venueName, neighborhood) },
                        enabled = !isVerifyingVenue && venueName.isNotBlank(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isVerifyingVenue) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify Maps", fontSize = 11.sp)
                    }
                }

                if (venueVerificationText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Google Maps Verification:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = venueVerificationText,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = venueAddress,
                    onValueChange = { venueAddress = it },
                    label = { Text("Detailed Bengaluru Address") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date (e.g. Nov 22, 2026)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = timeStr,
                        onValueChange = { timeStr = it },
                        label = { Text("Time (e.g. 06:00 AM IST)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price & Capacity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price ₹ (0 for Free)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = capacityStr,
                        onValueChange = { capacityStr = it },
                        label = { Text("Capacity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Event Description & Bengaluru Highlights") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = perks,
                    onValueChange = { perks = it },
                    label = { Text("Attendee Perks / Swag (Comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text("Event Rules & Entry Requirements") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Keywords & Search Tags (Comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = organizerName,
                    onValueChange = { organizerName = it },
                    label = { Text("Organizer Organization Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = organizerContact,
                    onValueChange = { organizerContact = it },
                    label = { Text("Organizer Helpline / Contact") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            error = "Please enter an event title"
                            return@Button
                        }
                        if (venueName.isBlank()) {
                            error = "Please enter venue name"
                            return@Button
                        }
                        val price = priceStr.toDoubleOrNull() ?: 0.0
                        val capacity = capacityStr.toIntOrNull() ?: 100

                        onSubmit(
                            title.trim(),
                            category,
                            subCategory,
                            neighborhood,
                            venueName.trim(),
                            venueAddress.ifBlank { "$venueName, $neighborhood, Bengaluru" },
                            dateStr.trim(),
                            timeStr.trim(),
                            price,
                            capacity,
                            description.trim(),
                            perks.trim(),
                            rules.trim(),
                            tags.trim(),
                            organizerName.trim(),
                            organizerContact.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("publish_event_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publish Event to Bengaluru", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
