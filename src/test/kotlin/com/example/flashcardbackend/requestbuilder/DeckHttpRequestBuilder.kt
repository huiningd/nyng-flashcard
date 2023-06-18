package com.example.flashcardbackend.requestbuilder

import com.example.flashcardbackend.deck.DeckCreateDTO
import com.example.flashcardbackend.deck.DeckUpdateDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class DeckHttpRequestBuilder(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {

    @Throws(Exception::class)
    fun findAll(): ResultActions {
        return mockMvc.perform(get("/decks"))
    }

    @Throws(Exception::class)
    fun findById(deckId: Int): ResultActions {
        return mockMvc.perform(get("/decks/$deckId"))
    }

    @Throws(Exception::class)
    fun createDeck(deckCreateDTO: DeckCreateDTO): ResultActions {
        val requestBody = objectMapper.writeValueAsString(deckCreateDTO)
        return mockMvc.perform(
            post("/decks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun updateDeck(deckUpdateDTO: DeckUpdateDTO): ResultActions {
        val requestBody = objectMapper.writeValueAsString(deckUpdateDTO)
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
