package com.example.freekidslearn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freekidslearn.data.AlphabetType
import com.example.freekidslearn.data.Letter
import com.example.freekidslearn.ui.LetterAdapter
import com.example.freekidslearn.utils.AlphabetLoader
import com.google.android.material.appbar.MaterialToolbar

/**
 * ============================================================================
 * ALPHABETLISTACTIVITY.KT - Écran affichant la liste des lettres
 * ============================================================================
 *
 * Cette Activity affiche toutes les lettres de l'alphabet choisi (arabe ou français)
 * dans une grille défilante. L'enfant peut cliquer sur une lettre pour accéder
 * à l'écran de traçage.
 *
 * COMPOSANTS UTILISÉS:
 *
 * 1. RECYCLERVIEW:
 *    - Composant Android pour afficher des listes défilantes
 *    - Optimisé pour les performances (recycle les vues)
 *    - Supporte différents layouts (liste, grille, etc.)
 *
 * 2. GRIDLAYOUTMANAGER:
 *    - Organise les éléments en grille (lignes et colonnes)
 *    - On utilise 2 colonnes pour afficher les lettres côte à côte
 *
 * 3. MATERIALTOOLBAR:
 *    - Barre d'outils Material Design en haut de l'écran
 *    - Affiche le titre et le bouton retour
 *
 * ARCHITECTURE:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                 AlphabetListActivity                            │
 * │  ┌───────────────────────────────────────────────────────────┐  │
 * │  │              MaterialToolbar (titre + retour)             │  │
 * │  └───────────────────────────────────────────────────────────┘  │
 * │  ┌───────────────────────────────────────────────────────────┐  │
 * │  │                    RecyclerView                           │  │
 * │  │  ┌─────────────┐  ┌─────────────┐                         │  │
 * │  │  │ A - Alligator│  │ B - Baleine │                         │  │
 * │  │  └─────────────┘  └─────────────┘                         │  │
 * │  │  ┌─────────────┐  ┌─────────────┐                         │  │
 * │  │  │ C - Chat    │  │ D - Dauphin │                         │  │
 * │  │  └─────────────┘  └─────────────┘                         │  │
 * │  │         ... (26 ou 28 lettres)                            │  │
 * │  └───────────────────────────────────────────────────────────┘  │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * FLUX DE DONNÉES:
 * 1. Récupérer le type d'alphabet depuis l'Intent (ARABIC ou FRENCH)
 * 2. Charger les lettres depuis le fichier JSON correspondant
 * 3. Afficher les lettres dans le RecyclerView via l'Adapter
 * 4. Au clic sur une lettre, naviguer vers LetterTracingActivity
 *
 * ============================================================================
 */
class AlphabetListActivity : AppCompatActivity() {

    // =========================================================================
    // SECTION 1: DÉCLARATION DES VARIABLES
    // =========================================================================

    /**
     * Type d'alphabet actuel (ARABIC ou FRENCH)
     * Récupéré depuis l'Intent au démarrage
     * 'lateinit' = initialisé plus tard (dans onCreate), pas null
     */
    private lateinit var alphabetType: AlphabetType

    /**
     * Liste des lettres à afficher
     * Chargée depuis le fichier JSON correspondant au type d'alphabet
     */
    private lateinit var letters: List<Letter>

    // =========================================================================
    // SECTION 2: CYCLE DE VIE - ONCREATE
    // =========================================================================

    /**
     * Méthode appelée à la création de l'Activity
     *
     * ÉTAPES:
     * 1. Appeler super.onCreate() et définir le layout
     * 2. Récupérer le type d'alphabet depuis l'Intent
     * 3. Configurer la toolbar (titre + bouton retour)
     * 4. Configurer le RecyclerView avec les lettres
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alphabet_list)

        // ─────────────────────────────────────────────────────────────────────
        // Récupérer le type d'alphabet passé par MainActivity
        // ─────────────────────────────────────────────────────────────────────
        // getStringExtra() récupère la valeur String associée à la clé
        // ?: AlphabetType.FRENCH.name = valeur par défaut si null (opérateur Elvis)
        val typeString =
            intent.getStringExtra(MainActivity.EXTRA_ALPHABET_TYPE) ?: AlphabetType.FRENCH.name

        // Convertir la String en enum AlphabetType
        // valueOf() convertit "ARABIC" -> AlphabetType.ARABIC
        alphabetType = AlphabetType.valueOf(typeString)

        // Configurer l'interface
        setupToolbar()
        setupRecyclerView()
    }

    // =========================================================================
    // SECTION 3: CONFIGURATION DE LA TOOLBAR
    // =========================================================================

    /**
     * Configure la barre d'outils (toolbar) en haut de l'écran
     *
     * FONCTIONNALITÉS:
     * - Affiche le titre dynamique selon l'alphabet choisi
     * - Ajoute un bouton retour (flèche) pour revenir à MainActivity
     * - Gère le clic sur le bouton retour
     *
     * setSupportActionBar():
     * - Définit la toolbar comme ActionBar officielle de l'Activity
     * - Permet d'utiliser supportActionBar pour modifier le titre, etc.
     *
     * setDisplayHomeAsUpEnabled(true):
     * - Active le bouton "Up" (flèche retour) dans la toolbar
     * - Ce bouton est différent du bouton "Back" du système
     */
    private fun setupToolbar() {
        // Récupérer la référence à la toolbar depuis le layout
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        // Définir cette toolbar comme l'ActionBar de l'Activity
        setSupportActionBar(toolbar)

        // Activer le bouton retour (icône de flèche)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Définir le titre selon le type d'alphabet
        // when = équivalent du switch en Java
        val title = when (alphabetType) {
            AlphabetType.ARABIC -> getString(R.string.arabic_alphabet)  // "Alphabet Arabe"
            AlphabetType.FRENCH -> getString(R.string.french_alphabet)  // "Alphabet Français"
        }
        supportActionBar?.title = title

        // Configurer l'action du bouton retour
        // Quand on clique sur la flèche, on ferme cette Activity
        toolbar.setNavigationOnClickListener {
            finish()  // Ferme l'Activity et revient à la précédente (MainActivity)
        }
    }

