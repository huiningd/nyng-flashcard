package com.example.flashcardbackend.deckgroup

import com.example.flashcardbackend.deck.DeckListItem
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
class DeckGroupRepository(@Autowired val jdbcTemplate: JdbcTemplate) {

    fun findAll(): List<DeckGroupListItem> {
        return jdbcTemplate.query(
            "SELECT * FROM deck_group",
        ) { resultSet, rowNum ->
            println("Found $rowNum deck groups")
            DeckGroupListItem(
                resultSet.getInt("id"),
                resultSet.getString("group_name"),
                resultSet.getString("description"),
            )
        }
    }

    fun insert(deckGroup: DeckGroupCreate): Int {
        return jdbcTemplate.update(
            "INSERT INTO deck_group (group_name, description) VALUES (?,?)",
            deckGroup.name,
            deckGroup.description,
        )
    }

    fun update(deckGroup: DeckGroupUpdate): Int {
        return jdbcTemplate.update(
            "UPDATE deck_group SET group_name = ?, description = ? WHERE id = ?",
            deckGroup.name,
            deckGroup.description,
            deckGroup.id,
        )
    }

    fun deleteById(id: Int): Int {
        return jdbcTemplate.update("DELETE deck_group WHERE id = ?", id)
    }

    fun findById(id: Int): DeckGroup? {
        val sql = """
            SELECT deck_group.*, deck.* 
            FROM deck_group, deck 
            WHERE deck_group.id = deck.group_id AND deck_group.id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, DeckGroupResultSetExtractor(), id)
    }
}

class DeckGroupResultSetExtractor : ResultSetExtractor<DeckGroup?> {
    @Throws(SQLException::class, DataAccessException::class)
    override fun extractData(rs: ResultSet): DeckGroup? {
        var deckGroup: DeckGroup? = null
        while (rs.next()) {
            if (deckGroup == null) {
                deckGroup = DeckGroup(
                    name = rs.getString("group_name"),
                    description = rs.getString("description"),
                    decks = mutableListOf(),
                )
            }
            val deck = DeckListItem(
                rs.getInt("id"),
                rs.getInt("group_id"),
                rs.getString("deck_name"),
                rs.getString("description"),
            )
            deckGroup.decks += deck
        }
        return deckGroup
    }
}
