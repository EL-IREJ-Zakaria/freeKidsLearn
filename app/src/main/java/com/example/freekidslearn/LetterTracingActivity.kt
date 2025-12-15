package com.example.freekidslearn

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.freekidslearn.data.AlphabetType
import com.example.freekidslearn.data.AppDatabase
import com.example.freekidslearn.data.Letter
import com.example.freekidslearn.data.LetterProgress
import com.example.freekidslearn.ui.DrawingView
import com.example.freekidslearn.utils.AlphabetLoader
import com.example.freekidslearn.utils.SoundManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

/**
 * Activity for letter tracing with drawing canvas and sound
 */
class LetterTracingActivity : AppCompatActivity() {

    private lateinit var alphabetType: AlphabetType
    private lateinit var letters: List<Letter>
    private var currentLetterIndex = 0

    private lateinit var drawingView: DrawingView
    private lateinit var textLetterDisplay: TextView
    private lateinit var textLetterName: TextView
    private lateinit var soundManager: SoundManager
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_tracing)

        // Initialize
        val typeString = intent.getStringExtra(AlphabetListActivity.EXTRA_ALPHABET_TYPE)
            ?: AlphabetType.FRENCH.name
        alphabetType = AlphabetType.valueOf(typeString)

        letters = AlphabetLoader.loadLetters(this, alphabetType)

        val letterId = intent.getIntExtra(AlphabetListActivity.EXTRA_LETTER_ID, letters[0].id)
        currentLetterIndex = letters.indexOfFirst { it.id == letterId }.coerceAtLeast(0)

        soundManager = SoundManager(this)
        database = AppDatabase.getDatabase(this)

        initViews()
        setupToolbar()
        setupButtons()
        displayCurrentLetter()
    }

    private fun initViews() {
        drawingView = findViewById(R.id.drawingView)
        textLetterDisplay = findViewById(R.id.textLetterDisplay)
        textLetterName = findViewById(R.id.textLetterName)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupButtons() {
        val buttonClear = findViewById<MaterialButton>(R.id.buttonClear)
        val buttonNext = findViewById<MaterialButton>(R.id.buttonNext)
        val buttonPrevious = findViewById<MaterialButton>(R.id.buttonPrevious)
        val buttonRepeatSound = findViewById<MaterialButton>(R.id.buttonRepeatSound)

        buttonClear.setOnClickListener {
            drawingView.clearCanvas()
        }

        buttonNext.setOnClickListener {
            if (currentLetterIndex < letters.size - 1) {
                saveProgress()
                currentLetterIndex++
                displayCurrentLetter()
            } else {
                Toast.makeText(this, R.string.great_job, Toast.LENGTH_SHORT).show()
            }
        }

        buttonPrevious.setOnClickListener {
            if (currentLetterIndex > 0) {
                currentLetterIndex--
                displayCurrentLetter()
            }
        }

        buttonRepeatSound.setOnClickListener {
            playCurrentLetterSound()
        }
    }

    private fun displayCurrentLetter() {
        val letter = letters[currentLetterIndex]

        textLetterDisplay.text = letter.letter
        textLetterName.text = letter.name

        drawingView.clearCanvas()
        playCurrentLetterSound()
    }

    private fun playCurrentLetterSound() {
        val letter = letters[currentLetterIndex]
        val isArabic = alphabetType == AlphabetType.ARABIC
        soundManager.playLetterSound(letter.letter, isArabic)
    }

    private fun saveProgress() {
        val letter = letters[currentLetterIndex]

        lifecycleScope.launch {
            val dao = database.letterProgressDao()
            val existingProgress = dao.getProgressById(letter.id)

            if (existingProgress != null) {
                val updated = existingProgress.copy(
                    timesCompleted = existingProgress.timesCompleted + 1,
                    lastCompletedDate = System.currentTimeMillis()
                )
                dao.updateProgress(updated)
            } else {
                val newProgress = LetterProgress(
                    letterId = letter.id,
                    alphabetType = alphabetType.name,
                    timesCompleted = 1,
                    lastCompletedDate = System.currentTimeMillis()
                )
                dao.insertProgress(newProgress)
            }

            // Play success sound
            soundManager.playSuccessSound()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
