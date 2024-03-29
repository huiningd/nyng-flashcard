package com.example.flashcardbackend.flashcard

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class FlashcardController(val service: FlashcardService) {

    @GetMapping("/flashcards")
    fun listAll(): List<FlashcardListItemDTO> = service.findFlashcards()

    @GetMapping("/flashcards/{id}")
    fun getById(@PathVariable("id") id: Int): FlashcardDTO? =
        service.findFlashcardById(id)

    @PostMapping("/flashcards")
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @Valid @RequestBody
        flashcardCreateDTO: FlashcardCreateDTO,
    ) = service.create(flashcardCreateDTO)

    @PutMapping("/flashcards")
    fun put(
        @Valid @RequestBody
        flashcardUpdateDTO: FlashcardUpdateDTO,
    ) = service.update(flashcardUpdateDTO)

    @DeleteMapping("/flashcards/{id}")
    fun deleteById(@PathVariable("id") id: Int) = service.deleteFlashcardById(id)
}
