package com.example.flashcardbackend.flashcard

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FlashcardService(val repository: FlashcardRepository) {

    @Transactional(readOnly = true)
    fun findFlashcards(): List<FlashcardListItemDTO> =
        repository.findAll().map { it.toFlashcardListItemDTO() }

    @Transactional(readOnly = true)
    fun findCardById(id: Int): FlashcardDTO? =
        repository.findById(id)?.toFlashcardDTO()

    @Transactional
    fun create(flashcardCreateDTO: FlashcardCreateDTO) =
        repository.insert(flashcardCreateDTO.toFlashcardCreate())

    @Transactional
    fun update(flashcardUpdateDTO: FlashcardUpdateDTO) =
        repository.update(flashcardUpdateDTO.toFlashcardUpdate())
}
