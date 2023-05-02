package com.example.flashcardbackend.flashcard

import com.example.flashcardbackend.utils.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FlashcardService(val repository: FlashcardRepository) {

    @Transactional(readOnly = true)
    fun findFlashcards(): List<FlashcardListItemDTO> =
        repository.findAll().map { it.toFlashcardListItemDTO() }

    @Transactional(readOnly = true)
    fun findFlashcardById(id: Int): FlashcardDTO? {
        val card = repository.findById(id)?.toFlashcardDTO() ?: throw(NotFoundException("Flashcard with id $id not found."))
        // TODO: use tag repository to get tags
        val tags = emptyList<String>()
        card.tags = tags
        return card
    }

    @Transactional
    fun create(flashcardCreateDTO: FlashcardCreateDTO) =
        repository.insert(flashcardCreateDTO.toFlashcardCreate())

    @Transactional
    fun update(flashcardUpdateDTO: FlashcardUpdateDTO) {
        val affectedRows = repository.update(flashcardUpdateDTO.toFlashcardUpdate())
        if (affectedRows == 0) {
            throw(NotFoundException("Flashcard with id ${flashcardUpdateDTO.id} not found."))
        }
    }

    @Transactional
    fun deleteFlashcardById(id: Int) {
        val affectedRows = repository.deleteById(id)
        if (affectedRows == 0) {
            throw(NotFoundException("Flashcard with id $id not found."))
        }
    }
}
