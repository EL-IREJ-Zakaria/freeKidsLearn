package com.example.freekidslearn.utils

import android.content.Context
import com.example.freekidslearn.data.AlphabetType
import com.example.freekidslearn.data.Letter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * ============================================================================
 * ALPHABETLOADER.KT - Utilitaire pour charger les données d'alphabet depuis JSON
 * ============================================================================
 *
 * Ce fichier définit un objet singleton qui charge les données des alphabets
 * depuis les fichiers JSON stockés dans le dossier assets de l'application.
 *
 * QU'EST-CE QU'UN OBJECT SINGLETON EN KOTLIN?
 * - 'object' crée une classe avec une seule instance (singleton)
 * - Pas besoin de créer d'instance: on appelle directement AlphabetLoader.loadLetters()
 * - L'instance est créée paresseusement (lazy) au premier accès
 * - Thread-safe par défaut
 *
 * POURQUOI UTILISER JSON?
 * - Facile à lire et modifier
 * - Pas besoin de recompiler l'app pour changer les données
 * - Format standard compris par de nombreux outils
 * - Fonctionne hors-ligne (stocké dans l'APK)
 *
 * BIBLIOTHÈQUE GSON:
 * - Gson est une bibliothèque Google pour parser du JSON en Kotlin/Java
 * - Elle convertit automatiquement JSON -> Objets et Objets -> JSON
 * - TypeToken permet de gérer les types génériques (List<Letter>)
 *
 * FLUX DE DONNÉES:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                     ASSETS (dans l'APK)                         │
 * │   ┌─────────────────────┐   ┌─────────────────────┐            │
 * │   │ arabic_alphabet.json│   │ french_alphabet.json│            │
 * │   │ [{"id":1,"letter":" │   │ [{"id":101,"letter":│            │
 * │   │  ا","name":"Alif"}] │   │  "A","name":"A"}]   │            │
 * │   └──────────┬──────────┘   └──────────┬──────────┘            │
 * └──────────────┼─────────────────────────┼────────────────────────┘
 *                │                         │
 *                ▼                         ▼
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                    AlphabetLoader.loadLetters()                 │
 * │   1. Lire le fichier JSON depuis assets                         │
 * │   2. Parser le JSON avec Gson                                   │
 * │   3. Retourner List<Letter>                                     │
 * └──────────────────────────────┬──────────────────────────────────┘
 *                                │
 *                                ▼
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                    AlphabetListActivity                         │
 * │   - Affiche les lettres dans un RecyclerView                    │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * ============================================================================
 */
object AlphabetLoader {

    /**
     * Charge les lettres depuis un fichier JSON selon le type d'alphabet
     *
     * @param context Le contexte Android (nécessaire pour accéder aux assets)
     * @param type Le type d'alphabet à charger (ARABIC ou FRENCH)
     * @return Liste des lettres chargées, ou liste vide en cas d'erreur
     *
     * FONCTIONNEMENT:
     * 1. Déterminer le nom du fichier JSON selon le type d'alphabet
     * 2. Ouvrir le fichier depuis le dossier assets
     * 3. Lire tout le contenu du fichier en String
     * 4. Utiliser Gson pour convertir le JSON en List<Letter>
     * 5. Gérer les erreurs potentielles (fichier manquant, JSON invalide)
     *
     * EXEMPLE D'UTILISATION:
     * val letters = AlphabetLoader.loadLetters(context, AlphabetType.FRENCH)
     * // letters contient maintenant les 26 lettres françaises
     *
     * GESTION DES ERREURS:
     * - Si le fichier n'existe pas -> IOException -> retourne liste vide
     * - Si le JSON est invalide -> JsonSyntaxException -> crash (à améliorer)
     */
    fun loadLetters(context: Context, type: AlphabetType): List<Letter> {
        // =====================================================================
        // ÉTAPE 1: Déterminer le nom du fichier JSON
        // =====================================================================
        // Utilise une expression 'when' (équivalent du switch en Java)
        // pour choisir le bon fichier selon le type d'alphabet
        val fileName = when (type) {
            AlphabetType.ARABIC -> "arabic_alphabet.json"   // 28 lettres arabes
            AlphabetType.FRENCH -> "french_alphabet.json"   // 26 lettres françaises
        }

        // =====================================================================
        // ÉTAPE 2: Lire et parser le fichier JSON
        // =====================================================================
        return try {
            // -----------------------------------------------------------------
            // 2a. Ouvrir et lire le fichier JSON depuis assets
            // -----------------------------------------------------------------
            // context.assets donne accès au dossier assets de l'APK
            // open() ouvre un InputStream vers le fichier
            // bufferedReader() crée un lecteur bufferisé (plus efficace)
            // use { } ferme automatiquement le fichier après lecture
            // readText() lit tout le contenu en une seule String
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

            // -----------------------------------------------------------------
            // 2b. Convertir le JSON en List<Letter> avec Gson
            // -----------------------------------------------------------------
            // TypeToken est nécessaire car Java/Kotlin perdent l'information
            // sur les types génériques à l'exécution (type erasure)
            // Cette astuce permet de dire à Gson "je veux une List<Letter>"
            val listType = object : TypeToken<List<Letter>>() {}.type

            // Gson().fromJson() parse le JSON et crée les objets Letter
            // Les champs JSON doivent correspondre aux @SerializedName dans Letter.kt
            Gson().fromJson(jsonString, listType)

        } catch (e: IOException) {
            // -----------------------------------------------------------------
            // GESTION D'ERREUR: Fichier introuvable ou illisible
            // -----------------------------------------------------------------
            // IOException: erreur d'entrée/sortie (fichier manquant, etc.)
            // printStackTrace: affiche l'erreur dans Logcat pour débugger
            // Retourne une liste vide pour éviter de crasher l'app
            e.printStackTrace()
            emptyList()
        }
    }
}
