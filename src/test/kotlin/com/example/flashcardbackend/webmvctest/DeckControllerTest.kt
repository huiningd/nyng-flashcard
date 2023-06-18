package com.example.flashcardbackend.webmvctest

import com.example.flashcardbackend.deck.*
import com.example.flashcardbackend.flashcard.FlashcardListItem
import com.example.flashcardbackend.requestbuilder.DeckHttpRequestBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
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

@WebMvcTest(DeckController::class, DeckService::class)
@DisplayName("Tests for CRUD operations of decks")
class DeckControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    @MockBean
    private lateinit var deckRepository: DeckRepository

    private var requestBuilder = DeckHttpRequestBuilder(mockMvc, objectMapper)

    @Nested
    @DisplayName("Find all decks")
    inner class FindAllDecks {

        @Nested
        @DisplayName("When no decks are found")
        inner class WhenNoDecksAreFound {

            @BeforeEach
            fun returnZeroDecks() {
                given(deckRepository.findAll()).willReturn(emptyList())
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.findAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return found decks as JSON")
            fun shouldReturnFoundDecksAsJSON() {
                requestBuilder.findAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return zero deck")
            fun shouldReturnZeroDeck() {
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), hasSize<Int>(0)))
            }
        }

        @Nested
        @DisplayName("When two decks are found")
        inner class WhenTwoDecksAreFound {

            @BeforeEach
            fun returnTwoDecks() {
                val deckListItems = listOf(
                    DeckListItem(12, 1, "Deck 1", "Description 1"),
                    DeckListItem(13, 1, "Deck 2", "Description 2"),
                )

                given(deckRepository.findAll()).willReturn(deckListItems)
            }

            @Test
            @DisplayName("Should return HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.findAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return found decks as JSON")
            fun shouldReturnFoundDecksAsJSON() {
                requestBuilder.findAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return two decks")
            fun shouldReturnTwoDecks() {
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), hasSize<Int>(2)))
            }

            @Test
            @DisplayName("Should return the information of the found decks")
            fun shouldReturnCorrectInformation() {
                requestBuilder.findAll()
                    .andExpect(jsonPath("[0].id", equalTo(12)))
                    .andExpect(jsonPath("[0].deckGroupId", equalTo(1)))
                    .andExpect(jsonPath("[0].name", equalTo("Deck 1")))
                    .andExpect(jsonPath("[0].description", equalTo("Description 1")))
                    .andExpect(jsonPath("[1].id", equalTo(13)))
                    .andExpect(jsonPath("[1].deckGroupId", equalTo(1)))
                    .andExpect(jsonPath("[1].name", equalTo("Deck 2")))
                    .andExpect(jsonPath("[1].description", equalTo("Description 2")))
            }
        }
    }

    @Nested
    @DisplayName("Find deck by id")
    inner class FindDeckById {
        private val deckId = 1

        @Nested
        @DisplayName("When deck with id is found")
        inner class WhenDeckIsFound {
            private val expectedDeck = Deck(
                12,
                1,
                "Deck 1",
                "Description 1",
                listOf(
                    FlashcardListItem(1, 1, "Front Content 1"),
                    FlashcardListItem(2, 3, "Front Content 2"),
                ),
            )

            @BeforeEach
            fun returnDeck() {
                given(deckRepository.findById(deckId)).willReturn(expectedDeck)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOK() {
                requestBuilder.findById(deckId)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return deck as JSON")
            fun shouldReturnDeckAsJSON() {
                requestBuilder.findById(deckId)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return the expected deck")
            fun shouldReturnExpectedDeck() {
                requestBuilder.findById(deckId)
                    .andExpect(
                        content().json(
                            objectMapper.writeValueAsString(expectedDeck.toDeckDTO()),
                        ),
                    )
            }
        }

        @Nested
        @DisplayName("When deck with id is not found")
        inner class WhenDeckIsNotFound {
            @BeforeEach
            fun returnNullDeck() {
                given(deckRepository.findById(deckId)).willReturn(null)
            }

            @Test
            @DisplayName("Should return the HTTP status code NOT FOUND")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.findById(deckId)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return not found message")
            fun shouldReturnNotFoundMessage() {
                requestBuilder.findById(deckId)
                    .andExpect(jsonPath("$.message").value("Deck with id $deckId not found."))
            }
        }
    }

    @Nested
    @DisplayName("Create a new deck")
    inner class CreateDeck {

        @Nested
        @DisplayName("When the deck create request is valid")
        inner class WhenDeckCreateRequestIsValid {
            private val deckCreateDTO = DeckCreateDTO(1, "Deck 1", "Description 1")
            private val deckCreate = deckCreateDTO.toDeckCreate()

            @Test
            @DisplayName("Should return the HTTP status code CREATED")
            fun shouldReturnHttpStatusCodeCreated() {
                requestBuilder.createDeck(deckCreateDTO)
                    .andExpect(status().isCreated)
            }

            @Test
            @DisplayName("Should insert a new deck")
            fun shouldInsertNewDeck() {
                requestBuilder.createDeck(deckCreateDTO)
                verify(deckRepository).insert(deckCreate)
            }
        }

        @Nested
        @DisplayName("When the deck create request is invalid")
        inner class WhenDeckCreateRequestIsInvalid {
            private val deckCreateDTO = DeckCreateDTO(0, "", "")

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.createDeck(deckCreateDTO)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return validation error response body")
            fun shouldReturnValidationErrorResponse() {
                requestBuilder.createDeck(deckCreateDTO)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'deckGroupId')].message").value("The deck group ID should be positive number."))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'name')].message").value("The deck name is required."))
            }
        }
    }

    @Nested
    @DisplayName("Update a deck")
    inner class UpdateDeck {

        @Nested
        @DisplayName("When the deck update request is valid")
        inner class WhenDeckUpdateRequestIsValid {
            private val deckUpdateDTO = DeckUpdateDTO(1, 1, "Deck 1", "Description 1")
            private val deckUpdate = deckUpdateDTO.toDeckUpdate()

            @BeforeEach
            fun returnRowsAffected() {
                given(deckRepository.update(deckUpdate)).willReturn(1)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeNoContent() {
                requestBuilder.updateDeck(deckUpdateDTO)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should update the existing deck")
            fun shouldUpdateExistingDeck() {
                requestBuilder.updateDeck(deckUpdateDTO)
                verify(deckRepository).update(deckUpdate)
            }
        }

        @Nested
        @DisplayName("When the deck update request is invalid")
        inner class WhenDeckUpdateRequestIsInvalid {
            private val deckUpdateDTO = DeckUpdateDTO(0, 0, "", "")

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.updateDeck(deckUpdateDTO)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return validation error response body")
            fun shouldReturnValidationErrorResponse() {
                requestBuilder.updateDeck(deckUpdateDTO)
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'id')].message").value("The deck ID should be positive number."))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'deckGroupId')].message").value("The deck group ID should be positive number."))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'name')].message").value("The deck name is required."))
            }
        }

        @Nested
        @DisplayName("When the deck with given id does not exist")
        inner class WhenDeckWithGivenIdDoesNotExist {
            private val deckUpdateDTO = DeckUpdateDTO(15, 1, "Deck 1", "Description 1")
            private val deckUpdate = deckUpdateDTO.toDeckUpdate()

            @BeforeEach
            fun returnRowsAffected() {
                given(deckRepository.update(deckUpdate)).willReturn(0)
            }

            @Test
            @DisplayName("Should return the HTTP status code NOT FOUND")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.updateDeck(deckUpdateDTO)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error response body")
            fun shouldReturnErrorResponse() {
                requestBuilder.updateDeck(deckUpdateDTO)
                    .andExpect(jsonPath("$.message").value("Deck with id 15 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Delete a deck")
    inner class DeleteDeck {
        private val id = 13

        @Nested
        @DisplayName("When the deck exists")
        inner class WhenDeckExists {

            @BeforeEach
            fun returnRowsAffected() {
                given(deckRepository.deleteById(id)).willReturn(1)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.deleteById(id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should delete the deck by id")
            fun shouldDeleteDeckById() {
                requestBuilder.deleteById(id)
                verify(deckRepository).deleteById(id)
            }
        }

        @Nested
        @DisplayName("When the deck does not exist")
        inner class WhenDeckDoesNotExist {

            @BeforeEach
            fun returnRowsAffected() {
                given(deckRepository.deleteById(id)).willReturn(0)
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
                    .andExpect(jsonPath("$.message").value("Deck with id $id not found."))
            }
        }
    }
}
