package com.example.flashcardbackend.flashcard

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

enum class StudyStatus {
    NEW, YOUNG, MATURE, SUSPENDED, BURIED
}

data class StudyStatusUpdateDTO(
    @field:Positive(message = "The card ID should be positive number.")
    val id: Int,
    @field:NotBlank(message = "Study status is required.")
    val studyStatus: StudyStatus,
)

data class StudyStatusUpdate(
    val id: Int,
    val studyStatus: StudyStatus,
)

fun StudyStatusUpdateDTO.toStudyStatusUpdate(): StudyStatusUpdate =
    StudyStatusUpdate(id = this.id, studyStatus = this.studyStatus)
