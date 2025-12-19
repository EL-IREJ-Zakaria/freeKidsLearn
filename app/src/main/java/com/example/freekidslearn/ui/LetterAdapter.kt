package com.example.freekidslearn.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freekidslearn.R
import com.example.freekidslearn.data.Letter

/**
 * RecyclerView adapter for displaying letters in a colorful grid
 */
class LetterAdapter(
    private val letters: List<Letter>,
    private val onLetterClick: (Letter) -> Unit
) : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    // Couleurs arc-en-ciel pour les cercles des lettres
    private val circleBackgrounds = listOf(
        R.drawable.circle_letter_pink,
        R.drawable.circle_letter_purple,
        R.drawable.circle_letter_blue,
        R.drawable.circle_letter_green,
        R.drawable.circle_letter_orange,
        R.drawable.circle_letter_red
    )

    inner class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textLetter: TextView = itemView.findViewById(R.id.textLetter)
        private val circleBackground: View = itemView.findViewById(R.id.circleBackground)

        fun bind(letter: Letter, position: Int) {
            textLetter.text = letter.letter

            // Couleur arc-en-ciel basée sur la position
            val colorIndex = position % circleBackgrounds.size
            circleBackground.setBackgroundResource(circleBackgrounds[colorIndex])

            itemView.setOnClickListener {
                onLetterClick(letter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_letter, parent, false)
        return LetterViewHolder(view)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.bind(letters[position], position)
    }

    override fun getItemCount(): Int = letters.size
}
