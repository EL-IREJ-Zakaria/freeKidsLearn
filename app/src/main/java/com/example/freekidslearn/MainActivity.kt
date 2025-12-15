package com.example.freekidslearn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.freekidslearn.data.AlphabetType
import com.google.android.material.card.MaterialCardView

/**
 * ============================================================================
 * MAINACTIVITY.KT - Écran d'accueil avec sélection de la langue
 * ============================================================================
 *
 * Cette Activity est le point d'entrée de l'application. Elle affiche l'écran
 * d'accueil où l'enfant peut choisir entre l'alphabet arabe et l'alphabet français.
 *
 * QU'EST-CE QU'UNE ACTIVITY?
 * - Une Activity représente un écran dans une application Android
 * - Elle a un cycle de vie (création, pause, reprise, destruction)
 * - Elle est associée à un layout XML qui définit son interface
 * - AppCompatActivity est la classe de base recommandée (support des anciens Android)
 *
 * CYCLE DE VIE DE L'ACTIVITY:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                    CYCLE DE VIE                                 │
 * │                                                                 │
 * │   onCreate() ──► onStart() ──► onResume() ──► [RUNNING]        │
 * │        │                              │                         │
 * │        │                              ▼                         │
 * │        │                         onPause() ──► onStop()        │
 * │        │                              │            │            │
 * │        │                              │            ▼            │
 * │        │                              │       onDestroy()       │
 * │        │                              │            │            │
 * │        └──────────────────────────────┴────────────┘            │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * NAVIGATION DE L'APPLICATION:
 * ┌─────────────────┐     ┌─────────────────────┐     ┌──────────────────┐
 * │  MainActivity   │────►│ AlphabetListActivity│────►│LetterTracingActi-│
 * │  (Choix langue) │     │ (Liste des lettres) │     │     vity (Traçage)│
 * └─────────────────┘     └─────────────────────┘     └──────────────────┘
 *
 * LAYOUT ASSOCIÉ: activity_main.xml
 * - Contient deux MaterialCardView pour le choix de l'alphabet
 * - Design adapté aux enfants (grandes cartes colorées)
 *
 * ============================================================================
 */
class MainActivity : AppCompatActivity() {

    // =========================================================================
    // SECTION 1: CYCLE DE VIE - ONCREATE
    // =========================================================================

    /**
     * Méthode appelée à la création de l'Activity
     *
     * @param savedInstanceState État sauvegardé (null si première création)
     *
     * C'EST ICI QUE:
     * 1. On appelle super.onCreate() (obligatoire)
     * 2. On définit le layout avec setContentView()
     * 3. On initialise les composants de l'interface
     * 4. On configure les listeners (actions des boutons)
     *
     * QUAND EST-ELLE APPELÉE?
     * - Au lancement de l'app
     * - Après une rotation d'écran (l'Activity est recréée)
     * - Quand on revient sur l'app après qu'elle ait été détruite par le système
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Appeler la méthode parent (OBLIGATOIRE)
        super.onCreate(savedInstanceState)

        // Définir le layout XML à utiliser pour cette Activity
        // R.layout.activity_main référence le fichier res/layout/activity_main.xml
        setContentView(R.layout.activity_main)

        // Configurer les boutons de sélection de langue
        setupLanguageSelection()
    }

    // =========================================================================
    // SECTION 2: CONFIGURATION DE L'INTERFACE
    // =========================================================================

    /**
     * Configure les cartes de sélection de langue
     *
     * FONCTIONNEMENT:
     * 1. Récupère les références aux vues MaterialCardView depuis le layout XML
     * 2. Configure un listener de clic sur chaque carte
     * 3. Au clic, navigue vers l'écran de liste avec le type d'alphabet choisi
     *
     * MATERIALCARDVIEW:
     * - Composant Material Design pour afficher du contenu dans une carte
     * - Fournit automatiquement une élévation et des coins arrondis
     * - Peut être cliquable avec un effet de ripple (ondulation)
     */
    private fun setupLanguageSelection() {
        // Récupérer les références aux cartes depuis le layout XML
        // findViewById<Type> cherche une vue par son ID et la convertit au bon type
        val cardArabic = findViewById<MaterialCardView>(R.id.cardArabic)
        val cardFrench = findViewById<MaterialCardView>(R.id.cardFrench)

        // Configurer le clic sur la carte "Alphabet Arabe"
        cardArabic.setOnClickListener {
            // Naviguer vers la liste des lettres arabes
            navigateToAlphabetList(AlphabetType.ARABIC)
        }

        // Configurer le clic sur la carte "Alphabet Français"
        cardFrench.setOnClickListener {
            // Naviguer vers la liste des lettres françaises
            navigateToAlphabetList(AlphabetType.FRENCH)
        }
    }

    // =========================================================================
    // SECTION 3: NAVIGATION
    // =========================================================================

    /**
     * Navigue vers l'écran de liste des lettres
     *
     * @param type Le type d'alphabet sélectionné (ARABIC ou FRENCH)
     *
     * QU'EST-CE QU'UN INTENT?
     * - Un Intent est un message pour démarrer une autre Activity
     * - Il peut contenir des données supplémentaires (extras)
     * - C'est le mécanisme principal de navigation entre écrans
     *
     * FONCTIONNEMENT:
     * 1. Créer un Intent vers AlphabetListActivity
     * 2. Ajouter le type d'alphabet comme "extra" (donnée supplémentaire)
     * 3. Démarrer l'Activity avec startActivity()
     *
     * EXTRAS:
     * - Les extras sont des paires clé-valeur passées entre Activities
     * - La clé est une String constante (EXTRA_ALPHABET_TYPE)
     * - La valeur peut être de différents types (String, Int, etc.)
     * - L'Activity destination récupère la valeur avec intent.getStringExtra(clé)
     */
    private fun navigateToAlphabetList(type: AlphabetType) {
        // Créer un Intent explicite vers AlphabetListActivity
        // this = contexte actuel, AlphabetListActivity::class.java = classe cible
        val intent = Intent(this, AlphabetListActivity::class.java)

        // Ajouter le type d'alphabet comme extra
        // type.name convertit l'enum en String ("ARABIC" ou "FRENCH")
        intent.putExtra(EXTRA_ALPHABET_TYPE, type.name)

        // Démarrer l'Activity (elle apparaît à l'écran)
        startActivity(intent)
    }

    // =========================================================================
    // SECTION 4: CONSTANTES
    // =========================================================================

    /**
     * Companion object - contient les constantes partagées
     *
     * QU'EST-CE QU'UN COMPANION OBJECT?
     * - Équivalent Kotlin des membres statiques en Java
     * - Permet d'accéder aux constantes sans créer d'instance
     * - Exemple: MainActivity.EXTRA_ALPHABET_TYPE
     *
     * CONVENTION DE NOMMAGE:
     * - Les extras commencent par EXTRA_
     * - Les noms sont en MAJUSCULES_AVEC_UNDERSCORES
     * - Cela aide à identifier rapidement les clés d'Intent
     */
    companion object {
        /**
         * Clé pour passer le type d'alphabet entre Activities
         * Utilisée comme clé dans intent.putExtra() et intent.getStringExtra()
         */
        const val EXTRA_ALPHABET_TYPE = "alphabet_type"
    }
}
