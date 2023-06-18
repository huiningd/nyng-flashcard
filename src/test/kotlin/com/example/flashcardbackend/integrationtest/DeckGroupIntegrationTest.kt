package com.example.flashcardbackend.integrationtest

import com.example.flashcardbackend.deckgroup.*
import com.example.flashcardbackend.requestbuilder.DeckGroupHttpRequestBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.Table
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
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
class DeckGroupIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val dataSource: DataSource,
) {

    private val requestBuilder = DeckGroupHttpRequestBuilder(mockMvc, objectMapper)

    private val DECK_GROUP_TABLE = Table(dataSource, "deck_group")

    @Nested
    @DisplayName("Find all deck groups")
    inner class FindAllDeckGroups {

        @Nested
        @DisplayName("When deck groups exist")
        inner class WhenDeckGroupsExist {

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.findAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return found deck groups as JSON")
            fun shouldReturnFoundDeckGroupsAsJSON() {
                requestBuilder.findAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return 2 deck groups")
            fun shouldReturnTwoDeckGroups() {
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(3)))
            }

            @Test
            @DisplayName("Should return information of found deck groups")
            fun shouldReturnFoundDeckGroupsInfo() {
                requestBuilder.findAll()
                    .andExpect(
                        content().json(objectMapper.writeValueAsString(deckGroups)),
                    )
            }
        }

        @Nested
        @DisplayName("When no deck groups exist")
        @Sql(statements = ["DELETE FROM deck_group;"])
        inner class WhenNoDeckGroupsExist {

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.findAll()
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return found deck groups as JSON")
            fun shouldReturnFoundDeckGroupsAsJSON() {
                requestBuilder.findAll()
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return 0 deck")
            fun shouldReturnZeroDeck() {
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(0)))
            }

            @Test
            @DisplayName("Should return empty list as JSON")
            fun shouldReturnEmptyListAsJson() {
                requestBuilder.findAll()
                    .andExpect(content().json("[]"))
            }
        }
    }

    @Nested
    @DisplayName("Find deck group by ID")
    inner class FindDeckGroupById {

        @Nested
        @DisplayName("When deck group with given ID exists")
        inner class WhenDeckGroupExists {

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.findById(deckGroupLang.id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return deck groups as JSON")
            fun shouldReturnDeckGroupsAsJSON() {
                requestBuilder.findById(deckGroupLang.id)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return the information of found deck group with decks")
            fun shouldReturnFoundDeckGroupWithDecks() {
                requestBuilder.findById(deckGroupLang.id)
                    .andExpect(
                        content().json(objectMapper.writeValueAsString(deckGroupLang)),
                    )
            }

            @Test
            @DisplayName("Should return the information of found deck group with empty deck")
            fun shouldReturnFoundDeckGroupWithEmptyDeck() {
                requestBuilder.findById(deckGroupMovies.id)
                    .andExpect(jsonPath("$.id").value(deckGroupMovies.id))
                    .andExpect(jsonPath("$.name").value(deckGroupMovies.name))
                    .andExpect(jsonPath("$.description").value(deckGroupMovies.description))
                    .andExpect(jsonPath("$.decks").isEmpty)
            }
        }

        @Nested
        @DisplayName("When deck group with given ID does not exist")
        inner class WhenDeckGroupDoesNotExist {
            private val id = 12

            @Test
            @DisplayName("Should return the HTTP status code NOT FOUND")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.findById(id)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return a JSON error message")
            fun shouldReturnJsonErrorMessage() {
                requestBuilder.findById(id)
                    .andExpect(jsonPath("$.message").value("Deck group with id $id not found."))
            }
        }
    }

    @Nested
    @DisplayName("Create deck group")
    inner class CreateDeckGroup {
        @Nested
        @DisplayName("When deck group is created successfully")
        inner class WhenDeckGroupIsCreatedSuccessfully {
            private val deckGroupCreateDTO = DeckGroupCreateDTO("Finnished", "Learn Finnish with Finnished")
            private val deckGroupCreate = deckGroupCreateDTO.toDeckGroupCreate()

            @Test
            @DisplayName("Should return the HTTP status code CREATED")
            fun shouldReturnHttpStatusCodeCreated() {
                requestBuilder.createDeckGroup(deckGroupCreateDTO)
                    .andExpect(status().isCreated)
            }

            @Test
            @DisplayName("Should insert the deck group into the database")
            fun shouldInsertDeckGroupIntoTheDatabase() {
                requestBuilder.createDeckGroup(deckGroupCreateDTO)
                val expectedDeckGroupCount = 4

                // Check row count
                assertThat(DECK_GROUP_TABLE).hasNumberOfRows(expectedDeckGroupCount)
            }

            @Test
            @DisplayName("Should insert the deck group into the database with correct values")
            fun shouldInsertDeckGroupIntoTheDatabaseWithCorrectValues() {
                requestBuilder.createDeckGroup(deckGroupCreateDTO)
                val expectedDeckGroupIndex = 3

                // Check row values
                assertThat(DECK_GROUP_TABLE).row(expectedDeckGroupIndex)
                    .value("group_name").`as`("name")
                    .isEqualTo(deckGroupCreate.name)
                    .value("description").`as`("description")
                    .isEqualTo(deckGroupCreate.description)
            }

            @Test
            @DisplayName("The inserted new deck group is found when fetching")
            fun shouldInsertDeckGroupInTheRepository() {
                requestBuilder.createDeckGroup(deckGroupCreateDTO)
                // Verify the last deck group is the newly created one
                requestBuilder.findAll()
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(4)))
                    .andExpect(jsonPath("$[3].name").value(deckGroupCreate.name))
                    .andExpect(jsonPath("$[3].description").value(deckGroupCreate.description))
            }
        }

        @Nested
        @DisplayName("When deck group name is blank")
        inner class WhenDeckGroupNameIsBlank {
            private val deckGroupCreateDTO = DeckGroupCreateDTO("", "A new deck group.")

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.createDeckGroup(deckGroupCreateDTO)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return a JSON error message")
            fun shouldReturnJsonErrorMessage() {
                requestBuilder.createDeckGroup(deckGroupCreateDTO)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck group name is required."))
            }
        }
    }

    @Nested
    @DisplayName("Update deck group")
    inner class UpdateDeckGroup {

        @Nested
        @DisplayName("When deck group is updated successfully")
        inner class WhenDeckGroupIsUpdatedSuccessfully {
            private val deckGroupUpdateDTO = DeckGroupUpdateDTO(1, "Updated Deck Group", "An updated deck group.")
            private val deckGroupUpdate = deckGroupUpdateDTO.toDeckGroupUpdate()
            private val rowIndexOfDeckGroup = 0

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should update the deck group in the database")
            fun shouldUpdateDeckGroupIntoTheDatabase() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)

                // Verify the deck group is updated in db
                assertThat(DECK_GROUP_TABLE).row(rowIndexOfDeckGroup)
                    .value("group_name").`as`("name")
                    .isEqualTo(deckGroupUpdate.name)
                    .value("description").`as`("description")
                    .isEqualTo(deckGroupUpdate.description)
            }

            @Test
            @DisplayName("The updated deck group is found when fetching by ID")
            fun shouldUpdateDeckGroupInTheRepository() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)
                // Verify the deck group is updated
                requestBuilder.findById(deckGroupUpdate.id)
                    .andExpect(jsonPath("$.name").value(deckGroupUpdate.name))
                    .andExpect(jsonPath("$.description").value(deckGroupUpdate.description))
            }
        }

        @Nested
        @DisplayName("When deck group name is blank")
        inner class WhenDeckGroupNameIsBlank {
            private val deckGroupUpdateDTO = DeckGroupUpdateDTO(1, "", "An updated deck group.")

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return a JSON error message")
            fun shouldReturnJsonErrorMessage() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(Matchers.containsString("Validation failed")))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck group name is required."))
            }
        }

        @Nested
        @DisplayName("When Deck Group with given id does not exist")
        inner class WhenDeckGroupWithGivenIdDoesNotExist {
            private val deckGroupUpdateDTO = DeckGroupUpdateDTO(17, "Deck Group 1", "Updated deck group.")

            @Test
            @DisplayName("Should return HTTP status code Not Found")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error message")
            fun shouldReturnErrorMessage() {
                requestBuilder.updateDeckGroup(deckGroupUpdateDTO)
                    .andExpect(jsonPath("$.message").value("Deck group with id 17 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Delete a deck group")
    inner class DeleteDeckGroup {

        @Nested
        @DisplayName("When the deck group exists")
        inner class WhenDeckGroupExists {
            private val id = 1

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.deleteById(id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should delete the deck group by id")
            fun shouldDeleteDeckById() {
                requestBuilder.deleteById(id)
                requestBuilder.findById(id)
                    .andExpect(status().isNotFound)
            }
        }

        @Nested
        @DisplayName("When the deck group does not exist")
        inner class WhenDeckGroupDoesNotExist {
            private val id = 5

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
                    .andExpect(jsonPath("$.message").value("Deck group with id $id not found."))
            }
        }
    }
}
