package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Registration
import com.example.ui.theme.BlrCobalt
import com.example.ui.theme.BlrEmerald
import com.example.ui.theme.BlrSaffron
import com.example.ui.theme.CommunityTeal
import com.example.ui.theme.HackathonPurple
import com.example.ui.theme.MarathonOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    registrations: List<Registration>,
    onCancelRegistration: (Registration) -> Unit,
    onExploreEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var ticketToCancel by remember { mutableStateOf<Registration?>(null) }

    if (ticketToCancel != null) {
        AlertDialog(
            onDismissRequest = { ticketToCancel = null },
            title = { Text("Cancel Registration?") },
            text = {
                Text(
                    "Are you sure you want to cancel your pass for ${ticketToCancel?.eventTitle}? Your spot will be released back to other attendees."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        ticketToCancel?.let { onCancelRegistration(it) }
                        ticketToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Cancel Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketToCancel = null }) {
                    Text("Keep Booking")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Bengaluru Passes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${registrations.size} active registrations",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (registrations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = BlrCobalt,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Bookings Yet",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Find thrilling marathons, 36-hour hackathons, and vibrant community meetups across Bengaluru!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreEvents,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlrCobalt)
                    ) {
                        Text("Discover Bengaluru Events", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(registrations, key = { it.id }) { registration ->
                    TicketPassCard(
                        registration = registration,
                        onCancelClick = { ticketToCancel = registration },
                        onDirectionsClick = {
                            val mapIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("geo:0,0?q=${Uri.encode(registration.eventVenue)}")
                            )
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://maps.google.com/?q=${Uri.encode(registration.eventVenue)}")
                                    )
                                )
                            }
                        },
                        onCalendarClick = {
                            val intent = Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, registration.eventTitle)
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, registration.eventVenue)
                                .putExtra(CalendarContract.Events.DESCRIPTION, "Bengaluru Pass: ${registration.bookingRef} (${registration.ticketType})")
                            context.startActivity(intent)
                        },
                        onShareClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "I have booked my ticket for ${registration.eventTitle} on ${registration.eventDate}! Booking ID: ${registration.bookingRef}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Booking Pass"))
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun TicketPassCard(
    registration: Registration,
    onCancelClick: () -> Unit,
    onDirectionsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (registration.eventCategory) {
        "Marathon" -> MarathonOrange
        "Hackathon" -> HackathonPurple
        else -> CommunityTeal
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ticket_card_${registration.bookingRef}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Header Color Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(categoryColor)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BENGALURU EVENT PASS • ${registration.eventCategory.uppercase()}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (registration.isCheckedIn) "CHECKED IN" else "CONFIRMED",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Ticket Body
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = registration.eventTitle,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BlrCobalt, modifier = Modifier.size(14.dp))
                            Text(text = registration.eventDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = BlrSaffron, modifier = Modifier.size(14.dp))
                            Text(text = registration.eventTime, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "TICKET TIER", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Text(text = registration.ticketType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                    Text(text = registration.eventVenue, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Perforated ticket visual divider
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f),
                        strokeWidth = 2f
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pass Holder & QR Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "PASS HOLDER", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Text(text = registration.attendeeName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        if (registration.attendeeEmail.isNotBlank()) {
                            Text(text = registration.attendeeEmail, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (registration.attendeeDetail.isNotBlank()) {
                            Text(text = registration.attendeeDetail, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "BOOKING ID: ${registration.bookingRef}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BlrCobalt)
                    }

                    // Stylized QR code representation
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "Digital Pass QR Code",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons on pass
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDirectionsClick,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Maps", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onCalendarClick,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Calendar", fontSize = 12.sp)
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onCancelClick,
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
