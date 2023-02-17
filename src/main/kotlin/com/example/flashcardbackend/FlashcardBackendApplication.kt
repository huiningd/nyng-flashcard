package com.example.flashcardbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlashcardBackendApplication

fun main(args: Array<String>) {
    runApplication<FlashcardBackendApplication>(*args)
}
