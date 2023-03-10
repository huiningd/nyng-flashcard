package com.example.flashcardbackend.flashcard

import CardContent
import CardContentCreate
import CardContentCreateDTO
import CardContentDTO
import CardContentUpdate
import CardContentUpdateDTO
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import toCardContentCreate
import toCardContentDTO
import toCardContentUpdate
import java.time.LocalDateTime

typealias CardTagId = String

data class CardTag(
    val name: String,
)

data class FlashcardDTO(
    val id: Int,
    val deckId: Int,
    val front: CardContentDTO,
    val back: CardContentDTO?,
    val studyStatus: StudyStatus,
    val tags: MutableList<String>,
    val comment: String?,
    val lastViewed: LocalDateTime,
)

data class Flashcard(
    val id: Int,
    val deckId: Int,
    val front: CardContent,
    val back: CardContent?,
    val studyStatus: StudyStatus,
    val comment: String?,
    val tags: MutableList<String>,
    val lastViewed: LocalDateTime,
)

fun Flashcard.toFlashcardDTO(): FlashcardDTO =
    FlashcardDTO(
        id = this.id,
        deckId = this.deckId,
        front = this.front.toCardContentDTO(),
        back = this.back?.toCardContentDTO(),
        studyStatus = this.studyStatus,
        comment = this.comment,
        tags = this.tags,
        lastViewed = this.lastViewed,
    )

data class FlashcardCreateDTO(
    @field:Positive(message = "The deck ID should be positive number.")
    val deckId: Int,
    @field:NotNull(message = "Flash card's front content is required.")
    val front: CardContentCreateDTO,
    val back: CardContentCreateDTO?,
    val comment: String?,
    val tags: List<CardTagId>?,
)

data class FlashcardCreate(
    val deckId: Int,
    val front: CardContentCreate,
    val back: CardContentCreate?,
    val comment: String?,
    val tags: List<CardTagId>?,
)

fun FlashcardCreateDTO.toFlashcardCreate(): FlashcardCreate =
    FlashcardCreate(
        deckId = this.deckId,
        front = this.front.toCardContentCreate(),
        back = this.back?.toCardContentCreate(),
        comment = this.comment,
        tags = this.tags,
    )

data class FlashcardUpdateDTO(
    @field:Positive(message = "The card ID should be positive number.")
    val id: Int,
    @field:Positive(message = "The deck ID should be positive number.")
    val deckId: Int,
    val front: CardContentUpdateDTO?,
    val back: CardContentUpdateDTO?,
    val comment: String?,
    val tags: List<CardTagId>?,
)

data class FlashcardUpdate(
    val id: Int,
    val deckId: Int,
    val front: CardContentUpdate?,
    val back: CardContentUpdate?,
    val comment: String?,
    val tags: List<CardTagId>?,
)

fun FlashcardUpdateDTO.toFlashcardUpdate(): FlashcardUpdate =
    FlashcardUpdate(
        id = this.id,
        deckId = this.deckId,
        front = this.front?.toCardContentUpdate(),
        back = this.back?.toCardContentUpdate(),
        comment = this.comment,
        tags = this.tags,
    )

data class FlashcardListItemDTO(
    val id: Int,
    val deckId: Int,
    val contentPreview: String,
)

data class FlashcardListItem(
    val id: Int,
    val deckId: Int,
    val contentPreview: String,
)

fun FlashcardListItem.toFlashcardListItemDTO(): FlashcardListItemDTO =
    FlashcardListItemDTO(
        id = this.id,
        deckId = this.deckId,
        contentPreview = this.contentPreview,
    )
