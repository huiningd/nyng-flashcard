package com.example.flashcardbackend.webmvctest

import com.example.flashcardbackend.deckgroup.*
import com.example.flashcardbackend.requestbuilder.DeckGroupHttpRequestBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DeckGroupController::class, DeckGroupService::class)
class DeckGroupControllerTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    @MockBean
    private lateinit var deckGroupRepository: DeckGroupRepository

    private var requestBuilder = DeckGroupHttpRequestBuilder(mockMvc)

    @Nested
    @DisplayName("Find all deck groups")
    inner class FindAllDeckGroups {

        @Nested
        @DisplayName("When 2 deck groups exist")
        inner class WhenDeckGroupsExist {
            private val deckGroups = listOf(
                DeckGroupListItem(1, "Deck Group 1", "description 1"),
                DeckGroupListItem(2, "Deck Group 2", "description 2"),
            )

            @BeforeEach
            fun setUp() {
                given(deckGroupRepository.findAll()).willReturn(deckGroups)
            }

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
                    .andExpect(jsonPath(("$"), Matchers.hasSize<Int>(2)))
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
        inner class WhenNoDeckGroupsExist {
            @BeforeEach
            fun setUp() {
                `when`(deckGroupRepository.findAll()).thenReturn(emptyList())
            }

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
            private val id = 12

            @BeforeEach
            fun setUp() {
                val expectedDeckGroup = DeckGroup(
                    id = id,
                    name = "Sample deck group",
                    description = "Sample description",
                    decks = emptyList(),
                )

                given(deckGroupRepository.findById(id)).willReturn(expectedDeckGroup)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.findById(id)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should return deck groups as JSON")
            fun shouldReturnDeckGroupsAsJSON() {
                requestBuilder.findById(id)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }

            @Test
            @DisplayName("Should return the information of found deck group")
            fun shouldReturnFoundDeckGroupInfo() {
                requestBuilder.findById(id)
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Sample deck group"))
                    .andExpect(jsonPath("$.description").value("Sample description"))
                    .andExpect(jsonPath("$.decks").isEmpty)
            }
        }

        @Nested
        @DisplayName("When deck group with given ID does not exist")
        inner class WhenDeckGroupDoesNotExist {
            private val id = 12

            @BeforeEach
            fun setUp() {
                given(deckGroupRepository.findById(id)).willReturn(null)
            }

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
            private val requestBody = """{"name": "Finnished", "description": "Learn Finnish with Finnished"}"""
            private val deckGroupCreate = DeckGroupCreate("Finnished", "Learn Finnish with Finnished")

            @BeforeEach
            fun setUp() {
                given(deckGroupRepository.insert(deckGroupCreate)).willReturn(1)
            }

            @Test
            @DisplayName("Should return the HTTP status code CREATED")
            fun shouldReturnHttpStatusCodeCreated() {
                requestBuilder.createDeckGroup(requestBody)
                    .andExpect(status().isCreated)
            }

            @Test
            @DisplayName("Should insert the deck group in the repository")
            fun shouldInsertDeckGroupInTheRepository() {
                requestBuilder.createDeckGroup(requestBody)
                verify(deckGroupRepository).insert(deckGroupCreate)
            }
        }

        @Nested
        @DisplayName("When deck group name is blank")
        inner class WhenDeckGroupNameIsBlank {
            private val requestBody = """{"name": "", "description": "A new deck group."}"""

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.createDeckGroup(requestBody)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return a JSON error message")
            fun shouldReturnJsonErrorMessage() {
                requestBuilder.createDeckGroup(requestBody)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(containsString("Validation failed")))
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
            private val requestBody = """{"id": 1, "name": "Updated Deck Group", "description": "An updated deck group."}"""
            private val deckGroupUpdate = DeckGroupUpdate(1, "Updated Deck Group", "An updated deck group.")

            @BeforeEach
            fun setUp() {
                given(deckGroupRepository.update(deckGroupUpdate)).willReturn(1)
            }

            @Test
            @DisplayName("Should return the HTTP status code OK")
            fun shouldReturnHttpStatusCodeOk() {
                requestBuilder.updateDeckGroup(requestBody)
                    .andExpect(status().isOk)
            }

            @Test
            @DisplayName("Should update the deck group in the repository")
            fun shouldUpdateDeckGroupInTheRepository() {
                requestBuilder.updateDeckGroup(requestBody)

                verify(deckGroupRepository).update(deckGroupUpdate)
            }
        }

        @Nested
        @DisplayName("When deck group name is blank")
        inner class WhenDeckGroupNameIsBlank {
            private val requestBody = """{"id": 1, "name": "", "description": "An updated deck group."}"""

            @Test
            @DisplayName("Should return the HTTP status code BAD REQUEST")
            fun shouldReturnHttpStatusCodeBadRequest() {
                requestBuilder.updateDeckGroup(requestBody)
                    .andExpect(status().isBadRequest)
            }

            @Test
            @DisplayName("Should return a JSON error message")
            fun shouldReturnJsonErrorMessage() {
                requestBuilder.updateDeckGroup(requestBody)
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").value(containsString("Validation failed")))
                    .andExpect(jsonPath("$.fieldErrors[0].message").value("The deck group name is required."))
            }
        }

        @Nested
        @DisplayName("When Deck Group with given id does not exist")
        inner class WhenDeckGroupWithGivenIdDoesNotExist {
            private val requestBody = """{"id": 17, "name": "Deck Group 1", "description": "Description 1"}"""
            private val deckGroupUpdate = DeckGroupUpdate(17, "Deck Group 1", "Description 1")

            @BeforeEach
            fun setup() {
                given(deckGroupRepository.update(deckGroupUpdate)).willReturn(0)
            }

            @Test
            @DisplayName("Should return HTTP status code Not Found")
            fun shouldReturnHttpStatusCodeNotFound() {
                requestBuilder.updateDeckGroup(requestBody)
                    .andExpect(status().isNotFound)
            }

            @Test
            @DisplayName("Should return error message")
            fun shouldReturnErrorMessage() {
                requestBuilder.updateDeckGroup(requestBody)
                    .andExpect(jsonPath("$.message").value("Deck group with id 17 not found."))
            }
        }
    }

    @Nested
    @DisplayName("Delete a deck group")
    inner class DeleteDeckGroup {
        private val id = 10

        @Nested
        @DisplayName("When the deck group exists")
        inner class WhenDeckGroupExists {

            @BeforeEach
            fun setup() {
                given(deckGroupRepository.deleteById(id)).willReturn(1)
            }

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
                verify(deckGroupRepository).deleteById(id)
            }
        }

        @Nested
        @DisplayName("When the deck group does not exist")
        inner class WhenDeckGroupDoesNotExist {

            @BeforeEach
            fun setup() {
                given(deckGroupRepository.deleteById(id)).willReturn(0)
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
                    .andExpect(jsonPath("$.message").value("Deck group with id $id not found."))
            }
        }
    }
}
