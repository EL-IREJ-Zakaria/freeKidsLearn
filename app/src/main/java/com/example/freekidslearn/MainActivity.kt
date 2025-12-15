package com.example.freekidslearn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.freekidslearn.data.AlphabetType
import com.google.android.material.card.MaterialCardView

/**
 * Main activity - Home screen with language selection
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLanguageSelection()
    }

    private fun setupLanguageSelection() {
        val cardArabic = findViewById<MaterialCardView>(R.id.cardArabic)
        val cardFrench = findViewById<MaterialCardView>(R.id.cardFrench)

        cardArabic.setOnClickListener {
            navigateToAlphabetList(AlphabetType.ARABIC)
        }

        cardFrench.setOnClickListener {
            navigateToAlphabetList(AlphabetType.FRENCH)
        }
    }

    private fun navigateToAlphabetList(type: AlphabetType) {
        val intent = Intent(this, AlphabetListActivity::class.java)
        intent.putExtra(EXTRA_ALPHABET_TYPE, type.name)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_ALPHABET_TYPE = "alphabet_type"
    }
}