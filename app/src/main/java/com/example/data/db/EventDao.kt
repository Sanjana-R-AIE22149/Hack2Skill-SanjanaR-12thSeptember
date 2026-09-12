package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY id ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE category = :category ORDER BY id ASC")
    fun getEventsByCategory(category: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE neighborhood = :neighborhood ORDER BY id ASC")
    fun getEventsByNeighborhood(neighborhood: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Long): Flow<Event?>

    @Query("SELECT * FROM events WHERE isOrganizerCreated = 1 ORDER BY id DESC")
    fun getOrganizerCreatedEvents(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)

    @Update
    suspend fun updateEvent(event: Event)

    @Query("UPDATE events SET registeredCount = registeredCount + 1 WHERE id = :eventId")
    suspend fun incrementRegisteredCount(eventId: Long)

    @Query("UPDATE events SET registeredCount = MAX(0, registeredCount - 1) WHERE id = :eventId")
    suspend fun decrementRegisteredCount(eventId: Long)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: Long)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventsCount(): Int
}
