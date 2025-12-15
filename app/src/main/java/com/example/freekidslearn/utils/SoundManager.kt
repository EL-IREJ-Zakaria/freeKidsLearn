package com.example.freekidslearn.utils

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import java.util.*

/**
 * ============================================================================
 * SOUNDMANAGER.KT - Gestionnaire de sons pour l'application
 * ============================================================================
 *
 * Ce fichier gère toute la partie audio de l'application:
 * - Prononciation des lettres avec Text-to-Speech (TTS)
 * - Sons de succès et encouragement
 * - Gestion des ressources audio
 *
 * TECHNOLOGIES UTILISÉES:
 *
 * 1. TEXT-TO-SPEECH (TTS):
 *    - Convertit du texte en parole
 *    - Intégré à Android, pas besoin de connexion internet
 *    - Supporte plusieurs langues (français, arabe, etc.)
 *    - Prononce les lettres de manière naturelle
 *
 * 2. MEDIAPLAYER:
 *    - Joue des fichiers audio (MP3, WAV, etc.)
 *    - Utilisé pour les sons de succès
 *    - Peut jouer des sons système ou personnalisés
 *
 * ARCHITECTURE:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                      SoundManager                               │
 * │  ┌──────────────────────────┐  ┌──────────────────────────┐    │
 * │  │     TextToSpeech         │  │     MediaPlayer          │    │
 * │  │  - Prononce les lettres  │  │  - Sons de succès        │    │
 * │  │  - Multi-langue (fr/ar)  │  │  - Effets sonores        │    │
 * │  └──────────────────────────┘  └──────────────────────────┘    │
 * │                                                                 │
 * │  Méthodes publiques:                                            │
 * │  - playLetterSound(letter, isArabic)  -> TTS                   │
 * │  - playSuccessSound()                  -> MediaPlayer          │
 * │  - stopSounds()                        -> Arrêter tous les sons│
 * │  - release()                           -> Libérer les ressources│
 * └─────────────────────────────────────────────────────────────────┘
 *
 * CYCLE DE VIE:
 * 1. Création: SoundManager(context) -> initialise TTS
 * 2. Utilisation: playLetterSound() / playSuccessSound()
 * 3. Nettoyage: release() -> libère toutes les ressources
 *
 * IMPORTANT:
 * - Toujours appeler release() dans onDestroy() de l'Activity
 * - TTS nécessite que la langue soit installée sur l'appareil
 * - Sur émulateur, installer Google TTS depuis le Play Store
 *
 * ============================================================================
 */
class SoundManager(private val context: Context) {

    // =========================================================================
    // SECTION 1: DÉCLARATION DES VARIABLES
    // =========================================================================

    /**
     * MediaPlayer pour jouer les sons de succès
     * Nullable car il est créé à la demande et libéré après utilisation
     */
    private var mediaPlayer: MediaPlayer? = null

    /**
     * TextToSpeech pour la synthèse vocale des lettres
     * Nullable car l'initialisation peut échouer
     */
    private var textToSpeech: TextToSpeech? = null

    /**
     * Flag indiquant si le TTS est prêt à être utilisé
     * L'initialisation du TTS est asynchrone, donc on doit vérifier
     * qu'il est prêt avant de l'utiliser
     */
    private var isTtsInitialized = false

    // =========================================================================
    // SECTION 2: INITIALISATION
    // =========================================================================

    /**
     * Bloc d'initialisation - exécuté à la création de l'instance
     * Démarre l'initialisation du TextToSpeech
     */
    init {
        initTextToSpeech()
    }

    /**
     * Initialise le moteur Text-to-Speech
     *
     * FONCTIONNEMENT:
     * 1. Crée une instance de TextToSpeech
     * 2. L'initialisation est ASYNCHRONE (se fait en arrière-plan)
     * 3. Quand c'est prêt, le callback est appelé avec le statut
     * 4. On met à jour isTtsInitialized selon le résultat
     *
     * POURQUOI ASYNCHRONE?
     * - Le chargement du moteur TTS peut prendre du temps
     * - On ne veut pas bloquer l'interface utilisateur
     * - L'app reste réactive pendant le chargement
     */
    private fun initTextToSpeech() {
        // Créer le TTS avec un listener pour être notifié quand c'est prêt
        textToSpeech = TextToSpeech(context) { status ->
            // Ce callback est appelé quand l'initialisation est terminée
            if (status == TextToSpeech.SUCCESS) {
                // TTS initialisé avec succès, on peut l'utiliser
                isTtsInitialized = true
            }
            // Si status != SUCCESS, isTtsInitialized reste false
            // Les appels à playLetterSound() ne feront rien
        }
    }

    // =========================================================================
    // SECTION 3: LECTURE DES SONS
    // =========================================================================

