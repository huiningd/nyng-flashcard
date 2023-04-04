package com.example.flashcardbackend.deck

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class DeckRepository(@Autowired val jdbcTemplate: JdbcTemplate) {

    fun findAll(): List<DeckListItem> {
        return jdbcTemplate.query(
            "SELECT * FROM deck",
        ) { resultSet, rowNum ->
            println("Found $rowNum decks")
            DeckListItem(
                resultSet.getInt("id"),
                resultSet.getInt("group_id"),
                resultSet.getString("deck_name"),
                resultSet.getString("description"),
            )
        }
    }

    fun insert(deck: DeckCreate): Int {
        return jdbcTemplate.update(
            "INSERT INTO deck (deck_name, description, group_id) VALUES (?,?,?)",
            deck.name,
            deck.description,
            deck.deckGroupId,
        )
    }

    fun update(deck: DeckUpdate): Int {
        return jdbcTemplate.update(
            "UPDATE deck SET deck_name = ?, description = ?, group_id = ? WHERE id = ?",
            deck.name,
            deck.description,
            deck.deckGroupId,
            deck.id,
        )
    }

    fun deleteById(id: Int) {
        jdbcTemplate.update("DELETE FROM deck WHERE id = ?", id)
    }

    private val deckResultSetExtractor: ResultSetExtractor<List<Deck>> = JdbcTemplateMapperFactory
        .newInstance()
        .addKeys("id", "flashcard_id")
        .newResultSetExtractor(Deck::class.java)

    fun findById(id: Int): Deck? {
        val sql = """
            SELECT d.id AS id, d.deck_name AS name, d.description AS description, d.group_id AS deck_group_id,
            f.id AS flashcard_id,
            c.text AS flashcard_front_content_text
            FROM deck d
            LEFT JOIN flashcard f on d.id = f.deck_id
            LEFT JOIN card_content c on c.id = f.front_content_id
            WHERE d.id = ?
        """.trimIndent()
        val result: List<Deck>? = jdbcTemplate.query(sql, deckResultSetExtractor, id)
        return if (!result.isNullOrEmpty()) result[0] else null
    }
}
