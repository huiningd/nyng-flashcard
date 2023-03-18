package com.example.flashcardbackend.deck

import com.example.flashcardbackend.flashcard.FlashcardListItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.sql.SQLException

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

    fun deleteById(id: Int): Int {
        return jdbcTemplate.update("DELETE deck WHERE id = ?", id)
    }

    fun findById(id: Int): Deck? {
        val sql = """
            SELECT deck.*, flashcard.id AS flashcard_id, flashcard.deck_id 
            FROM deck, flashcard 
            WHERE deck.id = flashcard.deck_id AND flashcard.id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, DeckResultSetExtractor(), id)
    }
}

class DeckResultSetExtractor : ResultSetExtractor<Deck?> {
    @Throws(SQLException::class, DataAccessException::class)
    override fun extractData(rs: ResultSet): Deck? {
        var deck: Deck? = null
        while (rs.next()) {
            if (deck == null) {
                deck = Deck(
                    id = rs.getInt("id"),
                    deckGroupId = rs.getInt("group_id"),
                    name = rs.getString("deck_name"),
                    description = rs.getString("description"),
                    flashcards = mutableListOf(),
                )
            }
            val flashcard = FlashcardListItem(
                rs.getInt("flashcard_id"),
                rs.getInt("deck_id"),
                // TODO: add a card_name or preview field so that we don't need to query 3 tables every time fetch a deck
                "todo",
            )
            deck.flashcards += flashcard
        }
        return deck
    }
}
