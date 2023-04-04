package com.example.flashcardbackend.deck

import com.example.flashcardbackend.flashcard.FlashcardListItem
import com.example.flashcardbackend.flashcard.FlashcardListItemDTO
import com.example.flashcardbackend.flashcard.toFlashcardListItemDTO
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class DeckDTO(
    val id: Int,
    val deckGroupId: Int,
    val name: String,
    val description: String?,
    val flashcards: List<FlashcardListItemDTO>,
)

data class Deck(
    val id: Int,
    val deckGroupId: Int,
    val name: String,
    val description: String?,
    val flashcards: List<FlashcardListItem>,
)

fun Deck.toDeckDTO(): DeckDTO =
    DeckDTO(
        id = this.id,
        deckGroupId = this.deckGroupId,
        name = this.name,
        description = this.description,
        flashcards = this.flashcards.map { it.toFlashcardListItemDTO() },
    )

data class DeckCreateDTO(
    @field:Positive(message = "The deck group ID should be positive number.")
    val deckGroupId: Int,
    @field:NotBlank(message = "The deck name is required.")
    val name: String,
    val description: String?,
)

data class DeckCreate(
    val deckGroupId: Int,
    val name: String,
    val description: String?,
)

fun DeckCreateDTO.toDeckCreate(): DeckCreate =
    DeckCreate(deckGroupId = this.deckGroupId, name = this.name, description = this.description)

data class DeckUpdateDTO(
    @field:Positive(message = "The deck ID should be positive number.")
    val id: Int,
    @field:Positive(message = "The deck group ID should be positive number.")
    val deckGroupId: Int,
    @field:NotBlank(message = "The deck name is required.")
    val name: String,
    val description: String?,
)

data class DeckUpdate(
    val id: Int,
    val deckGroupId: Int,
    val name: String,
    val description: String?,
)

fun DeckUpdateDTO.toDeckUpdate(): DeckUpdate =
    DeckUpdate(id = this.id, deckGroupId = this.deckGroupId, name = this.name, description = this.description)

data class DeckListItemDTO(
    val id: Int,
    val deckGroupId: Int,
    val name: String,
    val description: String?,
)

data class DeckListItem(
    val id: Int,
    val deckGroupId: Int,
    val name: String,
    val description: String?,
)

fun DeckListItem.toDeckListItemDTO(): DeckListItemDTO =
    DeckListItemDTO(id = this.id, deckGroupId = this.deckGroupId, name = this.name, description = this.description)
