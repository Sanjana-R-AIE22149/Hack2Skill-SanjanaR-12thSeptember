package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Event
import com.example.data.model.Registration
import com.example.data.model.UserProfile
import com.example.ui.theme.BlrCobalt
import com.example.ui.theme.BlrEmerald
import com.example.ui.theme.BlrSaffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationBottomSheet(
    event: Event,
    userProfile: UserProfile? = null,
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        email: String,
        phone: String,
        ticketType: String,
        detail: String,
        ageGroup: String,
        primaryInterest: String,
        experienceLevel: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val ticketOptions = when (event.category) {
        "Marathon" -> listOf("21K Half Marathon (Timed)", "10K Challenge (Timed)", "5K Fun Run")
        "Hackathon" -> listOf("Solo Builder Pass", "Hacker Team Pass (2-4)", "Student Dev Pass")
        else -> listOf("General Entry Pass", "Community Contributor Pass")
    }

    var selectedTicketType by remember { mutableStateOf(ticketOptions.first()) }
    var name by remember { mutableStateOf(userProfile?.name?.ifBlank { "Sanjana R" } ?: "Sanjana R") }
    var email by remember { mutableStateOf(userProfile?.email?.ifBlank { "sanjana.r@gmail.com" } ?: "sanjana.r@gmail.com") }
    var phone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var detail by remember {
        mutableStateOf(
            if (event.category == "Marathon") "Size L" else ""
        )
    }

    // Demographics & interests
    var ageGroup by remember { mutableStateOf("25-34") }
    var primaryInterest by remember {
        mutableStateOf(
            when (event.category) {
                "Marathon" -> "Athletics"
                "Hackathon" -> "AI & Machine Learning"
                else -> "Networking"
            }
        )
    }
    var experienceLevel by remember { mutableStateOf("Intermediate") }

    var validationError by remember { mutableStateOf<String?>(null) }

    val ageOptions = listOf("18-24", "25-34", "35-44", "45+")
    val interestOptions = listOf("Athletics", "AI & Machine Learning", "UI/UX Design", "Open Source", "Networking")
    val expOptions = listOf("Beginner", "Intermediate", "Advanced")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Register for Event",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Instant Booking • Bengaluru Pass",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Event Summary Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${event.dateStr} • ${event.timeStr}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${event.venueName}, ${event.neighborhood}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ticket Tier Selection
            Text(
                text = "Select Registration Tier",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ticketOptions.forEach { option ->
                    val isSelected = selectedTicketType == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
                            )
                            .clickable { selectedTicketType = option }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attendee Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendee Information",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (userProfile != null) {
                    Text(
                        text = "Pre-filled from Profile",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; validationError = null },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_attendee_name"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; validationError = null },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_attendee_email"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; validationError = null },
                label = { Text("Mobile Phone (+91)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_attendee_phone"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic detail based on category
            when (event.category) {
                "Marathon" -> {
                    Text(
                        text = "Select T-Shirt Size",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Size S", "Size M", "Size L", "Size XL", "Size XXL").forEach { size ->
                            val isSelected = detail == size
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { detail = size }
                            ) {
                                Text(
                                    text = size.removePrefix("Size "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                "Hackathon" -> {
                    OutlinedTextField(
                        value = detail,
                        onValueChange = { detail = it },
                        label = { Text("Team Name or GitHub / LinkedIn Profile") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
                else -> {
                    OutlinedTextField(
                        value = detail,
                        onValueChange = { detail = it },
                        label = { Text("Special requirements or dietary preference") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Demographics & Analytics survey
            Text(
                text = "Attendee Demographics & Interests",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text("Age Group:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ageOptions.forEach { age ->
                    FilterChip(
                        selected = ageGroup == age,
                        onClick = { ageGroup = age },
                        label = { Text(age, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Primary Interest:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                interestOptions.forEach { interest ->
                    FilterChip(
                        selected = primaryInterest == interest,
                        onClick = { primaryInterest = interest },
                        label = { Text(interest, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Experience Level:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                expOptions.forEach { exp ->
                    FilterChip(
                        selected = experienceLevel == exp,
                        onClick = { experienceLevel = exp },
                        label = { Text(exp, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (validationError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError!!,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pricing & Confirm Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Amount",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (event.price == 0.0) "FREE" else "₹${event.price.toInt()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (event.price == 0.0) BlrEmerald else MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            validationError = "Please enter your full name"
                            return@Button
                        }
                        if (email.isBlank() || !email.contains("@")) {
                            validationError = "Please enter a valid email address"
                            return@Button
                        }
                        if (phone.isBlank() || phone.length < 8) {
                            validationError = "Please enter a valid phone number"
                            return@Button
                        }
                        onSubmit(
                            name.trim(),
                            email.trim(),
                            phone.trim(),
                            selectedTicketType,
                            detail.trim(),
                            ageGroup,
                            primaryInterest,
                            experienceLevel
                        )
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("confirm_registration_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (event.price == 0.0) "Complete Free Registration" else "Confirm & Book Ticket",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RegistrationSuccessDialog(
    registration: Registration,
    onDismiss: () -> Unit,
    onViewTickets: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = BlrEmerald.copy(alpha = 0.15f),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BlrEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Registration Confirmed!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your booking pass is now active for Bengaluru",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BOOKING REFERENCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = registration.bookingRef,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = registration.eventTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = "Pass Holder: ${registration.attendeeName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (registration.attendeeEmail.isNotBlank()) {
                            Text(
                                text = registration.attendeeEmail,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onViewTickets()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("view_ticket_pass_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "View Digital Ticket Pass",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
