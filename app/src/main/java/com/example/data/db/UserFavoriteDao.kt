package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteDao {
    @Query("SELECT eventId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavorite)

    @Query("DELETE FROM favorites WHERE eventId = :eventId")
    suspend fun deleteFavorite(eventId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE eventId = :eventId)")
    suspend fun isFavorite(eventId: Long): Boolean
}
