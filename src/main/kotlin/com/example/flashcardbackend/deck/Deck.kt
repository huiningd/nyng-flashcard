package com.example.flashcardbackend.deck

import com.example.flashcardbackend.flashcard.Flashcard

data class Deck(
    val id: Int,
    val collectionId: Int,
    val name: String,
    val description: String?,
    val flashcards: List<Flashcard>,
)
