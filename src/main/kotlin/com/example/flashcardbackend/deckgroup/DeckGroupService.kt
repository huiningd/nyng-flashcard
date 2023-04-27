package com.example.flashcardbackend.deckgroup

import com.example.flashcardbackend.utils.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeckGroupService(val repository: DeckGroupRepository) {

    @Transactional(readOnly = true)
    fun findDeckGroups(): List<DeckGroupListItemDTO> = repository.findAll().map { it.toDeckGroupListItemDTO() }

    @Transactional(readOnly = true)
    fun findDeckGroupById(id: Int): DeckGroupDTO? =
        repository.findById(id)?.toDeckGroupDTO() ?: throw(NotFoundException("Deck group with id $id not found."))

    // Spring rolls back a transaction if a method annotated with @Transactional throws a RuntimeException or any unchecked exception.
    @Transactional
    fun create(deckGroupCreateDTO: DeckGroupCreateDTO) =
        repository.insert(deckGroupCreateDTO.toDeckGroupCreate())

    @Transactional
    fun update(deckGroupUpdateDTO: DeckGroupUpdateDTO) {
        val affectedRows = repository.update(deckGroupUpdateDTO.toDeckGroupUpdate())
        if (affectedRows == 0) {
            throw(NotFoundException("Deck group with id ${deckGroupUpdateDTO.id} not found."))
        }
    }

    @Transactional
    fun deleteDeckGroupById(id: Int) {
        val affectedRows = repository.deleteById(id)
        if (affectedRows == 0) {
            throw(NotFoundException("Deck group with id $id not found."))
        }
    }
}
