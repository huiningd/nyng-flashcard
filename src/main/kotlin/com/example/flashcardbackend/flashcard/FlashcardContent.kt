package com.example.flashcardbackend.flashcard

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

enum class CardContentType {
    FRONT, BACK,
}

data class CardContentCreateDTO(
    @field:NotBlank(message = "The card content is required.")
    val text: String,
    val mediaUrl: String?,
)

data class CardContentCreate(
    val text: String,
    val mediaUrl: String?,
)

fun CardContentCreateDTO.toCardContentCreate(): CardContentCreate =
    CardContentCreate(text = this.text, mediaUrl = this.mediaUrl)

data class CardContentUpdateDTO(
    @field:Positive(message = "The deck ID should be positive number.")
    val id: Int,
    @field:NotBlank(message = "The card content type is required.")
    val cardContentType: CardContentType,
    @field:NotBlank(message = "The card content is required.")
    val text: String,
    val mediaUrl: String?,
)

data class CardContentUpdate(
    val id: Int,
    val cardContentType: CardContentType,
    val text: String,
    val mediaUrl: String?,
)

fun CardContentUpdateDTO.toCardContentUpdate(): CardContentUpdate =
    CardContentUpdate(id = this.id, cardContentType = this.cardContentType, text = this.text, mediaUrl = this.mediaUrl)
