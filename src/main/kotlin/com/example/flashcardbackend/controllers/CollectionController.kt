package com.example.flashcardbackend.controllers

import com.example.flashcardbackend.models.DeckCollection
import com.example.flashcardbackend.services.CollectionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CollectionController(val service: CollectionService) {
    @GetMapping("/collection")
    fun index(): MutableIterable<DeckCollection> = service.getCollections()

    @PostMapping("/collection")
    fun post(@RequestBody collection: DeckCollection) {
        service.post(collection)
    }
}
