package com.example.flashcardbackend.deckgroup

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class DeckGroupHttpRequestBuilder(private val mockMvc: MockMvc) {

    @Throws(Exception::class)
    fun finAll(): ResultActions {
        return mockMvc.perform(get("/deckgroups"))
    }

    @Throws(Exception::class)
    fun findById(id: Int): ResultActions {
        return mockMvc.perform(get("/deckgroups/$id"))
    }

    @Throws(Exception::class)
    fun createDeckGroup(requestBody: String): ResultActions {
        return mockMvc.perform(
            post("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun updateDeckGroup(requestBody: String): ResultActions {
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
