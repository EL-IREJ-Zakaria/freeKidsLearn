package com.example.freekidslearn.utils

import android.content.Context
import com.example.freekidslearn.data.AlphabetType
import com.example.freekidslearn.data.Letter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * Utility class to load alphabet data from JSON files
 */
object AlphabetLoader {

    /**
     * Load letters from JSON file based on alphabet type
     */
    fun loadLetters(context: Context, type: AlphabetType): List<Letter> {
        val fileName = when (type) {
            AlphabetType.ARABIC -> "arabic_alphabet.json"
            AlphabetType.FRENCH -> "french_alphabet.json"
        }

        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Letter>>() {}.type
            Gson().fromJson(jsonString, listType)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }
}
