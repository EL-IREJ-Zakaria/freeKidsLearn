package com.example.freekidslearn

import android.os.Bundle
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
 * ============================================================================
 * LETTERTRACINGACTIVITY.KT - Écran de traçage des lettres
 * ============================================================================
 *
 * Écran simplifié où l'enfant peut:
 * - Tracer les lettres avec son doigt sur le canvas
 * - Naviguer entre les lettres (précédent/suivant)
 * - Effacer son dessin pour recommencer
 *
 * ============================================================================
 */
class LetterTracingActivity : AppCompatActivity() {

    // Variables pour la gestion des lettres
    private lateinit var alphabetType: AlphabetType
    private lateinit var letters: List<Letter>
    private var currentLetterIndex = 0

    // Références aux vues
    private lateinit var drawingView: DrawingView

    // Gestionnaires
    private lateinit var soundManager: SoundManager
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_tracing)

        // Récupérer le type d'alphabet depuis l'Intent
        val typeString = intent.getStringExtra(AlphabetListActivity.EXTRA_ALPHABET_TYPE)
            ?: AlphabetType.FRENCH.name
        alphabetType = AlphabetType.valueOf(typeString)

        // Charger les lettres
        letters = AlphabetLoader.loadLetters(this, alphabetType)

        // Trouver l'index de la lettre sélectionnée
        val letterId = intent.getIntExtra(AlphabetListActivity.EXTRA_LETTER_ID, letters[0].id)
        currentLetterIndex = letters.indexOfFirst { it.id == letterId }.coerceAtLeast(0)

        // Initialiser les gestionnaires
        soundManager = SoundManager(this)
        database = AppDatabase.getDatabase(this)

        // Configurer l'interface
        initViews()
        setupToolbar()
        setupButtons()

        // Afficher la lettre actuelle (effacer le canvas et jouer le son)
        displayCurrentLetter()
    }

    /**
     * Initialise les références aux vues
     */
    private fun initViews() {
        drawingView = findViewById(R.id.drawingView)
    }

    /**
     * Configure la toolbar avec le bouton retour
     */
    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Mettre à jour le titre avec la lettre actuelle
        updateToolbarTitle()

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Met à jour le titre de la toolbar avec la lettre actuelle
     */
    private fun updateToolbarTitle() {
        val letter = letters[currentLetterIndex]
        supportActionBar?.title = "Tracer: ${letter.letter} - ${letter.name}"
    }

    /**
     * Configure les boutons de contrôle
     */
    private fun setupButtons() {
        val buttonClear = findViewById<MaterialButton>(R.id.buttonClear)
        val buttonNext = findViewById<MaterialButton>(R.id.buttonNext)
        val buttonPrevious = findViewById<MaterialButton>(R.id.buttonPrevious)

        // Bouton Effacer
        buttonClear.setOnClickListener {
            drawingView.clearCanvas()
        }

        // Bouton Suivant
        buttonNext.setOnClickListener {
            if (currentLetterIndex < letters.size - 1) {
                saveProgress()
                currentLetterIndex++
                displayCurrentLetter()
            } else {
                Toast.makeText(this, R.string.great_job, Toast.LENGTH_SHORT).show()
            }
        }

        // Bouton Précédent
        buttonPrevious.setOnClickListener {
            if (currentLetterIndex > 0) {
                currentLetterIndex--
                displayCurrentLetter()
            }
        }
    }

    /**
     * Affiche la lettre actuelle
     */
    private fun displayCurrentLetter() {
        val letter = letters[currentLetterIndex]

        // Mettre à jour le titre
        updateToolbarTitle()

        // Afficher la lettre en arrière-plan du canvas (guide pour tracer)
        drawingView.setBackgroundLetter(letter.letter)

        // Effacer le dessin précédent
        drawingView.clearCanvas()

        // Jouer le son de la lettre
        playCurrentLetterSound()
    }

    /**
     * Joue le son de la lettre actuelle
     */
    private fun playCurrentLetterSound() {
        val letter = letters[currentLetterIndex]
        val isArabic = alphabetType == AlphabetType.ARABIC
        soundManager.playLetterSound(letter.letter, isArabic)
    }

    /**
     * Sauvegarde la progression de l'enfant
     */
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

            // Jouer un son de succès
            soundManager.playSuccessSound()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
