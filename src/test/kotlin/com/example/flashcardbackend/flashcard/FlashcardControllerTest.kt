package com.example.flashcardbackend.flashcard

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
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
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    @MockBean
    private lateinit var flashcardRepository: FlashcardRepository

    private var requestBuilder = FlashcardHttpRequestBuilder(mockMvc)

    @Nested
    @DisplayName("Get all flashcards")
    inner class GetAllFlashcards {

        @Nested
        @DisplayName("When flashcards exist")
        inner class WhenFlashcardsExist {

            @BeforeEach
            fun setup() {
                val flashcardList = listOf(
                    FlashcardListItem(1, 1, "Front content 1"),
                    FlashcardListItem(2, 3, "Front content 2"),
                    FlashcardListItem(3, 5, "Front content 3"),
                )

                given(flashcardRepository.findAll()).willReturn(flashcardList)
            }

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.finAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return found flashcards as JSON")
            fun shouldReturnFoundDecksAsJSON() {
                requestBuilder.finAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return 3 deck")
            fun shouldReturnZeroDeck() {
                requestBuilder.finAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(3)))
            }

            @Test
            @DisplayName("Should return list of flashcards")
            fun shouldReturnListOfFlashcards() {
                requestBuilder.finAll()
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].frontContentText").value("Front content 1"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].frontContentText").value("Front content 2"))
                    .andExpect(jsonPath("$[2].id").value(3))
                    .andExpect(jsonPath("$[2].frontContentText").value("Front content 3"))
            }
        }

        @Nested
        @DisplayName("When no flashcards exist")
        inner class WhenNoFlashcardsExist {

            @BeforeEach
            fun setup() {
                given(flashcardRepository.findAll()).willReturn(emptyList())
            }

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.finAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return 0 deck")
            fun shouldReturnZeroDeck() {
                requestBuilder.finAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(0)))
            }

            @Test
            @DisplayName("Should return empty list")
            fun shouldReturnEmptyList() {
                requestBuilder.finAll()
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$").isEmpty)
            }
        }
    }

    @Nested
    @DisplayName("Get flashcard by ID")
    inner class GetFlashcardById {
        private val id = 1

        @Nested
        @DisplayName("When flashcard with given ID exists")
        inner class WhenFlashcardWithGivenIdExists {

            @BeforeEach
            fun setup() {
                val flashcard = Flashcard(
                    id, 1, "Front text", null, "Back text", null,
                    StudyStatus.NEW, null, LocalDateTime.now(),
                )

                given(flashcardRepository.findById(1)).willReturn(flashcard)
            }

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.finById(id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return flashcard with the given ID")
            fun shouldReturnFlashcardWithGivenId() {
                requestBuilder.finById(id)
                    .andExpect(jsonPath("$.id").value(id))
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
        }

        @Nested
        @DisplayName("When flashcard with given ID does not exist")
        inner class WhenFlashcardWithGivenIdDoesNotExist {

            @BeforeEach
            fun setup() {
                given(flashcardRepository.findById(1)).willReturn(null)
            }

            @Test
            @DisplayName("Should return HTTP status code Not Found")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.finById(id)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error message")
            fun shouldReturnErrorMessage() {
                requestBuilder.finById(id)
                    .andExpect(jsonPath("$.message").value("Flashcard with id 1 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Create flashcard")
    inner class CreateFlashcard {

        @Nested
        @DisplayName("When request body is valid")
        inner class WhenRequestBodyIsValid {

            private val flashcardCreateDTO = FlashcardCreateDTO(
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
            private val requestBody = objectMapper.writeValueAsString(flashcardCreateDTO)
            private val flashcardCreate = flashcardCreateDTO.toFlashcardCreate()

            @Test
            @DisplayName("Should return the HTTP status code CREATED")
            fun shouldReturnHttpStatusCodeCreated() {
                requestBuilder.createFlashcard(requestBody)
                    .andExpect(status().isCreated)
            }

            @Test
            @DisplayName("Should insert a new flashcard")
            fun shouldInsertNewFlashcard() {
                requestBuilder.createFlashcard(requestBody)
                verify(flashcardRepository).insert(flashcardCreate)
            }
        }

        @Nested
        @DisplayName("When request body is invalid")
        inner class WhenRequestBodyIsInvalid {
            private val flashcardCreateDTO = FlashcardCreateDTO(
                deckId = 0,
                front = CardContentCreateDTO(
                    text = "",
                    mediaUrl = null,
                ),
                back = null,
                comment = null,
            )
            private val requestBody = objectMapper.writeValueAsString(flashcardCreateDTO)

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.createFlashcard(requestBody)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return validation error response body")
            fun shouldReturnValidationErrorResponse() {
                requestBuilder.createFlashcard(requestBody)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck ID should be positive number."))
            }
        }
    }

    @Nested
    @DisplayName("Update a flashcard")
    inner class UpdateFlashcard {

        @Nested
        @DisplayName("When the flashcard update request is valid")
        inner class WhenFlashcardUpdateRequestIsValid {
            private val flashcardUpdateDTO = FlashcardUpdateDTO(
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
            private val requestBody = objectMapper.writeValueAsString(flashcardUpdateDTO)
            private val flashcardUpdate = flashcardUpdateDTO.toFlashcardUpdate()

            @BeforeEach
            fun setup() {
                given(flashcardRepository.update(flashcardUpdate)).willReturn(1)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeNoContent() {
                requestBuilder.updateFlashcard(requestBody)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should update the existing flashcard")
            fun shouldUpdateExistingFlashcard() {
                requestBuilder.updateFlashcard(requestBody)
                verify(flashcardRepository).update(flashcardUpdate)
            }
        }

        @Nested
        @DisplayName("When the flashcard update request is invalid")
        inner class WhenFlashcardUpdateRequestIsInvalid {
            private val flashcardUpdateDTO = FlashcardUpdateDTO(
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
            private val requestBody = objectMapper.writeValueAsString(flashcardUpdateDTO)

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.updateFlashcard(requestBody)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return validation error response body")
            fun shouldReturnValidationErrorResponse() {
                requestBuilder.updateFlashcard(requestBody)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'id')].message").value("The card ID should be positive number."))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'deckId')].message").value("The deck ID should be positive number."))
            }
        }

        @Nested
        @DisplayName("When the flashcard with given id does not exist")
        inner class WhenFlashcardWithGivenIdDoesNotExist {
            private val flashcardUpdateDTO = FlashcardUpdateDTO(
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
            private val requestBody = objectMapper.writeValueAsString(flashcardUpdateDTO)
            private val flashcardUpdate = flashcardUpdateDTO.toFlashcardUpdate()

            @BeforeEach
            fun returnRowsAffected() {
                given(flashcardRepository.update(flashcardUpdate)).willReturn(0)
            }

            @Test
            @DisplayName("Should return HTTP status code NOT FOUND")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.updateFlashcard(requestBody)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error response body")
            fun shouldReturnErrorResponse() {
                requestBuilder.updateFlashcard(requestBody)
                    .andExpect(jsonPath("$.message").value("Flashcard with id 1 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Delete a flashcard")
    inner class DeleteFlashcard {
        private val id = 1

        @Nested
        @DisplayName("When the flashcard exists")
        inner class WhenFlashcardExists {

            @BeforeEach
            fun returnRowsAffected() {
                given(flashcardRepository.deleteById(id)).willReturn(1)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.deleteById(id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should delete the flashcard by id")
            fun shouldDeleteFlashcardById() {
                requestBuilder.deleteById(id)
                verify(flashcardRepository).deleteById(id)
            }
        }

        @Nested
        @DisplayName("When the flashcard does not exist")
        inner class WhenFlashcardDoesNotExist {

            @BeforeEach
            fun returnRowsAffected() {
                given(flashcardRepository.deleteById(id)).willReturn(0)
            }

            @Test
            @DisplayName("Should return the HTTP status code NOT FOUND")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.deleteById(id)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error response body")
            fun shouldReturnErrorResponse() {
                requestBuilder.deleteById(id)
                    .andExpect(jsonPath("$.message").value("Flashcard with id $id not found."))
            }
        }
    }
}
