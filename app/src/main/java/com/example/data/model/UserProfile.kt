package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1,
    val name: String = "Sanjana R",
    val email: String = "sanjana.r@gmail.com",
    val phone: String = "+91 98452 33445",
    val bio: String = "Bengaluru marathoner & full-stack software engineer. Passionate about AI agents and weekend Cubbon Park runs.",
    val preferredNeighborhood: String = "Cubbon Park",
    val interests: String = "Running, Coding, Design",
    val selectedTheme: String = "INDIAN_TRADITIONAL"
)
