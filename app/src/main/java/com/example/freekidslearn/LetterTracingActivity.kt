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
 * ============================================================================
 * LETTERTRACINGACTIVITY.KT - Écran de traçage des lettres avec dessin et son
 * ============================================================================
 *
 * C'est l'écran principal d'apprentissage où l'enfant peut:
 * - Voir une lettre en grand format
 * - Écouter sa prononciation
 * - Tracer la lettre avec son doigt sur un canvas
 * - Naviguer entre les lettres (précédent/suivant)
 * - Effacer son dessin pour recommencer
 *
 * COMPOSANTS UTILISÉS:
 *
 * 1. DRAWINGVIEW (Custom View):
 *    - Zone de dessin où l'enfant trace avec son doigt
 *    - Voir DrawingView.kt pour les détails
 *
 * 2. TEXTTOSPECCH (via SoundManager):
 *    - Prononce les lettres à haute voix
 *    - Supporte le français et l'arabe
 *
 * 3. ROOM DATABASE (via AppDatabase):
 *    - Sauvegarde la progression de l'enfant
 *    - Compte combien de fois chaque lettre a été tracée
 *
 * LAYOUT DE L'ÉCRAN:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  ← Tracer la lettre                    (MaterialToolbar)        │
 * ├─────────────────────────────────────────────────────────────────┤
 * │                         A                                       │
 * │                        (A)                                      │
 * │                   [🔊 Répéter]                                  │
 * ├─────────────────────────────────────────────────────────────────┤
 * │  ┌───────────────────────────────────────────────────────────┐  │
 * │  │                                                           │  │
 * │  │              Zone de dessin (DrawingView)                 │  │
 * │  │                                                           │  │
 * │  │                    /‾‾‾\                                  │  │
 * │  │                   /  |  \                                 │  │
 * │  │                  /───────\                                │  │
 * │  │                 /    |    \                               │  │
 * │  │                                                           │  │
 * │  └───────────────────────────────────────────────────────────┘  │
 * ├─────────────────────────────────────────────────────────────────┤
 * │   [◄ Préc.]      [Effacer]      [Suiv. ►]                      │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * COROUTINES ET LIFECYCLESCOPE:
 * - Les opérations de base de données sont asynchrones
 * - lifecycleScope lance des coroutines liées au cycle de vie de l'Activity
 * - Quand l'Activity est détruite, les coroutines sont automatiquement annulées
 *
 * ============================================================================
 */
class LetterTracingActivity : AppCompatActivity() {

    // =========================================================================
    // SECTION 1: DÉCLARATION DES VARIABLES
    // =========================================================================

    /**
     * Type d'alphabet actuel (ARABIC ou FRENCH)
     * Détermine quelle langue utiliser pour la prononciation
     */
    private lateinit var alphabetType: AlphabetType

    /**
     * Liste complète des lettres de l'alphabet
     * Chargée depuis le fichier JSON au démarrage
     */
    private lateinit var letters: List<Letter>

    /**
     * Index de la lettre actuellement affichée dans la liste
     * Permet de naviguer avec Précédent/Suivant
     * Exemple: 0 = première lettre, 25 = dernière lettre (français)
     */
    private var currentLetterIndex = 0

    // ─────────────────────────────────────────────────────────────────────────
    // Références aux vues de l'interface
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Vue personnalisée pour dessiner avec le doigt
     * Voir DrawingView.kt pour les détails d'implémentation
     */
    private lateinit var drawingView: DrawingView

    /**
     * TextView affichant la lettre en grand (ex: "A" ou "ا")
     */
    private lateinit var textLetterDisplay: TextView

    /**
     * TextView affichant le nom de la lettre (ex: "A" ou "Alif")
     */
    private lateinit var textLetterName: TextView

    // ─────────────────────────────────────────────────────────────────────────
    // Gestionnaires de ressources
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gestionnaire de sons (TextToSpeech + effets sonores)
     * Voir SoundManager.kt pour les détails
     */
    private lateinit var soundManager: SoundManager

    /**
     * Instance de la base de données Room
     * Pour sauvegarder la progression de l'enfant
     */
    private lateinit var database: AppDatabase

    // =========================================================================
    // SECTION 2: CYCLE DE VIE - ONCREATE
    // =========================================================================

