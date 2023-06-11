package com.example.flashcardbackend.integrationtest

import com.example.flashcardbackend.deck.*
import com.example.flashcardbackend.requestbuilder.DeckHttpRequestBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.db.api.Assertions
import org.assertj.db.type.Table
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
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("integrationTest")
@AutoConfigureMockMvc
@Sql(
    value = [
        "classpath:db/clear-database.sql",
        "classpath:db/init-database.sql",
    ],
)
class DeckIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val dataSource: DataSource,
) {

    private var requestBuilder = DeckHttpRequestBuilder(mockMvc)

    private val DECK_TABLE = Table(dataSource, "deck")

    @Nested
    @DisplayName("Find all decks")
    inner class FindAllDecks {

        @Nested
        @DisplayName("When no decks are found")
        @Sql(statements = ["DELETE FROM deck;"])
        inner class WhenNoDecksAreFound {

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
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(0)))
            }
        }

        @Nested
        @DisplayName("When three decks are found")
        inner class WhenDecksAreFound {

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
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(3)))
            }

            @Test
            @DisplayName("Should return the information of the found decks")
            fun shouldReturnCorrectInformation() {
                requestBuilder.findAll()
                    .andExpect(
                        content().json(objectMapper.writeValueAsString(decks)),
                    )
            }
        }
    }

    @Nested
    @DisplayName("Find deck by id")
    inner class FindDeckById {

        @Nested
        @DisplayName("When deck with id is found")
        inner class WhenDeckIsFound {
            private val deckId = 1

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
                            objectMapper.writeValueAsString(deckSpanish),
                        ),
                    )
            }
        }

        @Nested
        @DisplayName("When deck with id is not found")
        inner class WhenDeckIsNotFound {
            private val deckId = 50

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
            private val deckCreateDTO = DeckCreateDTO(1, "Finnish words 5", "A deck for learning Finnish vocabulary.")
            private val requestBody = objectMapper.writeValueAsString(deckCreateDTO)

            @Test
            @DisplayName("Should return the HTTP status code CREATED")
            fun shouldReturnHttpStatusCodeCreated() {
                requestBuilder.createDeck(requestBody)
                    .andExpect(status().isCreated)
            }

            @Test
            @DisplayName("Should insert the deck into the database")
            fun shouldInsertNewDeckIntoTheDatabase() {
                requestBuilder.createDeck(requestBody)
                val expectedDeckCount = 4

                // Check row count
                Assertions.assertThat(DECK_TABLE).hasNumberOfRows(expectedDeckCount)
            }

            @Test
            @DisplayName("Should insert the deck into the database with correct values")
            fun shouldInsertNewDeckIntoTheDatabaseWithCorrectValues() {
                requestBuilder.createDeck(requestBody)
                val expectedDeckIndex = 3

                // Check row values
                Assertions.assertThat(DECK_TABLE).row(expectedDeckIndex)
                    .value("group_id").`as`("deckGroupId")
                    .isEqualTo(deckCreateDTO.deckGroupId)
                    .value("deck_name").`as`("name")
                    .isEqualTo(deckCreateDTO.name)
                    .value("description").`as`("description")
                    .isEqualTo(deckCreateDTO.description)
            }

            @Test
            @DisplayName("The new deck should be found when fetching")
            fun shouldInsertNewDeck() {
                requestBuilder.createDeck(requestBody)
                // Verify the last deck is the newly created one
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(4)))
                    .andExpect(jsonPath("$[3].deckGroupId").value(deckCreateDTO.deckGroupId))
                    .andExpect(jsonPath("$[3].name").value(deckCreateDTO.name))
                    .andExpect(jsonPath("$[3].description").value(deckCreateDTO.description))
            }
        }

        @Nested
        @DisplayName("When the deck create request is invalid")
        inner class WhenDeckCreateRequestIsInvalid {
            private val requestBody = """{"deckGroupId": 0, "name": "","description": ""}"""

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.createDeck(requestBody)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return validation error response body")
            fun shouldReturnValidationErrorResponse() {
                requestBuilder.createDeck(requestBody)
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
            private val deckUpdateDTO = DeckUpdateDTO(1, 1, "Deck updated", "Description updated")
            private val requestBody = objectMapper.writeValueAsString(deckUpdateDTO)
            private val rowIndexOfDeck = 0

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeNoContent() {
                requestBuilder.updateDeck(requestBody)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should update the deck in the database")
            fun shouldUpdateDeckIntoTheDatabase() {
                requestBuilder.updateDeck(requestBody)

                // Verify data is updated in db
                Assertions.assertThat(DECK_TABLE).row(rowIndexOfDeck)
                    .value("group_id").`as`("deckGroupId")
                    .isEqualTo(deckUpdateDTO.deckGroupId)
                    .value("deck_name").`as`("name")
                    .isEqualTo(deckUpdateDTO.name)
                    .value("description").`as`("description")
                    .isEqualTo(deckUpdateDTO.description)
            }

            @Test
            @DisplayName("The updated deck should be found when fetching by ID")
            fun shouldUpdateExistingDeck() {
                requestBuilder.updateDeck(requestBody)
                // Verify the deck is updated
                requestBuilder.findById(deckUpdateDTO.id)
                    .andExpect(jsonPath("$.deckGroupId").value(deckUpdateDTO.deckGroupId))
                    .andExpect(jsonPath("$.name").value(deckUpdateDTO.name))
                    .andExpect(jsonPath("$.description").value(deckUpdateDTO.description))
            }
        }

        @Nested
        @DisplayName("When the deck update request is invalid")
        inner class WhenDeckUpdateRequestIsInvalid {
            private val requestBody = """{"id": 0, "deckGroupId": 0, "name": "", "description": ""}"""

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.updateDeck(requestBody)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return validation error response body")
            fun shouldReturnValidationErrorResponse() {
                requestBuilder.updateDeck(requestBody)
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'id')].message").value("The deck ID should be positive number."))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'deckGroupId')].message").value("The deck group ID should be positive number."))
                    .andExpect(jsonPath("$.fieldErrors[?(@.property == 'name')].message").value("The deck name is required."))
            }
        }

        @Nested
        @DisplayName("When the deck with given id does not exist")
        inner class WhenDeckWithGivenIdDoesNotExist {
            private val requestBody = """{"id": 15, "deckGroupId": 1, "name": "Deck 1", "description": "Description 1"}"""

            @Test
            @DisplayName("Should return the HTTP status code NOT FOUND")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.updateDeck(requestBody)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error response body")
            fun shouldReturnErrorResponse() {
                requestBuilder.updateDeck(requestBody)
                    .andExpect(jsonPath("$.message").value("Deck with id 15 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Delete a deck")
    inner class DeleteDeck {

        @Nested
        @DisplayName("When the deck exists")
        inner class WhenDeckExists {
            private val id = 2

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
                // Verify it is deleted
                requestBuilder.findById(id)
                    .andExpect(status().isNotFound)
            }
        }

        @Nested
        @DisplayName("When the deck does not exist")
        inner class WhenDeckDoesNotExist {
            private val id = 29

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
