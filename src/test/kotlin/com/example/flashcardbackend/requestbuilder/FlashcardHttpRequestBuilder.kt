package com.example.flashcardbackend.requestbuilder

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class FlashcardHttpRequestBuilder(private val mockMvc: MockMvc) {

    @Throws(Exception::class)
    fun findAll(): ResultActions {
        return mockMvc.perform(get("/flashcards"))
    }

    @Throws(Exception::class)
    fun findById(id: Int): ResultActions {
        return mockMvc.perform(get("/flashcards/$id"))
    }

    @Throws(Exception::class)
    fun createFlashcard(requestBody: String): ResultActions {
        return mockMvc.perform(
            post("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun updateFlashcard(requestBody: String): ResultActions {
        return mockMvc.perform(
            put("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
    }

    @Throws(Exception::class)
    fun deleteById(id: Int): ResultActions {
        return mockMvc.perform(delete("/flashcards/$id"))
    }
}
