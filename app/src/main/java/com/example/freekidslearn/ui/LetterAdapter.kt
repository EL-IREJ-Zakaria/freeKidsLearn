package com.example.freekidslearn.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freekidslearn.R
import com.example.freekidslearn.data.Letter

/**
 * RecyclerView adapter for displaying letters in a grid
 */
class LetterAdapter(
    private val letters: List<Letter>,
    private val onLetterClick: (Letter) -> Unit
) : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    inner class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textLetter: TextView = itemView.findViewById(R.id.textLetter)
        private val textLetterName: TextView = itemView.findViewById(R.id.textLetterName)
        private val textAnimal: TextView = itemView.findViewById(R.id.textAnimal)

        fun bind(letter: Letter) {
            textLetter.text = letter.letter
            textLetterName.text = letter.name
            textAnimal.text = letter.animal ?: ""

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
        holder.bind(letters[position])
    }

    override fun getItemCount(): Int = letters.size
}
