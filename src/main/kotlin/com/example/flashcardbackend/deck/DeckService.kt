package com.example.flashcardbackend.deck

import com.example.flashcardbackend.utils.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeckService(val repository: DeckRepository) {

    @Transactional(readOnly = true)
    fun findDecks(): List<DeckListItemDTO> = repository.findAll().map { it.toDeckListItemDTO() }

    @Transactional(readOnly = true)
    fun findDeckById(id: Int): DeckDTO? =
        repository.findById(id)?.toDeckDTO() ?: throw(NotFoundException("Deck with id $id not found."))

    @Transactional
    fun create(deckCreateDTO: DeckCreateDTO) =
        repository.insert(deckCreateDTO.toDeckCreate())

    @Transactional
    fun update(deckUpdateDTO: DeckUpdateDTO) {
        val affectedRows = repository.update(deckUpdateDTO.toDeckUpdate())
        if (affectedRows == 0) {
            throw(NotFoundException("Deck with id ${deckUpdateDTO.id} not found."))
        }
    }

    @Transactional
    fun deleteDeckById(id: Int) {
        val affectedRows = repository.deleteById(id)
        if (affectedRows == 0) {
            throw(NotFoundException("Deck with id $id not found."))
        }
    }
}
