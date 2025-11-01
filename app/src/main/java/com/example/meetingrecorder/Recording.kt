package com.example.meetingrecorder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val duration: String,
    val filePath: String
)
