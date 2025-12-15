package com.example.freekidslearn.data

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a letter in the alphabet
 */
data class Letter(
    @SerializedName("id")
    val id: Int,

    @SerializedName("letter")
    val letter: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("animal")
    val animal: String? = null,

    @SerializedName("soundFile")
    val soundFile: String? = null
)
