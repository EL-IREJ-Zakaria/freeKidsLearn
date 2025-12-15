package com.example.freekidslearn.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity to track user's progress for each letter
 */
@Entity(tableName = "letter_progress")
data class LetterProgress(
    @PrimaryKey
    val letterId: Int,
    val alphabetType: String,
    val timesCompleted: Int = 0,
    val lastCompletedDate: Long = 0L
)
