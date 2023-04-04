package com.example.flashcardbackend.deck

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class DeckController(val service: DeckService) {

    @GetMapping("/decks")
    fun listAll(): List<DeckListItemDTO> = service.findDecks()

    @GetMapping("/decks/{id}")
    fun getById(@PathVariable("id") id: Int): DeckDTO? {
        return service.findDeckById(id)
    }

    @PostMapping("/decks")
    fun post(
        @Valid @RequestBody
        deckCreateDTO: DeckCreateDTO,
    ) {
        service.create(deckCreateDTO)
    }

    @PutMapping("/decks")
    fun put(
        @Valid @RequestBody
        deckUpdateDTO: DeckUpdateDTO,
    ) {
        service.update(deckUpdateDTO)
    }

    @DeleteMapping("/decks/{id}")
    fun deleteById(@PathVariable("id") id: Int) {
        service.deleteDeckById(id)
    }
}
