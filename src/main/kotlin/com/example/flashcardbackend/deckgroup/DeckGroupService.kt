package com.example.flashcardbackend.deckgroup

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeckGroupService(val repository: DeckGroupRepository) {

    @Transactional(readOnly = true)
    fun findDeckGroups(): List<DeckGroupListItemDTO> = repository.findAll().map { it.toDeckGroupListItemDTO() }

    @Transactional(readOnly = true)
    fun findDeckGroupById(id: Int): DeckGroupDTO? = repository.findById(id)?.toDeckGroupDTO()

    @Transactional
    fun create(deckGroupCreateDTO: DeckGroupCreateDTO) =
        repository.insert(deckGroupCreateDTO.toDeckGroupCreate())

    @Transactional
    fun update(deckGroupUpdateDTO: DeckGroupUpdateDTO): Int =
        repository.update(deckGroupUpdateDTO.toDeckGroupUpdate())

    @Transactional
    fun deleteDeckGroupById(id: Int) = repository.deleteById(id)
}
