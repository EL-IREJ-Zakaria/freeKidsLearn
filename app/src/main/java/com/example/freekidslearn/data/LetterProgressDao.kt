package com.example.freekidslearn.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * ============================================================================
 * LETTERPROGRESSDAO.KT - Data Access Object pour accéder à la base de données
 * ============================================================================
 *
 * Ce fichier définit l'interface DAO (Data Access Object) qui fournit les méthodes
 * pour interagir avec la table letter_progress dans la base de données Room.
 *
 * QU'EST-CE QU'UN DAO?
 * - DAO = Data Access Object (Objet d'Accès aux Données)
 * - C'est une interface qui définit les opérations de base de données
 * - Room génère automatiquement l'implémentation à la compilation
 * - Elle isole la logique d'accès aux données du reste de l'application
 *
 * ANNOTATIONS ROOM:
 * - @Dao: Indique que cette interface est un DAO Room
 * - @Query: Définit une requête SQL personnalisée
 * - @Insert: Insère un nouvel enregistrement
 * - @Update: Met à jour un enregistrement existant
 * - @Delete: Supprime un enregistrement
 *
 * LIVEDATA:
 * - LiveData est un conteneur de données observable
 * - Quand les données changent en base, l'UI est automatiquement mise à jour
 * - Gère automatiquement le cycle de vie (pas de fuite mémoire)
 *
 * SUSPEND FUNCTIONS:
 * - Le mot-clé 'suspend' indique une fonction asynchrone (coroutine)
 * - Ces fonctions ne bloquent pas le thread principal (UI)
 * - Elles doivent être appelées depuis une coroutine ou une autre fonction suspend
 *
 * ============================================================================
 */
@Dao
interface LetterProgressDao {

    /**
     * Récupère toute la progression pour un type d'alphabet donné
     *
     * @param type Le type d'alphabet ("ARABIC" ou "FRENCH")
     * @return LiveData contenant la liste des progressions
     *
     * UTILISATION:
     * - Afficher la progression globale d'un alphabet
     * - Calculer le pourcentage de complétion
     * - Observer les changements en temps réel
     *
     * EXEMPLE:
     * dao.getProgressByType("FRENCH").observe(this) { progressList ->
     *     // Mettre à jour l'interface avec la nouvelle liste
     * }
     */
    @Query("SELECT * FROM letter_progress WHERE alphabetType = :type")
    fun getProgressByType(type: String): LiveData<List<LetterProgress>>

    /**
     * Récupère la progression d'une lettre spécifique par son ID
     *
     * @param id L'identifiant unique de la lettre
     * @return La progression ou null si la lettre n'a jamais été pratiquée
     *
     * UTILISATION:
     * - Vérifier si une lettre a déjà été complétée
     * - Afficher le nombre de pratiques pour une lettre
     *
     * NOTE: C'est une fonction suspend (asynchrone)
     */
    @Query("SELECT * FROM letter_progress WHERE letterId = :id")
    suspend fun getProgressById(id: Int): LetterProgress?

    /**
     * Insère ou remplace une progression dans la base de données
     *
     * @param progress L'objet LetterProgress à insérer
     *
     * STRATÉGIE DE CONFLIT:
     * - OnConflictStrategy.REPLACE: Si une entrée existe déjà avec le même ID,
     *   elle est remplacée par la nouvelle
     * - Cela simplifie la logique: pas besoin de vérifier si l'entrée existe
     *
     * UTILISATION:
     * - Enregistrer la première complétion d'une lettre
     * - Mettre à jour le compteur de complétion
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LetterProgress)

    /**
     * Met à jour une progression existante
     *
     * @param progress L'objet LetterProgress avec les nouvelles valeurs
     *
     * NOTE:
     * - L'objet doit avoir le même ID que celui à mettre à jour
     * - Plus explicite que REPLACE pour les mises à jour intentionnelles
     */
    @Update
    suspend fun updateProgress(progress: LetterProgress)

    /**
     * Supprime toute la progression (reset complet)
     *
     * UTILISATION:
     * - Bouton "Recommencer à zéro" dans les paramètres
     * - Utile pour les tests ou si un autre enfant utilise l'app
     *
     * ATTENTION: Cette action est irréversible!
     */
    @Query("DELETE FROM letter_progress")
    suspend fun deleteAllProgress()
}
