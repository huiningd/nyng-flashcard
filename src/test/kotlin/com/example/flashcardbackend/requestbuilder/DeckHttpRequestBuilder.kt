package com.example.flashcardbackend.requestbuilder

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class DeckHttpRequestBuilder(private val mockMvc: MockMvc) {

    @Throws(Exception::class)
    fun findAll(): ResultActions {
        return mockMvc.perform(get("/decks"))
    }

    @Throws(Exception::class)
    fun findById(deckId: Int): ResultActions {
        return mockMvc.perform(get("/decks/$deckId"))
    }

    @Throws(Exception::class)
    fun createDeck(requestBody: String): ResultActions {
        return mockMvc.perform(
            post("/decks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun updateDeck(requestBody: String): ResultActions {
        return mockMvc.perform(
            put("/decks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun deleteById(deckId: Int): ResultActions {
        return mockMvc.perform(delete("/decks/$deckId"))
    }
}
