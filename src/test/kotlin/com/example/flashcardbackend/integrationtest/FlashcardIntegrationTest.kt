package com.example.flashcardbackend.integrationtest

import com.example.flashcardbackend.flashcard.*
import com.example.flashcardbackend.requestbuilder.FlashcardHttpRequestBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@ActiveProfiles("integrationTest")
@AutoConfigureMockMvc
@Sql(
    value = [
        "classpath:db/clear-database.sql",
        "classpath:db/init-database.sql",
    ],
)
class FlashcardIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    private var requestBuilder = FlashcardHttpRequestBuilder(mockMvc)

    @Nested
    @DisplayName("Get all flashcards")
    inner class GetAllFlashcards {

        @Nested
        @DisplayName("When flashcards exist")
        inner class WhenFlashcardsExist {

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.findAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return found flashcards as JSON")
            fun shouldReturnFoundDecksAsJSON() {
                requestBuilder.findAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return 3 deck")
            fun shouldReturnZeroDeck() {
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(4)))
            }

            @Test
            @DisplayName("Should return list of flashcards")
            fun shouldReturnListOfFlashcards() {
                requestBuilder.findAll()
                    .andExpect(
                        content().json(objectMapper.writeValueAsString(flashcards)),
                    )
            }
        }

        @Nested
        @DisplayName("When no flashcards exist")
        @Sql(statements = ["DELETE FROM flashcard;"])
        inner class WhenNoFlashcardsExist {

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.findAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return 0 deck")
            fun shouldReturnZeroDeck() {
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(0)))
            }

            @Test
            @DisplayName("Should return empty list")
            fun shouldReturnEmptyList() {
                requestBuilder.findAll()
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$").isEmpty)
            }
        }
    }

    @Nested
    @DisplayName("Get flashcard by ID")
    inner class GetFlashcardById {

        @Nested
        @DisplayName("When flashcard with given ID exists")
        inner class WhenFlashcardWithGivenIdExists {
            private val id = 1

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.findById(id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return flashcard with the given ID")
            fun shouldReturnFlashcardWithGivenId() {
                requestBuilder.findById(id)
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.deckId").value(flashcardManzana.deckId))
                    .andExpect(jsonPath("$.frontText").value(flashcardManzana.frontText))
                    .andExpect(jsonPath("$.frontMediaUrl").value(flashcardManzana.frontMediaUrl))
                    .andExpect(jsonPath("$.backText").value(flashcardManzana.backText))
                    .andExpect(jsonPath("$.backMediaUrl").value(flashcardManzana.backMediaUrl))
                    .andExpect(jsonPath("$.studyStatus").value(flashcardManzana.studyStatus.toString()))
                    //.andExpect(jsonPath("$.tags").value(flashcardManzana.tags))
                    .andExpect(jsonPath("$.comment").value(flashcardManzana.comment))
                    .andExpect(jsonPath("$.lastViewed").exists())
            }
        }

        @Nested
        @DisplayName("When flashcard with given ID does not exist")
        inner class WhenFlashcardWithGivenIdDoesNotExist {
            private val id = 50

            @Test
            @DisplayName("Should return HTTP status code Not Found")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.findById(id)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error message")
            fun shouldReturnErrorMessage() {
                requestBuilder.findById(id)
                    .andExpect(jsonPath("$.message").value("Flashcard with id $id not found."))
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
                    text = "What is the Finnish word for rabbit",
                    mediaUrl = null,
                ),
                back = CardContentCreateDTO(
                    text = "kani, jänis",
                    mediaUrl = null,
                ),
                comment = null,
            )
            private val requestBody = objectMapper.writeValueAsString(flashcardCreateDTO)

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

                // Verify the last flashcard is the newly created one
                val expectedId = 5
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(5)))
                    .andExpect(jsonPath("$[4].id").value(expectedId))
                    .andExpect(jsonPath("$[4].frontContentText").value(flashcardCreateDTO.front.text))
            }

            @Test
            @DisplayName("Should insert the flashcard information correctly")
            fun shouldInsertInformationOfNewFlashcard() {
                requestBuilder.createFlashcard(requestBody)

                // Verify the flashcard content
                val expectedId = 5
                requestBuilder.findById(expectedId)
                    .andExpect(jsonPath("$.deckId").value(flashcardCreateDTO.deckId))
                    .andExpect(jsonPath("$.frontText").value(flashcardCreateDTO.front.text))
                    .andExpect(jsonPath("$.frontMediaUrl").value(flashcardCreateDTO.front.mediaUrl))
                    .andExpect(jsonPath("$.backText").value(flashcardCreateDTO.back?.text))
                    .andExpect(jsonPath("$.backMediaUrl").value(flashcardCreateDTO.back?.mediaUrl))
                    .andExpect(jsonPath("$.studyStatus").value("NEW"))
                    .andExpect(jsonPath("$.tags").isEmpty)
                    .andExpect(jsonPath("$.comment").value(flashcardCreateDTO.comment))
                    .andExpect(jsonPath("$.lastViewed").exists())
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
                    id = 2,
                    text = "Updated back text",
                    mediaUrl = null,
                    cardContentType = CardContentType.BACK,
                ),
                comment = "Updated comment",
                cardTypeId = 1,
            )
            private val requestBody = objectMapper.writeValueAsString(flashcardUpdateDTO)

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
                // Verify it is updated
                requestBuilder.findById(flashcardUpdateDTO.id)
                    .andExpect(jsonPath("$.deckId").value(flashcardUpdateDTO.deckId))
                    .andExpect(jsonPath("$.frontText").value(flashcardUpdateDTO.front?.text))
                    .andExpect(jsonPath("$.frontMediaUrl").value(flashcardUpdateDTO.front?.mediaUrl))
                    .andExpect(jsonPath("$.backText").value(flashcardUpdateDTO.back?.text))
                    .andExpect(jsonPath("$.backMediaUrl").value(flashcardUpdateDTO.back?.mediaUrl))
                    .andExpect(jsonPath("$.studyStatus").value("NEW"))
                    .andExpect(jsonPath("$.tags").isEmpty)
                    // .andExpect(jsonPath("$.comment").value(flashcardUpdateDTO.comment)) // not implemented yet
                    .andExpect(jsonPath("$.lastViewed").exists())
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
                cardTypeId = 1,
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
                id = 50,
                deckId = 1,
                front = CardContentUpdateDTO(
                    id = 1,
                    text = "text",
                    mediaUrl = null,
                    cardContentType = CardContentType.FRONT,
                ),
                back = null,
                comment = "Updated comment",
                cardTypeId = 1,
            )
            private val requestBody = objectMapper.writeValueAsString(flashcardUpdateDTO)

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
                    .andExpect(jsonPath("$.message").value("Flashcard with id 50 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Delete a flashcard")
    inner class DeleteFlashcard {

        @Nested
        @DisplayName("When the flashcard exists")
        inner class WhenFlashcardExists {
            private val id = 3

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
                // Verify it is deleted
                requestBuilder.findById(id)
                    .andExpect(status().isNotFound)
            }
        }

        @Nested
        @DisplayName("When the flashcard does not exist")
        inner class WhenFlashcardDoesNotExist {

            private val id = 60

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
