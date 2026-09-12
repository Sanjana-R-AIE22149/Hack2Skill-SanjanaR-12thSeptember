package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class UserFavorite(
    @PrimaryKey
    val eventId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
