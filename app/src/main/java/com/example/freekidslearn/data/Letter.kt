package com.example.freekidslearn.data

import com.google.gson.annotations.SerializedName

/**
 * ============================================================================
 * LETTER.KT - Modèle de données pour une lettre de l'alphabet
 * ============================================================================
 *
 * Ce fichier définit la classe de données (data class) qui représente une lettre
 * dans notre application d'apprentissage pour enfants.
 *
 * FONCTIONNEMENT:
 * - Cette classe est utilisée pour stocker les informations de chaque lettre
 * - Les données sont chargées depuis les fichiers JSON (arabic_alphabet.json et french_alphabet.json)
 * - La bibliothèque Gson convertit automatiquement le JSON en objets Letter
 *
 * ANNOTATIONS:
 * - @SerializedName: Indique à Gson quel champ JSON correspond à quelle propriété
 *   Exemple: "letter" dans le JSON sera mappé vers la propriété 'letter' de cette classe
 *
 * UTILISATION:
 * - AlphabetLoader charge les fichiers JSON et crée des objets Letter
 * - LetterAdapter affiche ces lettres dans la liste (RecyclerView)
 * - LetterTracingActivity utilise ces données pour l'écran de traçage
 *
 * ============================================================================
 */
data class Letter(
    /**
     * Identifiant unique de la lettre
     * - Lettres arabes: 1 à 28
     * - Lettres françaises: 101 à 126
     * Permet de distinguer les lettres et suivre la progression
     */
    @SerializedName("id")
    val id: Int,

    /**
     * La lettre elle-même à afficher
     * Exemples: "A", "B", "C" pour le français
     *           "ا", "ب", "ت" pour l'arabe
     */
    @SerializedName("letter")
    val letter: String,

    /**
     * Le nom de la lettre (pour la prononciation)
     * Exemples: "A", "Bé", "Cé" pour le français
     *           "Alif", "Ba", "Ta" pour l'arabe
     */
    @SerializedName("name")
    val name: String,

    /**
     * Type d'alphabet: "ARABIC" ou "FRENCH"
     * Utilisé pour:
     * - Configurer la langue du TextToSpeech
     * - Filtrer les lettres par alphabet
     * - Appliquer le style approprié (direction RTL pour l'arabe)
     */
    @SerializedName("type")
    val type: String,

    /**
     * Animal associé à la lettre (avec emoji)
     * Exemples: "🐊 Alligator" pour A, "🐱 Chat" pour C
     * Rend l'apprentissage plus ludique et mémorable pour les enfants
     * Nullable car c'est un champ optionnel
     */
    @SerializedName("animal")
    val animal: String? = null,

    /**
     * Nom du fichier audio personnalisé (optionnel)
     * Si null, le TextToSpeech est utilisé pour prononcer la lettre
     * Permet d'ajouter des sons MP3 personnalisés dans le futur
     */
    @SerializedName("soundFile")
    val soundFile: String? = null
)