    // =========================================================================
    // SECTION 4: CONFIGURATION DU RECYCLERVIEW
    // =========================================================================

    /**
     * Configure le RecyclerView pour afficher la grille de lettres
     *
     * COMPOSANTS NÉCESSAIRES POUR UN RECYCLERVIEW:
     * 1. LayoutManager: Comment organiser les éléments (liste, grille, etc.)
     * 2. Adapter: Le pont entre les données et l'affichage
     * 3. Layout XML pour chaque élément (item_letter.xml)
     *
     * GRIDLAYOUTMANAGER:
     * - Organise les éléments en grille
     * - Le paramètre 2 = nombre de colonnes
     * - Les lignes sont créées automatiquement selon le nombre d'éléments
     */
    private fun setupRecyclerView() {
        // ─────────────────────────────────────────────────────────────────────
        // Étape 1: Charger les lettres depuis le fichier JSON
        // ─────────────────────────────────────────────────────────────────────
        // AlphabetLoader lit le fichier arabic_alphabet.json ou french_alphabet.json
        // et retourne une List<Letter> avec toutes les lettres
        letters = AlphabetLoader.loadLetters(this, alphabetType)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 2: Configurer le RecyclerView
        // ─────────────────────────────────────────────────────────────────────
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLetters)

        // Utiliser une grille de 2 colonnes
        // Les lettres s'afficheront: [A][B] puis [C][D] puis [E][F]...
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // ─────────────────────────────────────────────────────────────────────
        // Étape 3: Créer et attacher l'Adapter
        // ─────────────────────────────────────────────────────────────────────
        // LetterAdapter prend:
        // - letters: la liste des lettres à afficher
        // - Un lambda (callback) appelé quand on clique sur une lettre
        val adapter = LetterAdapter(letters) { letter ->
            // Cette fonction est appelée quand l'enfant clique sur une lettre
            navigateToLetterTracing(letter)
        }

        // Attacher l'adapter au RecyclerView
        // Le RecyclerView va maintenant afficher les lettres
        recyclerView.adapter = adapter
    }

    // =========================================================================
    // SECTION 5: NAVIGATION VERS L'ÉCRAN DE TRAÇAGE
    // =========================================================================

    /**
     * Navigue vers l'écran de traçage pour une lettre spécifique
     *
     * @param letter La lettre sur laquelle l'enfant a cliqué
     *
     * DONNÉES PASSÉES À LETTERTRACINGACTIVITY:
     * - letter.id: L'identifiant de la lettre (pour la retrouver dans la liste)
     * - alphabetType.name: Le type d'alphabet (pour charger les bonnes lettres)
     *
     * POURQUOI PASSER L'ID ET PAS L'OBJET LETTER?
     * - Les objets complexes nécessitent une sérialisation (Parcelable/Serializable)
     * - Passer un simple Int est plus simple et performant
     * - L'Activity destination peut recharger la lettre depuis le JSON
     */
    private fun navigateToLetterTracing(letter: Letter) {
        // Créer un Intent vers LetterTracingActivity
        val intent = Intent(this, LetterTracingActivity::class.java)

        // Passer l'ID de la lettre cliquée
        intent.putExtra(EXTRA_LETTER_ID, letter.id)

        // Passer le type d'alphabet (pour que l'écran de traçage sache quel alphabet utiliser)
        intent.putExtra(EXTRA_ALPHABET_TYPE, alphabetType.name)

        // Démarrer l'Activity de traçage
        startActivity(intent)
    }

    // =========================================================================
    // SECTION 6: CONSTANTES
    // =========================================================================

    /**
     * Companion object contenant les constantes pour les extras d'Intent
     *
     * Ces constantes sont utilisées comme clés pour passer des données
     * entre AlphabetListActivity et LetterTracingActivity
     */
    companion object {
        /**
         * Clé pour passer l'ID de la lettre sélectionnée
         * Exemple: intent.putExtra(EXTRA_LETTER_ID, 101) pour la lettre "A"
         */
        const val EXTRA_LETTER_ID = "letter_id"

        /**
         * Clé pour passer le type d'alphabet
         * Exemple: intent.putExtra(EXTRA_ALPHABET_TYPE, "FRENCH")
         */
        const val EXTRA_ALPHABET_TYPE = "alphabet_type"
    }
}
