package com.example.freekidslearn.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * ============================================================================
 * APPDATABASE.KT - Configuration de la base de données Room
 * ============================================================================
 *
 * Ce fichier définit la classe principale de la base de données Room.
 * C'est le point d'entrée pour accéder à toutes les tables de l'application.
 *
 * QU'EST-CE QUE ROOM DATABASE?
 * - Room est la bibliothèque de persistance recommandée par Google pour Android
 * - Elle fournit une couche d'abstraction au-dessus de SQLite
 * - Elle génère automatiquement le code boilerplate pour la base de données
 * - Elle vérifie les requêtes SQL à la compilation (pas d'erreurs à l'exécution)
 *
 * ANNOTATIONS:
 * - @Database: Déclare cette classe comme base de données Room
 *   - entities: Liste des tables (entités) de la base de données
 *   - version: Numéro de version (à incrémenter lors de modifications du schéma)
 *   - exportSchema: Exporter le schéma pour la migration (false = désactivé)
 *
 * PATTERN SINGLETON:
 * - On utilise le pattern Singleton pour garantir une seule instance de la DB
 * - Évite les conflits d'accès concurrent à la base de données
 * - @Volatile assure la visibilité de l'instance entre les threads
 * - synchronized garantit qu'un seul thread crée l'instance
 *
 * ARCHITECTURE:
 * ┌─────────────────────────────────────────────────────────────┐
 * │                    APPLICATION                              │
 * │    ┌──────────────────────────────────────────────────┐    │
 * │    │           AppDatabase (Singleton)                │    │
 * │    │    ┌────────────────────────────────────────┐    │    │
 * │    │    │     LetterProgressDao (Interface)     │    │    │
 * │    │    │  - getProgressByType()                │    │    │
 * │    │    │  - getProgressById()                  │    │    │
 * │    │    │  - insertProgress()                   │    │    │
 * │    │    │  - updateProgress()                   │    │    │
 * │    │    │  - deleteAllProgress()                │    │    │
 * │    │    └────────────────────────────────────────┘    │    │
 * │    └──────────────────────────────────────────────────┘    │
 * │                          ↓                                  │
 * │    ┌──────────────────────────────────────────────────┐    │
 * │    │              SQLite Database                     │    │
 * │    │     Table: letter_progress                       │    │
 * │    └──────────────────────────────────────────────────┘    │
 * └─────────────────────────────────────────────────────────────┘
 *
 * ============================================================================
 */
@Database(
    entities = [LetterProgress::class],  // Liste des tables de la base de données
    version = 1,                          // Version actuelle du schéma
    exportSchema = false                  // Pas d'export du schéma pour simplifier
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Fournit l'accès au DAO de progression des lettres
     *
     * Cette méthode abstraite est implémentée automatiquement par Room
     * Elle retourne l'instance du DAO pour effectuer des opérations CRUD
     *
     * UTILISATION:
     * val dao = AppDatabase.getDatabase(context).letterProgressDao()
     * dao.insertProgress(progress)
     */
    abstract fun letterProgressDao(): LetterProgressDao

    /**
     * Companion object contenant la logique de création du Singleton
     *
     * En Kotlin, un companion object est similaire aux membres statiques en Java
     * Il permet d'accéder à getDatabase() sans créer d'instance de AppDatabase
     */
    companion object {
        /**
         * Instance unique de la base de données
         *
         * @Volatile: Garantit que la valeur est toujours lue depuis la mémoire principale
         * et non depuis un cache de thread local. Nécessaire pour le pattern Singleton
         * dans un contexte multithread.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Récupère ou crée l'instance unique de la base de données
         *
         * @param context Le contexte de l'application (pas d'Activity pour éviter les fuites)
         * @return L'instance unique de AppDatabase
         *
         * THREAD-SAFETY:
         * - synchronized(this) garantit qu'un seul thread peut créer l'instance
         * - Les appels suivants retournent l'instance existante (rapide)
         *
         * EXEMPLE D'UTILISATION:
         * class MonActivity : AppCompatActivity() {
         *     private lateinit var db: AppDatabase
         *
         *     override fun onCreate(...) {
         *         db = AppDatabase.getDatabase(applicationContext)
         *         // Utiliser db.letterProgressDao() pour accéder aux données
         *     }
         * }
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si l'instance existe déjà, la retourner immédiatement
            return INSTANCE ?: synchronized(this) {
                // Double vérification: peut-être qu'un autre thread l'a créée entre-temps
                val instance = Room.databaseBuilder(
                    context.applicationContext,  // Utiliser applicationContext pour éviter les fuites mémoire
                    AppDatabase::class.java,     // La classe de la base de données
                    "kids_learning_database"     // Nom du fichier SQLite
                ).build()

                // Sauvegarder l'instance pour les prochains appels
                INSTANCE = instance
                instance
            }
        }
    }
}