    /**
     * Prononce une lettre avec le Text-to-Speech
     *
     * @param letter La lettre ou le nom à prononcer (ex: "A", "Alif", "ا")
     * @param isArabic true si c'est une lettre arabe, false si française
     *
     * FONCTIONNEMENT:
     * 1. Vérifier que le TTS est initialisé
     * 2. Configurer la langue appropriée (arabe ou français)
     * 3. Vérifier que la langue est disponible
     * 4. Prononcer la lettre
     *
     * PARAMÈTRES TTS.speak():
     * - QUEUE_FLUSH: Arrête la parole en cours et commence la nouvelle
     * - QUEUE_ADD: Ajoute à la file d'attente (non utilisé ici)
     *
     * EXEMPLE:
     * soundManager.playLetterSound("A", false)      // Prononce "A" en français
     * soundManager.playLetterSound("الف", true)    // Prononce "Alif" en arabe
     */
    fun playLetterSound(letter: String, isArabic: Boolean) {
        // Vérifier que le TTS est prêt à être utilisé
        if (isTtsInitialized) {
            textToSpeech?.let { tts ->
                // ─────────────────────────────────────────────────────────
                // Étape 1: Configurer la langue selon le type d'alphabet
                // ─────────────────────────────────────────────────────────
                val locale = if (isArabic) {
                    // Arabe: code de langue "ar"
                    Locale("ar")
                } else {
                    // Français: utilise la constante prédéfinie
                    Locale.FRENCH
                }

                // ─────────────────────────────────────────────────────────
                // Étape 2: Appliquer la langue au TTS
                // ─────────────────────────────────────────────────────────
                val result = tts.setLanguage(locale)

                // ─────────────────────────────────────────────────────────
                // Étape 3: Vérifier que la langue est disponible et parler
                // ─────────────────────────────────────────────────────────
                // LANG_MISSING_DATA: Les données de la langue manquent
                // LANG_NOT_SUPPORTED: La langue n'est pas supportée
                if (result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    // La langue est disponible, on peut parler!
                    // speak(texte, mode_file, bundle_params, id_unique)
                    tts.speak(letter, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                // Si la langue n'est pas disponible, rien ne se passe
                // L'utilisateur devrait installer les langues dans les paramètres
            }
        }
    }

    /**
     * Joue un son de succès pour encourager l'enfant
     *
     * FONCTIONNEMENT:
     * 1. Libérer le MediaPlayer précédent (éviter les fuites mémoire)
     * 2. Créer un nouveau MediaPlayer avec le son de notification système
     * 3. Démarrer la lecture
     *
     * POURQUOI LE SON SYSTÈME?
     * - Pas besoin d'ajouter des fichiers audio à l'app
     * - Son familier pour l'utilisateur
     * - Fonctionne sur tous les appareils
     *
     * AMÉLIORATION POSSIBLE:
     * - Ajouter des fichiers MP3 personnalisés dans res/raw/
     * - MediaPlayer.create(context, R.raw.success_sound)
     */
    fun playSuccessSound() {
        // Utiliser le son de notification système comme son de succès
        try {
            // Libérer le MediaPlayer précédent s'il existe
            mediaPlayer?.release()

            // Créer un nouveau MediaPlayer avec le son de notification par défaut
            mediaPlayer = MediaPlayer.create(
                context,
                android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            )

            // Démarrer la lecture du son
            mediaPlayer?.start()
        } catch (e: Exception) {
            // En cas d'erreur, afficher dans Logcat mais ne pas crasher
            e.printStackTrace()
        }
    }

    // =========================================================================
    // SECTION 4: CONTRÔLE ET NETTOYAGE
    // =========================================================================

    /**
     * Arrête tous les sons en cours de lecture
     *
     * UTILISÉ POUR:
     * - Quand l'utilisateur change de lettre rapidement
     * - Quand l'utilisateur quitte l'écran
     * - Pour éviter les chevauchements de sons
     */
    fun stopSounds() {
        // Arrêter la synthèse vocale en cours
        textToSpeech?.stop()
        // Arrêter le MediaPlayer
        mediaPlayer?.stop()
    }

    /**
     * Libère toutes les ressources audio
     *
     * TRÈS IMPORTANT: Toujours appeler cette méthode dans onDestroy()
     * de l'Activity pour éviter les fuites mémoire!
     *
     * EXEMPLE:
     * override fun onDestroy() {
     *     super.onDestroy()
     *     soundManager.release()
     * }
     *
     * ACTIONS:
     * 1. Libérer le MediaPlayer et le mettre à null
     * 2. Arrêter le TTS, le libérer (shutdown) et le mettre à null
     */
    fun release() {
        // ─────────────────────────────────────────────────────────────
        // Libérer le MediaPlayer
        // ─────────────────────────────────────────────────────────────
        mediaPlayer?.release()  // Libère les ressources natives
        mediaPlayer = null      // Permet au garbage collector de nettoyer

        // ─────────────────────────────────────────────────────────────
        // Libérer le TextToSpeech
        // ─────────────────────────────────────────────────────────────
        textToSpeech?.stop()     // Arrête toute parole en cours
        textToSpeech?.shutdown() // Libère le moteur TTS
        textToSpeech = null      // Permet au garbage collector de nettoyer
    }
}
