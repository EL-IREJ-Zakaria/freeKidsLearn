package com.example.freekidslearn.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object for letter progress tracking
 */
@Dao
interface LetterProgressDao {

    @Query("SELECT * FROM letter_progress WHERE alphabetType = :type")
    fun getProgressByType(type: String): LiveData<List<LetterProgress>>

    @Query("SELECT * FROM letter_progress WHERE letterId = :id")
    suspend fun getProgressById(id: Int): LetterProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LetterProgress)

    @Update
    suspend fun updateProgress(progress: LetterProgress)

    @Query("DELETE FROM letter_progress")
    suspend fun deleteAllProgress()
}
