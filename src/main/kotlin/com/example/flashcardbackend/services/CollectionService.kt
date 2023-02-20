package com.example.flashcardbackend.services

import com.example.flashcardbackend.models.DeckCollection
import com.example.flashcardbackend.repositories.CollectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CollectionService(val db: CollectionRepository) {

    @Transactional(readOnly = true)
    fun getCollections(): MutableIterable<DeckCollection> = db.findAll()

    @Transactional
    fun post(collection: DeckCollection) {
        db.save(collection)
    }
}
