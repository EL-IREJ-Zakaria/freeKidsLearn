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
 * Activity to display list of letters in selected alphabet
 */
class AlphabetListActivity : AppCompatActivity() {

    private lateinit var alphabetType: AlphabetType
    private lateinit var letters: List<Letter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alphabet_list)

        // Get alphabet type from intent
        val typeString =
            intent.getStringExtra(MainActivity.EXTRA_ALPHABET_TYPE) ?: AlphabetType.FRENCH.name
        alphabetType = AlphabetType.valueOf(typeString)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val title = when (alphabetType) {
            AlphabetType.ARABIC -> getString(R.string.arabic_alphabet)
            AlphabetType.FRENCH -> getString(R.string.french_alphabet)
        }
        supportActionBar?.title = title

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        letters = AlphabetLoader.loadLetters(this, alphabetType)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLetters)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val adapter = LetterAdapter(letters) { letter ->
            navigateToLetterTracing(letter)
        }
        recyclerView.adapter = adapter
    }

    private fun navigateToLetterTracing(letter: Letter) {
        val intent = Intent(this, LetterTracingActivity::class.java)
        intent.putExtra(EXTRA_LETTER_ID, letter.id)
        intent.putExtra(EXTRA_ALPHABET_TYPE, alphabetType.name)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_LETTER_ID = "letter_id"
        const val EXTRA_ALPHABET_TYPE = "alphabet_type"
    }
}
