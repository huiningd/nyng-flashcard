package com.example.flashcardbackend.flashcard

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FlashcardService(val repository: FlashcardRepository) {

    @Transactional(readOnly = true)
    fun findFlashcards(): List<FlashcardListItemDTO> =
        repository.findAll().map { it.toFlashcardListItemDTO() }

    @Transactional(readOnly = true)
    fun findFlashcardById(id: Int): FlashcardDTO? {
        val card = repository.findById(id)?.toFlashcardDTO()
        // TODO: use tag repository to get tags
        val tags = emptyList<String>()
        if (card != null) {
            card.tags = tags
        }
        return card
    }

    @Transactional
    fun create(flashcardCreateDTO: FlashcardCreateDTO) =
        repository.insert(flashcardCreateDTO.toFlashcardCreate())

    @Transactional
    fun update(flashcardUpdateDTO: FlashcardUpdateDTO) =
        repository.update(flashcardUpdateDTO.toFlashcardUpdate())

    @Transactional
    fun deleteFlashcardById(id: Int) = repository.deleteById(id)
}
