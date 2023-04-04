package com.example.flashcardbackend.flashcard

import com.fasterxml.jackson.databind.ObjectMapper
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
import java.time.LocalDateTime

@WebMvcTest(FlashcardController::class, FlashcardService::class)
class FlashcardControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
) {

    @MockBean
    private lateinit var flashcardRepository: FlashcardRepository

    @Test
    fun `should return a list of flashcards`() {
        val flashcardList = listOf(
            FlashcardListItem(1, 1, "Front content 1"),
            FlashcardListItem(2, 3, "Front content 2"),
            FlashcardListItem(3, 5, "Front content 3"),
        )

        `when`(flashcardRepository.findAll()).thenReturn(flashcardList)

        mockMvc.perform(get("/flashcards"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].frontContentText").value("Front content 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].frontContentText").value("Front content 2"))
            .andExpect(jsonPath("$[2].id").value(3))
            .andExpect(jsonPath("$[2].frontContentText").value("Front content 3"))
    }

    @Test
    fun `should return an empty list if no flashcards exist`() {
        `when`(flashcardRepository.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/flashcards"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `should return a flashcard with the given id`() {
        val flashcard = Flashcard(
            1, 1, "Front text", null, "Back text", null,
            StudyStatus.NEW, null, LocalDateTime.now(),
        )

        `when`(flashcardRepository.findById(1)).thenReturn(flashcard)

        mockMvc.perform(get("/flashcards/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.deckId").value(1))
            .andExpect(jsonPath("$.frontText").value("Front text"))
            .andExpect(jsonPath("$.frontMediaUrl").isEmpty)
            .andExpect(jsonPath("$.backText").value("Back text"))
            .andExpect(jsonPath("$.backMediaUrl").isEmpty)
            .andExpect(jsonPath("$.studyStatus").value("NEW"))
            .andExpect(jsonPath("$.tags").isArray)
            .andExpect(jsonPath("$.tags").isEmpty)
            .andExpect(jsonPath("$.comment").isEmpty)
            .andExpect(jsonPath("$.lastViewed").exists())
    }

    @Test
    fun `should return not found if flashcard with given id does not exist`() {
        `when`(flashcardRepository.findById(1)).thenReturn(null)

        mockMvc.perform(get("/flashcards/1"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Flashcard with id 1 not found."))
    }

    @Test
    fun `should create a flashcard and return 201`() {
        val flashcardCreateDTO = FlashcardCreateDTO(
            deckId = 1,
            front = CardContentCreateDTO(
                text = "",
                mediaUrl = null,
            ),
            back = CardContentCreateDTO(
                text = "Back text",
                mediaUrl = null,
            ),
            comment = null,
        )

        mockMvc.perform(
            post("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ObjectMapper().writeValueAsString(flashcardCreateDTO),
                ),
        )
            .andExpect(status().isCreated)

        verify(flashcardRepository).insert(flashcardCreateDTO.toFlashcardCreate())
    }

    @Test
    fun `should return bad request if request body is invalid`() {
        val flashcardCreateDTO = FlashcardCreateDTO(
            deckId = 0,
            front = CardContentCreateDTO(
                text = "",
                mediaUrl = null,
            ),
            back = null,
            comment = null,
        )

        mockMvc.perform(
            post("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ObjectMapper().writeValueAsString(flashcardCreateDTO),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck ID should be positive number."))
    }

    @Test
    fun `should update a flashcard`() {
        // given
        val flashcardUpdateDTO = FlashcardUpdateDTO(
            id = 1,
            deckId = 1,
            front = CardContentUpdateDTO(
                id = 1,
                text = "Updated front text",
                mediaUrl = null,
                cardContentType = CardContentType.FRONT,
            ),
            back = CardContentUpdateDTO(
                id = 1,
                text = "Updated back text",
                mediaUrl = null,
                cardContentType = CardContentType.BACK,
            ),
            comment = "Updated comment",
        )

        `when`(flashcardRepository.update(flashcardUpdateDTO.toFlashcardUpdate())).thenReturn(1)

        // when
        mockMvc.perform(
            put("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ObjectMapper().writeValueAsString(flashcardUpdateDTO),
                ),
        )
            .andExpect(status().isOk)

        // then
        verify(flashcardRepository).update(flashcardUpdateDTO.toFlashcardUpdate())
    }

    @Test
    fun `put should return bad request if request body is invalid`() {
        // given
        val flashcardUpdateDTO = FlashcardUpdateDTO(
            id = 0,
            deckId = 0,
            front = CardContentUpdateDTO(
                id = 1,
                text = "",
                mediaUrl = null,
                cardContentType = CardContentType.FRONT,
            ),
            back = null,
            comment = null,
        )

        // when
        mockMvc.perform(
            put("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ObjectMapper().writeValueAsString(flashcardUpdateDTO),
                ),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
            .andExpect(jsonPath("$.fieldErrors[1].message").value("The card ID should be positive number."))
            .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck ID should be positive number."))
    }

    @Test
    fun `put should return not found if card with given id does not exist`() {
        val flashcardUpdateDTO = FlashcardUpdateDTO(
            id = 1,
            deckId = 1,
            front = CardContentUpdateDTO(
                id = 1,
                text = "text",
                mediaUrl = null,
                cardContentType = CardContentType.FRONT,
            ),
            back = null,
            comment = "Updated comment",
        )

        `when`(flashcardRepository.update(flashcardUpdateDTO.toFlashcardUpdate())).thenReturn(0)

        // when
        mockMvc.perform(
            put("/flashcards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    ObjectMapper().writeValueAsString(flashcardUpdateDTO),
                ),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Flashcard with id 1 not found."))
    }

    @Test
    fun `should delete a flashcard by id`() {
        val id = 1

        mockMvc.perform(delete("/flashcards/$id"))
            .andExpect(status().isOk)

        verify(flashcardRepository).deleteById(id)
    }
}
