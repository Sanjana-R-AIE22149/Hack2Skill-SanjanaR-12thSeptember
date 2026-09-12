package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    event: Event,
    onDismiss: () -> Unit,
    onSave: (Event) -> Unit,
    onDelete: (Long) -> Unit,
    onVerifyVenueWithMaps: (venue: String, neighborhood: String) -> Unit,
    venueVerificationText: String?,
    isVerifyingVenue: Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(event.title) }
    var category by remember { mutableStateOf(event.category) }
    var subCategory by remember { mutableStateOf(event.subCategory) }
    var neighborhood by remember { mutableStateOf(event.neighborhood) }
    var venueName by remember { mutableStateOf(event.venueName) }
    var venueAddress by remember { mutableStateOf(event.venueAddress) }
    var dateStr by remember { mutableStateOf(event.dateStr) }
    var timeStr by remember { mutableStateOf(event.timeStr) }
    var priceStr by remember { mutableStateOf(if (event.price == 0.0) "0" else event.price.toString()) }
    var capacityStr by remember { mutableStateOf(event.totalCapacity.toString()) }
    var description by remember { mutableStateOf(event.description) }
    var perks by remember { mutableStateOf(event.perks) }
    var rules by remember { mutableStateOf(event.rules) }
    var tags by remember { mutableStateOf(event.tags) }
    var organizerName by remember { mutableStateOf(event.organizerName) }
    var organizerContact by remember { mutableStateOf(event.organizerContact) }

    val categories = listOf("Marathon", "Hackathon", "Community Activity")
    val subCategories = listOf("Running", "Coding", "Design", "Open Source", "Cycling", "Tabletop")
    val neighborhoods = listOf(
        "Cubbon Park", "Koramangala", "HSR Layout", "Indiranagar",
        "Whitefield", "Electronic City", "Malleshwaram", "Jayanagar"
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
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Edit Event Listing",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Category Selection
            Text(
                text = "Event Category",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Subcategory Selection
            Text(
                text = "Primary Track / Subcategory",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subCategories.take(3).forEach { sub ->
                    FilterChip(
                        selected = subCategory == sub,
                        onClick = { subCategory = sub },
                        label = { Text(sub, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subCategories.drop(3).forEach { sub ->
                    FilterChip(
                        selected = subCategory == sub,
                        onClick = { subCategory = sub },
                        label = { Text(sub, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_event_title_input"),
                singleLine = true
            )

            // Neighborhood Selection
            Text(
                text = "Bengaluru Neighborhood",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                neighborhoods.chunked(3).forEach { rowHoods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowHoods.forEach { hood ->
                            FilterChip(
                                selected = neighborhood == hood,
                                onClick = { neighborhood = hood },
                                label = { Text(hood, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Venue Name & Verification
            OutlinedTextField(
                value = venueName,
                onValueChange = { venueName = it },
                label = { Text("Venue Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Maps AI Grounding verification
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

            OutlinedTextField(
                value = venueAddress,
                onValueChange = { venueAddress = it },
                label = { Text("Full Address in Bengaluru") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 2
            )

            // Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Date (e.g. Oct 24, 2026)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = timeStr,
                    onValueChange = { timeStr = it },
                    label = { Text("Time (e.g. 06:00 AM IST)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Price & Capacity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price ₹ (0 for Free)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = capacityStr,
                    onValueChange = { capacityStr = it },
                    label = { Text("Max Attendee Capacity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Event Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Perks & Rules
            OutlinedTextField(
                value = perks,
                onValueChange = { perks = it },
                label = { Text("Perks & Swag (Comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rules,
                onValueChange = { rules = it },
                label = { Text("Rules & Guidelines") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Search Tags (Keywords)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Organizer Contacts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = organizerName,
                    onValueChange = { organizerName = it },
                    label = { Text("Organizer Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = organizerContact,
                    onValueChange = { organizerContact = it },
                    label = { Text("Contact Email/Phone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDelete(event.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    modifier = Modifier.weight(0.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val parsedPrice = priceStr.toDoubleOrNull() ?: 0.0
                        val parsedCap = capacityStr.toIntOrNull() ?: 100
                        val updated = event.copy(
                            title = title.ifBlank { "Untitled Bengaluru Event" },
                            category = category,
                            subCategory = subCategory,
                            neighborhood = neighborhood,
                            venueName = venueName.ifBlank { "Bengaluru Venue" },
                            venueAddress = venueAddress.ifBlank { "Bengaluru, Karnataka" },
                            dateStr = dateStr.ifBlank { "Oct 2026" },
                            timeStr = timeStr.ifBlank { "09:00 AM IST" },
                            price = parsedPrice,
                            totalCapacity = parsedCap,
                            description = description,
                            perks = perks,
                            rules = rules,
                            tags = tags,
                            organizerName = organizerName,
                            organizerContact = organizerContact
                        )
                        onSave(updated)
                    },
                    modifier = Modifier
                        .weight(0.6f)
                        .height(48.dp)
                        .testTag("save_edit_event_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
