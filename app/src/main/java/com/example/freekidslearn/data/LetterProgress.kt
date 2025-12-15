package com.example.freekidslearn.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ============================================================================
 * LETTERPROGRESS.KT - Entité Room pour suivre la progression de l'utilisateur
 * ============================================================================
 *
 * Ce fichier définit une entité Room qui représente la progression d'un enfant
 * pour chaque lettre de l'alphabet. Room est la bibliothèque Android officielle
 * pour la persistance des données dans une base SQLite.
 *
 * QU'EST-CE QUE ROOM?
 * - Room est une couche d'abstraction au-dessus de SQLite
 * - Elle simplifie l'accès à la base de données
 * - Elle vérifie les requêtes SQL à la compilation
 * - Elle s'intègre avec LiveData pour observer les changements
 *
 * ANNOTATIONS ROOM:
 * - @Entity: Indique que cette classe représente une table dans la base de données
 * - @PrimaryKey: Identifie la colonne qui sert de clé primaire (unique)
 *
 * FONCTIONNEMENT:
 * - Chaque fois qu'un enfant complète le traçage d'une lettre, on enregistre sa progression
 * - On peut ainsi afficher des badges, des statistiques, etc.
 * - Les données persistent même après fermeture de l'application
 *
 * STRUCTURE DE LA TABLE:
 * ┌─────────────┬──────────────┬────────────────┬───────────────────┐
 * │ letterId    │ alphabetType │ timesCompleted │ lastCompletedDate │
 * │ (PK, INT)   │ (TEXT)       │ (INT)          │ (LONG)            │
 * ├─────────────┼──────────────┼────────────────┼───────────────────┤
 * │ 101         │ FRENCH       │ 5              │ 1702656000000     │
 * │ 1           │ ARABIC       │ 3              │ 1702655000000     │
 * └─────────────┴──────────────┴────────────────┴───────────────────┘
 *
 * ============================================================================
 */
@Entity(tableName = "letter_progress")
data class LetterProgress(
    /**
     * Clé primaire - Identifiant unique de la lettre
     * Correspond à Letter.id
     * Permet de lier la progression à une lettre spécifique
     */
    @PrimaryKey
    val letterId: Int,

    /**
     * Type d'alphabet (ARABIC ou FRENCH)
     * Stocké comme String dans SQLite
     * Permet de filtrer les progressions par alphabet
     */
    val alphabetType: String,

    /**
     * Nombre de fois que la lettre a été complétée/tracée
     * Incrémenté à chaque traçage réussi
     * Utilisé pour afficher des badges de progression
     * Valeur par défaut: 0
     */
    val timesCompleted: Int = 0,

    /**
     * Timestamp de la dernière complétion
     * Stocké en millisecondes depuis le 1er janvier 1970 (epoch)
     * Permet de trier par date ou afficher "Dernière pratique il y a X jours"
     * Valeur par défaut: 0 (jamais complété)
     */
    val lastCompletedDate: Long = 0L
)
