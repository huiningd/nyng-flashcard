package com.example.flashcardbackend.flashcard

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class FlashcardDTO(
    val id: Int,
    val deckId: Int,
    val frontText: String,
    val frontMediaUrl: String?,
    val backText: String?,
    val backMediaUrl: String?,
    val studyStatus: StudyStatus,
    var tags: List<String> = emptyList(),
    val comment: String?,
    val lastViewed: LocalDateTime,
)

data class Flashcard(
    val id: Int,
    val deckId: Int,
    val frontText: String,
    val frontMediaUrl: String?,
    val backText: String?,
    val backMediaUrl: String?,
    val studyStatus: StudyStatus,
    val comment: String?,
    val lastViewed: LocalDateTime,
)

fun Flashcard.toFlashcardDTO(): FlashcardDTO =
    FlashcardDTO(
        id = this.id,
        deckId = this.deckId,
        frontText = this.frontText,
        frontMediaUrl = this.frontMediaUrl,
        backText = this.backText,
        backMediaUrl = this.backMediaUrl,
        studyStatus = this.studyStatus,
        comment = this.comment,
        lastViewed = this.lastViewed,
    )

data class FlashcardCreateDTO(
    @field:Positive(message = "The deck ID should be positive number.")
    val deckId: Int,
    @field:NotNull(message = "Flash card's front content is required.")
    val front: CardContentCreateDTO,
    val back: CardContentCreateDTO?,
    val comment: String?,
)

data class FlashcardCreate(
    val deckId: Int,
    val front: CardContentCreate,
    val back: CardContentCreate?,
    val comment: String?,
)

fun FlashcardCreateDTO.toFlashcardCreate(): FlashcardCreate =
    FlashcardCreate(
        deckId = this.deckId,
        front = this.front.toCardContentCreate(),
        back = this.back?.toCardContentCreate(),
        comment = this.comment,
    )

data class FlashcardUpdateDTO(
    @field:Positive(message = "The card ID should be positive number.")
    val id: Int,
    @field:Positive(message = "The deck ID should be positive number.")
    val deckId: Int,
    @field:Positive(message = "The deck ID should be positive number.")
    val cardTypeId: Int,
    val front: CardContentUpdateDTO?,
    val back: CardContentUpdateDTO?,
    val comment: String?,
)

data class FlashcardUpdate(
    val id: Int,
    val deckId: Int,
    val cardTypeId: Int,
    val front: CardContentUpdate?,
    val back: CardContentUpdate?,
    val comment: String?,
)

fun FlashcardUpdateDTO.toFlashcardUpdate(): FlashcardUpdate =
    FlashcardUpdate(
        id = this.id,
        deckId = this.deckId,
        front = this.front?.toCardContentUpdate(),
        back = this.back?.toCardContentUpdate(),
        cardTypeId = this.cardTypeId,
        comment = this.comment,
    )

data class FlashcardListItemDTO(
    val id: Int,
    val frontContentText: String,
)

data class FlashcardListItem(
    val id: Int,
    val cardContentId: Int,
    val frontContentText: String,
)

fun FlashcardListItem.toFlashcardListItemDTO(): FlashcardListItemDTO =
    FlashcardListItemDTO(
        id = this.id,
        frontContentText = this.frontContentText,
    )
