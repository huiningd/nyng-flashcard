package com.example.flashcardbackend.deck

import com.example.flashcardbackend.utils.NotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class DeckController(val service: DeckService) {

    @GetMapping("/decks")
    fun listAll(): List<DeckListItemDTO> = service.findDecks()

    @GetMapping("/decks/{id}")
    fun getById(@PathVariable("id") id: Int): DeckDTO? {
        return service.findDeckById(id) ?: throw(NotFoundException("Deck with id $id not found."))
    }

    @PostMapping("/decks")
    @ResponseStatus(HttpStatus.CREATED)
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
        val affectedRows = service.update(deckUpdateDTO)
        if (affectedRows == 0) {
            throw(NotFoundException("Deck with id ${deckUpdateDTO.id} not found."))
        }
    }

    @DeleteMapping("/decks/{id}")
    fun deleteById(@PathVariable("id") id: Int) {
        service.deleteDeckById(id)
    }
}