    /**
     * Méthode appelée à la création de l'Activity
     *
     * ÉTAPES D'INITIALISATION:
     * 1. Récupérer les données de l'Intent (type d'alphabet, ID de lettre)
     * 2. Charger les lettres depuis le JSON
     * 3. Trouver l'index de la lettre cliquée
     * 4. Initialiser les gestionnaires (sons, base de données)
     * 5. Configurer l'interface (vues, toolbar, boutons)
     * 6. Afficher la première lettre
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_tracing)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 1: Récupérer les données de l'Intent
        // ─────────────────────────────────────────────────────────────────────
        val typeString = intent.getStringExtra(AlphabetListActivity.EXTRA_ALPHABET_TYPE)
            ?: AlphabetType.FRENCH.name  // Par défaut: français
        alphabetType = AlphabetType.valueOf(typeString)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 2: Charger les lettres
        // ─────────────────────────────────────────────────────────────────────
        letters = AlphabetLoader.loadLetters(this, alphabetType)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 3: Trouver l'index de la lettre sélectionnée
        // ─────────────────────────────────────────────────────────────────────
        // Récupérer l'ID de la lettre cliquée (par défaut: première lettre)
        val letterId = intent.getIntExtra(AlphabetListActivity.EXTRA_LETTER_ID, letters[0].id)

        // Trouver l'index de cette lettre dans la liste
        // indexOfFirst retourne l'index du premier élément qui correspond
        // coerceAtLeast(0) garantit que l'index est au minimum 0 (pas négatif)
        currentLetterIndex = letters.indexOfFirst { it.id == letterId }.coerceAtLeast(0)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 4: Initialiser les gestionnaires
        // ─────────────────────────────────────────────────────────────────────
        soundManager = SoundManager(this)              // Pour les sons
        database = AppDatabase.getDatabase(this)       // Pour la progression

        // ─────────────────────────────────────────────────────────────────────
        // Étape 5: Configurer l'interface
        // ─────────────────────────────────────────────────────────────────────
        initViews()         // Récupérer les références aux vues
        setupToolbar()      // Configurer la barre d'outils
        setupButtons()      // Configurer les boutons

        // ─────────────────────────────────────────────────────────────────────
        // Étape 6: Afficher la lettre sélectionnée
        // ─────────────────────────────────────────────────────────────────────
        displayCurrentLetter()
    }

    // =========================================================================
    // SECTION 3: INITIALISATION DES VUES
    // =========================================================================

    /**
     * Récupère les références aux vues depuis le layout XML
     *
     * POURQUOI SÉPARER CETTE MÉTHODE?
     * - Organisation du code (chaque méthode a une responsabilité)
     * - Facilite la maintenance et la lecture
     * - Les références sont réutilisées dans d'autres méthodes
     */
    private fun initViews() {
        // Récupérer la zone de dessin
        drawingView = findViewById(R.id.drawingView)

        // Récupérer les TextViews pour afficher la lettre
        textLetterDisplay = findViewById(R.id.textLetterDisplay)
        textLetterName = findViewById(R.id.textLetterName)
    }

    /**
     * Configure la toolbar avec le titre et le bouton retour
     */
    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Au clic sur le bouton retour, fermer l'Activity
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    // =========================================================================
    // SECTION 4: CONFIGURATION DES BOUTONS
    // =========================================================================

