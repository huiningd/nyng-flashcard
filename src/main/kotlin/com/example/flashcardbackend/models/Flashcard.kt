package com.example.flashcardbackend.models

import jakarta.persistence.*

@Entity
@Table(name = "flashcard")
class Flashcard(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int,
)

@Entity
@Table(name = "comment")
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int,

    @Column(name = "text")
    val text: String
)
