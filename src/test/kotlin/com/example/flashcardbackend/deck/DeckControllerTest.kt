package com.example.flashcardbackend.deck

import com.example.flashcardbackend.flashcard.FlashcardListItem
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(DeckController::class, DeckService::class)
class DeckControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
) {

    @MockBean
    private lateinit var deckRepository: DeckRepository

    @Test
    fun `should return all decks`() {
        val deckListItems = listOf(
            DeckListItem(12, 1, "Deck 1", "Description 1"),
            DeckListItem(13, 1, "Deck 2", "Description 2"),
        )

        `when`(deckRepository.findAll()).thenReturn(deckListItems)

        mockMvc.perform(get("/decks"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """[
                {"id": 12, "deckGroupId": 1, "name": "Deck 1", "description": "Description 1"},
                {"id": 13, "deckGroupId": 1, "name": "Deck 2", "description": "Description 2"}
            ]""",
                ),
            )
    }

    @Test
    fun `should return empty list if no decks`() {
        `when`(deckRepository.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/decks"))
            .andExpect(status().isOk)
            .andExpect(content().json("[]"))
    }

    @Test
    fun `should return deck by id`() {
        val deck = Deck(
            12,
            1,
            "Deck 1",
            "Description 1",
            listOf(
            FlashcardListItem(1, 1, "Front Content 1"),
            FlashcardListItem(2, 3, "Front Content 2"),
            ),
        )

        `when`(deckRepository.findById(1)).thenReturn(deck)

        mockMvc.perform(get("/decks/1"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """{
                "id": 12,
                "deckGroupId": 1,
                "name": "Deck 1",
                "description": "Description 1",
                "flashcards": [
                    {"id": 1, "frontContentText": "Front Content 1"},
                    {"id": 2, "frontContentText": "Front Content 2"}
                ]
            }""",
                ),
            )
    }

    @Test
    fun `should return not found if deck with given id does not exist`() {
        val id = 12

        `when`(deckRepository.findById(id)).thenReturn(null)

        mockMvc.perform(get("/decks/$id"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Deck with id $id not found."))
    }

    @Test
    fun `should create a new deck`() {
        val requestBody = """{"deckGroupId": 1, "name": "Deck 1", "description": "Description 1"}"""
        val deckCreate = DeckCreate(1, "Deck 1", "Description 1")

        mockMvc.perform(
            post("/decks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody),
        )
            .andExpect(status().isCreated)

        verify(deckRepository).insert(deckCreate)
    }

    @Test
    fun `should return bad request if deck create dto is invalid`() {
        val requestBody = """{"deckGroupId": 0, "name": "","description": ""}"""

        mockMvc.perform(
            post("/decks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
            .andExpect(jsonPath("$.fieldErrors[1].message").value("The deck group ID should be positive number."))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck name is required."))
    }

    @Test
    fun `should update an existing deck`() {
        val requestBody = """{"id": 15, "deckGroupId": 1, "name": "Deck 1", "description": "Description 1"}"""
        val deckUpdate = DeckUpdate(15, 1, "Deck 1", "Description 1")

        `when`(deckRepository.update(deckUpdate)).thenReturn(1)

        mockMvc.perform(
            put("/decks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody),
        )
            .andExpect(status().isOk)

        verify(deckRepository).update(deckUpdate)
    }

    @Test
    fun `should return bad request if deck update dto is invalid`() {
        val requestBody = """{"id": 0, "deckGroupId": 0, "name": "", "description": ""}"""

        mockMvc.perform(
            put("/decks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors[1].message").value("The deck ID should be positive number."))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck group ID should be positive number."))
            .andExpect(jsonPath("$.fieldErrors[2].message").value("The deck name is required."))
    }

    @Test
    fun `put should return not found if deck with given id does not exist`() {
        val requestBody = """{"id": 15, "deckGroupId": 1, "name": "Deck 1", "description": "Description 1"}"""
        val deckUpdate = DeckUpdate(15, 1, "Deck 1", "Description 1")

        `when`(deckRepository.update(deckUpdate)).thenReturn(0)

        mockMvc.perform(
            put("/decks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Deck with id 15 not found."))
    }

    @Test
    fun `should delete a deck group by id`() {
        val id = 13

        mockMvc.perform(delete("/decks/$id"))
            .andExpect(status().isOk)

        verify(deckRepository).deleteById(id)
    }
}
