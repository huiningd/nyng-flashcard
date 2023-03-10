package com.example.flashcardbackend.deck

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeckService(val repository: DeckRepository) {

    @Transactional(readOnly = true)
    fun findDecks(): List<DeckListItemDTO> = repository.findAll().map { it.toDeckListItemDTO() }

    @Transactional(readOnly = true)
    fun findDeckById(id: Int): DeckDTO? = repository.findById(id)?.toDeckDTO()

    @Transactional
    fun create(deckCreateDTO: DeckCreateDTO) =
        repository.insert(deckCreateDTO.toDeckCreate())

    @Transactional
    fun update(deckUpdateDTO: DeckUpdateDTO) =
        repository.update(deckUpdateDTO.toDeckUpdate())
}
