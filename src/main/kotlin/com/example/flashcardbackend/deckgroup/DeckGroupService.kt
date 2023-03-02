package com.example.flashcardbackend.deckgroup

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeckGroupService(val repository: DeckGroupRepository) {

    @Transactional(readOnly = true)
    fun findDeckGroups(): List<DeckGroupListItemDTO> = repository.findAll().map { it.toDeckGroupListItemDTO() }

    @Transactional
    fun create(deckGroupCreateDTO: DeckGroupCreateDTO) =
        repository.insert(deckGroupCreateDTO.toDeckGroupCreate())

    @Transactional
    fun update(deckGroupUpdateDTO: DeckGroupUpdateDTO) =
        repository.update(deckGroupUpdateDTO.toDeckGroupUpdate())
}
