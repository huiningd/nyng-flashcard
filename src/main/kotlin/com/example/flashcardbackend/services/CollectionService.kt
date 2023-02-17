package com.example.flashcardbackend.services

import com.example.flashcardbackend.models.DeckCollection
import com.example.flashcardbackend.repositories.CollectionRepository
import org.springframework.stereotype.Service

@Service
class CollectionService(val db: CollectionRepository) {

    fun getCollections(): MutableIterable<DeckCollection> = db.findAll()

    fun post(collection: DeckCollection) {
        db.save(collection)
    }
}
