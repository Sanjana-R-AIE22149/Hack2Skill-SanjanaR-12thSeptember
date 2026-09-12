package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Marathon", "Hackathon", "Community Activity"
    val subCategory: String = "Running", // "Running", "Coding", "Design", "Open Source", "Cycling", "Tabletop"
    val neighborhood: String, // "Cubbon Park", "Koramangala", "HSR Layout", "Indiranagar", "Whitefield", "Electronic City", "Malleshwaram", "Jayanagar"
    val venueName: String,
    val venueAddress: String,
    val dateStr: String, // e.g. "Oct 18, 2026"
    val timeStr: String, // e.g. "05:30 AM IST"
    val eventTimestamp: Long = 0L, // Milliseconds for date sorting and range filtering
    val price: Double, // 0.0 = Free
    val totalCapacity: Int,
    val registeredCount: Int = 0,
    val description: String,
    val perks: String, // e.g. "Finisher Medal, Dry-fit T-Shirt, Timing RFID Bib"
    val rules: String,
    val tags: String = "", // keywords e.g. "running, 21k, fitness, tech, ai, design"
    val organizerName: String,
    val organizerContact: String,
    val isOrganizerCreated: Boolean = false,
    val bannerType: String = "marathon", // "marathon", "hackathon", "community"
    val latitude: Double = 12.9716,
    val longitude: Double = 77.5946
)
