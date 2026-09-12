package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Registration
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Query("SELECT * FROM registrations ORDER BY registeredAt DESC")
    fun getAllRegistrations(): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE eventId = :eventId ORDER BY registeredAt DESC")
    fun getRegistrationsForEvent(eventId: Long): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE id = :id")
    fun getRegistrationById(id: Long): Flow<Registration?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: Registration): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(registrations: List<Registration>)

    @Update
    suspend fun updateRegistration(registration: Registration)

    @Query("UPDATE registrations SET isCheckedIn = :isCheckedIn WHERE id = :id")
    suspend fun updateCheckInStatus(id: Long, isCheckedIn: Boolean)

    @Query("DELETE FROM registrations WHERE id = :id")
    suspend fun deleteRegistration(id: Long)

    @Query("SELECT COUNT(*) FROM registrations")
    fun getTotalRegistrationsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM registrations")
    suspend fun getRegistrationsCountSync(): Int

    @Query("UPDATE registrations SET attendeeName = :newName, attendeeEmail = :newEmail WHERE attendeeName != :newName OR attendeeEmail != :newEmail")
    suspend fun updateAllAttendeeNamesAndEmails(newName: String, newEmail: String)
}
