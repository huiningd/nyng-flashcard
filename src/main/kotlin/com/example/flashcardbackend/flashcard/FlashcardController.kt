package com.example.flashcardbackend.flashcard

import com.example.flashcardbackend.utils.NotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class FlashcardController(val service: FlashcardService) {

    @GetMapping("/flashcards")
    fun listAll(): List<FlashcardListItemDTO> = service.findFlashcards()

    @GetMapping("/flashcards/{id}")
    fun getById(@PathVariable("id") id: Int): FlashcardDTO? {
        return service.findFlashcardById(id) ?: throw(NotFoundException("Flashcard with id $id not found."))
    }

    @PostMapping("/flashcards")
    @ResponseStatus(HttpStatus.CREATED)
    fun post(
        @Valid @RequestBody
        flashcardCreateDTO: FlashcardCreateDTO,
    ) {
        service.create(flashcardCreateDTO)
    }

    @PutMapping("/flashcards")
    fun put(
        @Valid @RequestBody
        flashcardUpdateDTO: FlashcardUpdateDTO,
    ) {
        val affectedRows = service.update(flashcardUpdateDTO)
        if (affectedRows == 0) {
            throw(NotFoundException("Flashcard with id ${flashcardUpdateDTO.id} not found."))
        }
    }

    @DeleteMapping("/flashcards/{id}")
    fun deleteById(@PathVariable("id") id: Int) {
        service.deleteFlashcardById(id)
    }
}
