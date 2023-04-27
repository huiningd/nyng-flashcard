package com.example.flashcardbackend.deckgroup

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class DeckGroupController(val service: DeckGroupService) {

    @GetMapping("/deckgroups")
    fun listAll(): List<DeckGroupListItemDTO> = service.findDeckGroups()

    @GetMapping("/deckgroups/{id}")
    fun getById(@PathVariable("id") id: Int): DeckGroupDTO? = service.findDeckGroupById(id)

    @PostMapping("/deckgroups")
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @Valid @RequestBody
        deckGroupCreateDTO: DeckGroupCreateDTO,
    ) = service.create(deckGroupCreateDTO)

    @PutMapping("/deckgroups")
    fun put(
        @Valid @RequestBody
        deckGroupUpdateDTO: DeckGroupUpdateDTO,
    ) = service.update(deckGroupUpdateDTO)

    @DeleteMapping("/deckgroups/{id}")
    fun deleteById(@PathVariable("id") id: Int) = service.deleteDeckGroupById(id)
}
