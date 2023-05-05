package com.example.flashcardbackend.integrationtest

import com.example.flashcardbackend.deck.DeckDTO
import com.example.flashcardbackend.deck.DeckListItemDTO
import com.example.flashcardbackend.deckgroup.DeckGroupDTO
import com.example.flashcardbackend.deckgroup.DeckGroupListItem
import com.example.flashcardbackend.flashcard.FlashcardDTO
import com.example.flashcardbackend.flashcard.FlashcardListItemDTO
import com.example.flashcardbackend.flashcard.StudyStatus
import java.time.LocalDateTime

val flashcardManzana = FlashcardDTO(
    id = 1,
    deckId = 1,
    frontText = "What is the Spanish word for apple?",
    frontMediaUrl = null,
    backText = "manzana",
    backMediaUrl = null,
    studyStatus = StudyStatus.NEW,
    comment = null,
    tags = emptyList(),
    lastViewed = LocalDateTime.now(),
)

val flashcardListItemManzana = FlashcardListItemDTO(1, "What is the Spanish word for apple?")
val flashcardListItemLeche = FlashcardListItemDTO(4, "What is the Spanish word for milk?")
val flashcardListItemFromage = FlashcardListItemDTO(2, "Translate to French: cheese")
val flashcardListItemNewton = FlashcardListItemDTO(3, "What is Newton\'s first law of motion?")

val flashcards = listOf(
    flashcardListItemManzana,
    flashcardListItemLeche,
    flashcardListItemFromage,
    flashcardListItemNewton,
)

val deckSpanish = DeckDTO(
    1,
    1,
    "Spanish Vocabulary",
    "A deck for learning Spanish vocabulary.",
    flashcards = listOf(flashcardListItemManzana, flashcardListItemLeche),
)

val deckListItemSpanish = DeckListItemDTO(1, 1, "Spanish Vocabulary", "A deck for learning Spanish vocabulary.")
val deckListItemFrench = DeckListItemDTO(2, 1, "French Vocabulary", "A deck for learning French vocabulary.")
val deckListItemPhysics = DeckListItemDTO(3, 3, "Physics Basics", "A deck for learning the basics of Physics.")

val decks = listOf(deckListItemSpanish, deckListItemFrench, deckListItemPhysics)

val deckGroups = listOf(
    DeckGroupListItem(1, "Languages", "A group of decks for learning languages."),
    DeckGroupListItem(2, "Movies", "A group of decks for learning movies topics."),
    DeckGroupListItem(3, "Science", "A group of decks for learning science topics."),
)

val deckGroupLang = DeckGroupDTO(
    1,
    "Languages",
    "A group of decks for learning languages.",
    decks = listOf(
        deckListItemSpanish,
        deckListItemFrench,
    ),
)

val deckGroupMovies = DeckGroupDTO(2, "Movies", "A group of decks for learning movies topics.", decks = emptyList())
