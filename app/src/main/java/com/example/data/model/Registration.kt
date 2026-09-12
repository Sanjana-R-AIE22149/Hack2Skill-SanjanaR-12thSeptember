package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registrations")
data class Registration(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: Long,
    val eventTitle: String,
    val eventCategory: String,
    val eventDate: String,
    val eventTime: String,
    val eventVenue: String,
    val attendeeName: String,
    val attendeeEmail: String,
    val attendeePhone: String,
    val ticketType: String, // e.g. "21K Half Marathon", "10K Timed Run", "Full-Stack Hacker Solo", "General Entry"
    val ticketPrice: Double,
    val bookingRef: String, // e.g. "BLR-MR-8492"
    val registeredAt: Long = System.currentTimeMillis(),
    val isCheckedIn: Boolean = false,
    val attendeeDetail: String = "", // T-Shirt size or Team name
    val ageGroup: String = "25-34", // "18-24", "25-34", "35-44", "45+"
    val primaryInterest: String = "Athletics", // "Athletics", "AI & Machine Learning", "UI/UX Design", "Open Source", "Networking"
    val experienceLevel: String = "Intermediate" // "Beginner", "Intermediate", "Advanced"
)
