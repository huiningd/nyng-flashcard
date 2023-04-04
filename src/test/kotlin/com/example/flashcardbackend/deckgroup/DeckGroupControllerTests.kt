package com.example.flashcardbackend.deckgroup

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DeckGroupController::class, DeckGroupService::class)
class DeckGroupControllerTests(
    @Autowired
    private val mockMvc: MockMvc,
) {

    @MockBean
    private lateinit var deckGroupRepository: DeckGroupRepository

    @Test
    fun `should return all deck groups`() {
        val deckGroups = listOf(
            DeckGroupListItem(1, "Deck Group 1", "description 1"),
            DeckGroupListItem(2, "Deck Group 2", "description 2"),
        )

        `when`(deckGroupRepository.findAll()).thenReturn(deckGroups)

        mockMvc.perform(get("/deckgroups"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """[
                    {"id": 1, "name": "Deck Group 1", "description": "description 1"},
                    {"id": 2, "name": "Deck Group 2", "description": "description 2"}
                ]""",
                ),
            )
    }

    @Test
    fun `should return empty list if no deck groups`() {
        `when`(deckGroupRepository.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/deckgroups"))
            .andExpect(status().isOk)
            .andExpect(content().json("[]"))
    }

    @Test
    fun `should return deck group with given id`() {
        val id = 12
        val expectedDeckGroup = DeckGroup(id = id, name = "Sample deck group", description = "Sample description", decks = emptyList())

        `when`(deckGroupRepository.findById(ArgumentMatchers.eq(12))).thenReturn(expectedDeckGroup)

        mockMvc.perform(get("/deckgroups/$id").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Sample deck group"))
            .andExpect(jsonPath("$.description").value("Sample description"))
            .andExpect(jsonPath("$.decks").isEmpty)
    }

    @Test
    fun `should return not found if deck group with given id does not exist`() {
        val id = 12

        `when`(deckGroupRepository.findById(id)).thenReturn(null)

        mockMvc.perform(get("/deckgroups/$id"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Deck group with id $id not found."))
    }

    @Test
    fun `should create a new deck group`() {
        val requestBody = """{"name": "Finnished", "description": "Learn Finnish with Finnished"}"""
        val deckGroupCreate = DeckGroupCreate("Finnished", "Learn Finnish with Finnished")

        mockMvc.perform(
            post("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
            .andExpect(status().isCreated)

        verify(deckGroupRepository).insert(deckGroupCreate)
    }

    @Test
    fun `post should return bad request if deck group name is blank`() {
        val requestBody = """{"name": "", "description": "A new deck group."}"""

        mockMvc.perform(
            post("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.message").value(containsString("Validation failed")))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck group name is required."))
    }

    @Test
    fun `should update an existing deck group`() {
        val requestBody = """{"id": 1, "name": "Updated Deck Group", "description": "An updated deck group."}"""
        val deckGroupUpdate = DeckGroupUpdate(1, "Updated Deck Group", "An updated deck group.")

        `when`(deckGroupRepository.update(deckGroupUpdate)).thenReturn(1)

        mockMvc.perform(
            put("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
            .andExpect(status().isOk)

        verify(deckGroupRepository).update(deckGroupUpdate)
    }

    @Test
    fun `put should return bad request if deck group name is blank`() {
        val requestBody = """{"id": 1, "name": "", "description": "An updated deck group."}"""

        mockMvc.perform(
            put("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
            .andExpect(status().isBadRequest).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.message").value(containsString("Validation failed")))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck group name is required."))
    }

    @Test
    fun `put should return not found if deck group with given id does not exist`() {
        val requestBody = """{"id": 17, "name": "Deck Group 1", "description": "Description 1"}"""
        val deckGroupUpdate = DeckGroupUpdate(17, "Deck Group 1", "Description 1")

        `when`(deckGroupRepository.update(deckGroupUpdate)).thenReturn(0)

        mockMvc.perform(
            put("/deckgroups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Deck group with id 17 not found."))
    }

    @Test
    fun `should delete a deck group by id`() {
        val id = 12

        mockMvc.perform(delete("/deckgroups/$id"))
            .andExpect(status().isOk)

        verify(deckGroupRepository).deleteById(id)
    }
}
