package com.example.flashcardbackend.deckgroup

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

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
        return jdbcTemplate.update("DELETE FROM deck_group WHERE id = ?", id)
    }

    private val deckGroupResultSetExtractor: ResultSetExtractor<List<DeckGroup>> = JdbcTemplateMapperFactory
        .newInstance()
        .addKeys("id", "deck_id")
        .newResultSetExtractor(DeckGroup::class.java)

    fun findById(id: Int): DeckGroup? {
        val sql = """
            SELECT dg.id AS id, dg.group_name AS name, dg.description AS description, 
            d.id AS deck_id, d.deck_name AS deck_name, d.description AS deck_description, d.group_id AS deck_deck_group_id
            FROM deck_group dg
            LEFT JOIN deck d on dg.id = d.group_id
            WHERE dg.id = ?
        """.trimIndent()
        val result: List<DeckGroup>? = jdbcTemplate.query(sql, deckGroupResultSetExtractor, id)
        return if (!result.isNullOrEmpty()) result[0] else null
    }
}
