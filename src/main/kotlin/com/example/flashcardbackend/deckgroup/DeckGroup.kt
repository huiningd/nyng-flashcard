package com.example.flashcardbackend.deckgroup

import com.example.flashcardbackend.deck.Deck
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class DeckGroup(
    val id: Int,
    val name: String,
    val description: String?,
    val decks: List<Deck>,
)

data class DeckGroupCreateDTO(
    @field:NotBlank(message = "The deck group name is required.")
    val name: String,
    val description: String?,
)

data class DeckGroupCreate(
    val name: String,
    val description: String?,
)

fun DeckGroupCreateDTO.toDeckGroupCreate(): DeckGroupCreate =
    DeckGroupCreate(name = this.name, description = this.description)

data class DeckGroupUpdateDTO(
    @field:Positive(message = "The deck group ID should be positive number.")
    val id: Int,
    @field:NotBlank(message = "The deck group name is required.")
    val name: String,
    val description: String?,
)

data class DeckGroupUpdate(
    val id: Int,
    val name: String,
    val description: String?,
)

fun DeckGroupUpdateDTO.toDeckGroupUpdate(): DeckGroupUpdate =
    DeckGroupUpdate(id = this.id, name = this.name, description = this.description)

data class DeckGroupListItemDTO(
    val id: Int,
    val name: String,
    val description: String?,
)

data class DeckGroupListItem(
    val id: Int,
    val name: String,
    val description: String?,
)

fun DeckGroupListItem.toDeckGroupListItemDTO(): DeckGroupListItemDTO =
    DeckGroupListItemDTO(id = this.id, name = this.name, description = this.description)
