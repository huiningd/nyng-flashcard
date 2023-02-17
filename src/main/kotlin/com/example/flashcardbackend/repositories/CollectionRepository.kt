package com.example.flashcardbackend.repositories

import com.example.flashcardbackend.models.DeckCollection
import org.springframework.data.repository.CrudRepository

interface CollectionRepository : CrudRepository<DeckCollection, Int>