    /**
     * Configure les actions des boutons de contrôle
     *
     * BOUTONS:
     * - Effacer: Efface le dessin pour recommencer
     * - Suivant: Passe à la lettre suivante (et sauvegarde la progression)
     * - Précédent: Revient à la lettre précédente
     * - Répéter: Rejoue le son de la lettre
     */
    private fun setupButtons() {
        // Récupérer les références aux boutons
        val buttonClear = findViewById<MaterialButton>(R.id.buttonClear)
        val buttonNext = findViewById<MaterialButton>(R.id.buttonNext)
        val buttonPrevious = findViewById<MaterialButton>(R.id.buttonPrevious)
        val buttonRepeatSound = findViewById<MaterialButton>(R.id.buttonRepeatSound)

        // ─────────────────────────────────────────────────────────────────────
        // Bouton EFFACER: Efface le dessin
        // ─────────────────────────────────────────────────────────────────────
        buttonClear.setOnClickListener {
            drawingView.clearCanvas()  // Appelle la méthode de DrawingView
        }

        // ─────────────────────────────────────────────────────────────────────
        // Bouton SUIVANT: Passe à la lettre suivante
        // ─────────────────────────────────────────────────────────────────────
        buttonNext.setOnClickListener {
            // Vérifier qu'on n'est pas à la dernière lettre
            if (currentLetterIndex < letters.size - 1) {
                // Sauvegarder la progression de la lettre actuelle
                saveProgress()

                // Passer à la lettre suivante
                currentLetterIndex++

                // Afficher la nouvelle lettre
                displayCurrentLetter()
            } else {
                // On est à la dernière lettre, afficher un message d'encouragement
                Toast.makeText(this, R.string.great_job, Toast.LENGTH_SHORT).show()
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // Bouton PRÉCÉDENT: Revient à la lettre précédente
        // ─────────────────────────────────────────────────────────────────────
        buttonPrevious.setOnClickListener {
            // Vérifier qu'on n'est pas à la première lettre
            if (currentLetterIndex > 0) {
                currentLetterIndex--
                displayCurrentLetter()
            }
            // Si on est à la première lettre, ne rien faire
        }

        // ─────────────────────────────────────────────────────────────────────
        // Bouton RÉPÉTER: Rejoue le son de la lettre
        // ─────────────────────────────────────────────────────────────────────
        buttonRepeatSound.setOnClickListener {
            playCurrentLetterSound()
        }
    }

    // =========================================================================
    // SECTION 5: AFFICHAGE ET SON
    // =========================================================================

    /**
     * Affiche la lettre actuelle (texte + son + reset du canvas)
     *
     * ACTIONS:
     * 1. Récupérer la lettre à l'index actuel
     * 2. Afficher la lettre en grand
     * 3. Afficher le nom de la lettre
     * 4. Effacer le canvas de dessin
     * 5. Jouer le son de la lettre
     */
    private fun displayCurrentLetter() {
        // Récupérer la lettre à l'index actuel
        val letter = letters[currentLetterIndex]

        // Afficher la lettre et son nom dans les TextViews
        textLetterDisplay.text = letter.letter  // Ex: "A" ou "ا"
        textLetterName.text = letter.name       // Ex: "A" ou "Alif"

        // Effacer le dessin précédent pour une nouvelle lettre
        drawingView.clearCanvas()

        // Jouer le son de la lettre
        playCurrentLetterSound()
    }

    /**
     * Prononce la lettre actuelle avec le TextToSpeech
     *
     * La langue est automatiquement adaptée selon le type d'alphabet:
     * - ARABIC: Utilise la langue arabe
     * - FRENCH: Utilise la langue française
     */
    private fun playCurrentLetterSound() {
        val letter = letters[currentLetterIndex]

        // Déterminer si c'est de l'arabe (pour choisir la bonne langue TTS)
        val isArabic = alphabetType == AlphabetType.ARABIC

        // Jouer le son via SoundManager
        soundManager.playLetterSound(letter.letter, isArabic)
    }

    // =========================================================================
    // SECTION 6: SAUVEGARDE DE LA PROGRESSION
    // =========================================================================

    /**
     * Sauvegarde la progression pour la lettre actuelle
     *
     * FONCTIONNEMENT:
     * 1. Lance une coroutine (opération asynchrone)
     * 2. Récupère la progression existante depuis la base de données
     * 3. Si elle existe: incrémente le compteur
     * 4. Sinon: crée une nouvelle entrée
     * 5. Joue un son de succès pour encourager l'enfant
     *
     * POURQUOI LIFECYCLESCOPE?
     * - Room nécessite des opérations asynchrones (pas sur le thread principal)
     * - lifecycleScope est lié au cycle de vie de l'Activity
     * - Si l'Activity est détruite, la coroutine est automatiquement annulée
     * - Évite les fuites mémoire et les crashes
     */
    private fun saveProgress() {
        val letter = letters[currentLetterIndex]

        // Lancer une coroutine pour les opérations de base de données
        lifecycleScope.launch {
            // Récupérer le DAO pour accéder à la base de données
            val dao = database.letterProgressDao()

            // Vérifier si une progression existe déjà pour cette lettre
            val existingProgress = dao.getProgressById(letter.id)

            if (existingProgress != null) {
                // ─────────────────────────────────────────────────────────────
                // La lettre a déjà été pratiquée: mettre à jour le compteur
                // ─────────────────────────────────────────────────────────────
                // copy() crée une copie avec les valeurs modifiées
                val updated = existingProgress.copy(
                    timesCompleted = existingProgress.timesCompleted + 1,  // +1 au compteur
                    lastCompletedDate = System.currentTimeMillis()         // Date actuelle
                )
                dao.updateProgress(updated)
            } else {
                // ─────────────────────────────────────────────────────────────
                // Première fois pour cette lettre: créer une nouvelle entrée
                // ─────────────────────────────────────────────────────────────
                val newProgress = LetterProgress(
                    letterId = letter.id,
                    alphabetType = alphabetType.name,
                    timesCompleted = 1,                           // Première complétion
                    lastCompletedDate = System.currentTimeMillis() // Date actuelle
                )
                dao.insertProgress(newProgress)
            }

            // ─────────────────────────────────────────────────────────────────
            // Jouer un son de succès pour encourager l'enfant
            // ─────────────────────────────────────────────────────────────────
            soundManager.playSuccessSound()
        }
    }

    // =========================================================================
    // SECTION 7: CYCLE DE VIE - ONDESTROY
    // =========================================================================

    /**
     * Méthode appelée quand l'Activity est détruite
     *
     * TRÈS IMPORTANT:
     * - Libérer les ressources audio pour éviter les fuites mémoire
     * - Le SoundManager contient un TextToSpeech et un MediaPlayer
     * - Ces objets doivent être explicitement libérés
     *
     * QUAND EST-ELLE APPELÉE?
     * - Quand l'utilisateur appuie sur le bouton retour
     * - Quand le système a besoin de mémoire
     * - Lors d'une rotation d'écran (l'Activity est recréée)
     */
    override fun onDestroy() {
        super.onDestroy()

        // Libérer les ressources audio
        // Voir SoundManager.release() pour les détails
        soundManager.release()
    }
}
