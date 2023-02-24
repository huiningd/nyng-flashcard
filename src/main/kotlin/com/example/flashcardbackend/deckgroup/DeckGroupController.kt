package com.example.flashcardbackend.deckgroup

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DeckGroupController(val service: DeckGroupService) {

    @GetMapping("/deckgroup")
    fun index(): List<DeckGroupListItemDTO> = service.findDeckGroups()

    @PostMapping("/deckgroup")
    fun post(
        @Valid @RequestBody
        deckGroupCreateDTO: DeckGroupCreateDTO,
    ) {
        // TODO handle validation exception
        service.create(deckGroupCreateDTO)
    }

    @PutMapping("/deckgroup")
    fun put(
        @Valid @RequestBody
        deckGroupUpdateDTO: DeckGroupUpdateDTO,
    ) {
        service.update(deckGroupUpdateDTO)
    }
}
