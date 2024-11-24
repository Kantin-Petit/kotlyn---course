package com.example.criminalintent_2024.bd

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.criminalintent_2024.Crime
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao

interface CrimeDAO {
    @Query("SELECT * FROM crime")
    fun getIncidents(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getIncident(id: UUID): Crime

    @Update
    suspend fun majIncident(incident: Crime)

    @Insert
    suspend fun ajoutIncident(incident:Crime)

    @Delete
    suspend fun deleteIncident(incident: Crime)
}