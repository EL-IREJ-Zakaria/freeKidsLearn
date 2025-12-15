package com.example.freekidslearn.utils

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import java.util.*

/**
 * Manages sound playback for letters
 */
class SoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
            }
        }
    }

    /**
     * Play letter sound using Text-to-Speech
     */
    fun playLetterSound(letter: String, isArabic: Boolean) {
        if (isTtsInitialized) {
            textToSpeech?.let { tts ->
                // Set language based on alphabet type
                val locale = if (isArabic) {
                    Locale("ar")
                } else {
                    Locale.FRENCH
                }

                val result = tts.setLanguage(locale)

                if (result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    tts.speak(letter, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    /**
     * Play success sound effect
     */
    fun playSuccessSound() {
        // Using system notification sound as success sound
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(
                context,
                android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            )
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stop all sounds
     */
    fun stopSounds() {
        textToSpeech?.stop()
        mediaPlayer?.stop()
    }

    /**
     * Release resources
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
