package com.example.freekidslearn.data

/**
 * ============================================================================
 * ALPHABETTYPE.KT - Énumération des types d'alphabets supportés
 * ============================================================================
 *
 * Ce fichier définit une énumération (enum class) qui liste les différents
 * types d'alphabets disponibles dans l'application.
 *
 * QU'EST-CE QU'UNE ENUM?
 * - Une enum (énumération) est un type de données qui contient un ensemble
 *   fixe de constantes prédéfinies
 * - Elle garantit que seules des valeurs valides peuvent être utilisées
 * - Elle évite les erreurs de frappe (typos) avec des chaînes de caractères
 *
 * AVANTAGES:
 * - Type-safe: Le compilateur vérifie que seules les valeurs ARABIC ou FRENCH sont utilisées
 * - Auto-complétion: L'IDE suggère les valeurs disponibles
 * - Refactoring facile: Renommer une valeur met à jour tout le code
 *
 * UTILISATION DANS L'APPLICATION:
 * - MainActivity: Pour déterminer quel alphabet l'utilisateur a choisi
 * - AlphabetListActivity: Pour charger les bonnes lettres
 * - SoundManager: Pour configurer la bonne langue du TextToSpeech
 *
 * EXTENSIONS FUTURES:
 * - On pourrait facilement ajouter ENGLISH, SPANISH, etc.
 * - Chaque nouveau type nécessiterait un fichier JSON correspondant
 *
 * ============================================================================
 */
enum class AlphabetType {
    /**
     * Alphabet arabe - 28 lettres
     * Caractéristiques:
     * - Écriture de droite à gauche (RTL)
     * - Lettres connectées en écriture cursive
     * - TextToSpeech en langue arabe (ar)
     */
    ARABIC,

    /**
     * Alphabet français - 26 lettres
     * Caractéristiques:
     * - Écriture de gauche à droite (LTR)
     * - Lettres séparées
     * - TextToSpeech en langue française (fr)
     */
    FRENCH
}
