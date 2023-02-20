package com.example.flashcardbackend.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "collection")
class DeckCollection(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int,

    @Column(name = "collection_name")
    val collectionName: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime,

    @JoinColumn(name = "collection_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val decks: List<Deck>
)

@Entity
@Table(name = "deck")
class Deck(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int,

    @Column(name = "collection_id")
    val collectionId: Int,

    @Column(name = "deck_name")
    val deckName: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime
)
