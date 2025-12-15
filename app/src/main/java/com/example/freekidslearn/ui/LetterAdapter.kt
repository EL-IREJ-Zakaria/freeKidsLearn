package com.example.freekidslearn.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freekidslearn.R
import com.example.freekidslearn.data.Letter

/**
 * ============================================================================
 * LETTERADAPTER.KT - Adaptateur RecyclerView pour afficher les lettres
 * ============================================================================
 *
 * Ce fichier définit l'adaptateur qui fait le lien entre les données (lettres)
 * et l'interface utilisateur (RecyclerView).
 *
 * QU'EST-CE QU'UN RECYCLERVIEW?
 * - RecyclerView est un composant Android pour afficher des listes
 * - Il "recycle" les vues: au lieu de créer une vue par élément,
 *   il réutilise les vues qui sortent de l'écran
 * - Très performant même avec des milliers d'éléments
 *
 * QU'EST-CE QU'UN ADAPTER?
 * - L'Adapter est le "pont" entre les données et l'affichage
 * - Il indique combien d'éléments afficher
 * - Il crée les vues pour chaque élément
 * - Il remplit les vues avec les données
 *
 * ARCHITECTURE:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                      RecyclerView                               │
 * │  ┌───────────────────────────────────────────────────────────┐  │
 * │  │                    LetterAdapter                          │  │
 * │  │  - letters: List<Letter>         (données)                │  │
 * │  │  - onLetterClick: callback       (action au clic)         │  │
 * │  │                                                           │  │
 * │  │  ┌─────────────────────────────────────────────────────┐  │  │
 * │  │  │            ViewHolder (item_letter.xml)             │  │  │
 * │  │  │  ┌───────┐  ┌───────────────────────────────────┐   │  │  │
 * │  │  │  │   A   │  │  Nom: "A"                         │   │  │  │
 * │  │  │  │       │  │  Animal: "🐊 Alligator"           │   │  │  │
 * │  │  │  └───────┘  └───────────────────────────────────┘   │  │  │
 * │  │  └─────────────────────────────────────────────────────┘  │  │
 * │  └───────────────────────────────────────────────────────────┘  │
 * └─────────────────────────────────────────────────────────���───────┘
 *
 * PATTERN VIEWHOLDER:
 * - ViewHolder conserve les références aux vues (TextView, etc.)
 * - Évite d'appeler findViewById() à chaque affichage (coûteux)
 * - Améliore les performances lors du défilement
 *
 * ============================================================================
 */
class LetterAdapter(
    /**
     * Liste des lettres à afficher
     * Passée au constructeur et ne change pas (immutable)
     */
    private val letters: List<Letter>,

    /**
     * Callback appelé quand l'utilisateur clique sur une lettre
     * Lambda qui reçoit la lettre cliquée en paramètre
     *
     * EXEMPLE D'UTILISATION:
     * LetterAdapter(letters) { letter ->
     *     // Naviguer vers l'écran de traçage avec cette lettre
     *     startActivity(Intent(...).putExtra("letter_id", letter.id))
     * }
     */
    private val onLetterClick: (Letter) -> Unit
) : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    // =========================================================================
    // SECTION 1: CLASSE VIEWHOLDER
    // =========================================================================

    /**
     * ViewHolder - Contient les références aux vues d'un élément de la liste
     *
     * 'inner class' permet d'accéder aux propriétés de la classe parente
     * (letters, onLetterClick)
     *
     * @param itemView La vue racine de l'élément (item_letter.xml gonflé)
     */
    inner class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * TextView pour afficher la lettre en grand (ex: "A", "ب")
         */
        private val textLetter: TextView = itemView.findViewById(R.id.textLetter)

        /**
         * TextView pour afficher le nom de la lettre (ex: "A", "Alif")
         */
        private val textLetterName: TextView = itemView.findViewById(R.id.textLetterName)

        /**
         * TextView pour afficher l'animal associé (ex: "🐊 Alligator")
         */
        private val textAnimal: TextView = itemView.findViewById(R.id.textAnimal)

        /**
         * Remplit les vues avec les données d'une lettre
         *
         * @param letter L'objet Letter contenant les données à afficher
         *
         * FONCTIONNEMENT:
         * 1. Affiche la lettre dans le grand TextView
         * 2. Affiche le nom de la lettre
         * 3. Affiche l'animal associé (ou chaîne vide si null)
         * 4. Configure le listener de clic
         */
        fun bind(letter: Letter) {
            // Afficher la lettre (ex: "A" ou "ا")
            textLetter.text = letter.letter

            // Afficher le nom de la lettre (ex: "A" ou "Alif")
            textLetterName.text = letter.name

            // Afficher l'animal associé (ex: "🐊 Alligator")
            // Si letter.animal est null, afficher une chaîne vide
            textAnimal.text = letter.animal ?: ""

            // Configurer l'action au clic sur l'élément
            itemView.setOnClickListener {
                // Appeler le callback avec la lettre cliquée
                onLetterClick(letter)
            }
        }
    }

    // =========================================================================
    // SECTION 2: MÉTHODES DE L'ADAPTER (REQUISES)
    // =========================================================================

    /**
     * Crée un nouveau ViewHolder quand nécessaire
     *
     * @param parent Le ViewGroup parent (le RecyclerView)
     * @param viewType Type de vue (utile si on a plusieurs layouts différents)
     * @return Un nouveau LetterViewHolder
     *
     * FONCTIONNEMENT:
     * 1. Utilise LayoutInflater pour "gonfler" le XML en objet View
     * 2. Passe cette vue au constructeur du ViewHolder
     *
     * QUAND EST-ELLE APPELÉE?
     * - Au départ, pour créer assez de ViewHolders pour remplir l'écran
     * - Rarement ensuite car les ViewHolders sont recyclés
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        // Gonfler le layout XML de l'élément
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_letter, parent, false)
        // Créer et retourner un nouveau ViewHolder
        return LetterViewHolder(view)
    }

    /**
     * Remplit un ViewHolder existant avec les données d'une position
     *
     * @param holder Le ViewHolder à remplir (recyclé ou nouveau)
     * @param position L'index de l'élément dans la liste (0, 1, 2...)
     *
     * FONCTIONNEMENT:
     * - Récupère la lettre à la position donnée
     * - Appelle bind() sur le ViewHolder pour afficher les données
     *
     * QUAND EST-ELLE APPELÉE?
     * - À chaque fois qu'un élément doit être affiché
     * - Lors du défilement, pour les éléments qui apparaissent
     */
    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        // Récupérer la lettre à cette position et la lier au ViewHolder
        holder.bind(letters[position])
    }

    /**
     * Retourne le nombre total d'éléments dans la liste
     *
     * @return Le nombre de lettres à afficher
     *
     * UTILISÉ PAR:
     * - RecyclerView pour savoir combien d'éléments gérer
     * - La scrollbar pour calculer sa taille
     */
    override fun getItemCount(): Int = letters.size
}
