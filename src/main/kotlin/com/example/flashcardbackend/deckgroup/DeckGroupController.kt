package com.example.flashcardbackend.deckgroup

import com.example.flashcardbackend.utils.NotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class DeckGroupController(val service: DeckGroupService) {

    @GetMapping("/deckgroups")
    fun listAll(): List<DeckGroupListItemDTO> = service.findDeckGroups()

    @GetMapping("/deckgroups/{id}")
    fun getById(@PathVariable("id") id: Int): DeckGroupDTO? {
        return service.findDeckGroupById(id) ?: throw(NotFoundException("Deck group with id $id not found."))
    }

    @PostMapping("/deckgroups")
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @Valid @RequestBody
        deckGroupCreateDTO: DeckGroupCreateDTO,
    ) {
        service.create(deckGroupCreateDTO)
    }

    @PutMapping("/deckgroups")
    fun put(
        @Valid @RequestBody
        deckGroupUpdateDTO: DeckGroupUpdateDTO,
    ) {
        val affectedRows = service.update(deckGroupUpdateDTO)
        if (affectedRows == 0) {
            throw(NotFoundException("Deck group with id ${deckGroupUpdateDTO.id} not found."))
        }
    }

    @DeleteMapping("/deckgroups/{id}")
    fun deleteById(@PathVariable("id") id: Int) {
        service.deleteDeckGroupById(id)
    }
}
