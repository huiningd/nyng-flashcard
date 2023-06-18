package com.example.flashcardbackend.requestbuilder

import com.example.flashcardbackend.deckgroup.DeckGroupCreateDTO
import com.example.flashcardbackend.deckgroup.DeckGroupUpdateDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class DeckGroupHttpRequestBuilder(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {

    @Throws(Exception::class)
    fun findAll(): ResultActions {
        return mockMvc.perform(get("/deckgroups"))
    }

    @Throws(Exception::class)
    fun findById(id: Int): ResultActions {
        return mockMvc.perform(get("/deckgroups/$id"))
    }

    @Throws(Exception::class)
    fun createDeckGroup(deckGroupCreateDTO: DeckGroupCreateDTO): ResultActions {
        val requestBody = objectMapper.writeValueAsString(deckGroupCreateDTO)
        return mockMvc.perform(
            post("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun updateDeckGroup(deckGroupUpdateDTO: DeckGroupUpdateDTO): ResultActions {
        val requestBody = objectMapper.writeValueAsString(deckGroupUpdateDTO)
        return mockMvc.perform(
            put("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun deleteById(id: Int): ResultActions {
        return mockMvc.perform(delete("/deckgroups/$id"))
    }
}
