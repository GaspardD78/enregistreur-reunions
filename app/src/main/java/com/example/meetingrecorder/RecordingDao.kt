package com.example.meetingrecorder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recordings ORDER BY id DESC")
    fun getAll(): Flow<List<Recording>>

    @Insert
    suspend fun insert(recording: Recording)
}
