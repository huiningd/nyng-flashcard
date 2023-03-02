package com.example.flashcardbackend.deckgroup

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.sql.SQLException

class DeckGroupRowMapper : RowMapper<DeckGroupListItem> {
    @Throws(SQLException::class)
    override fun mapRow(resultSet: ResultSet, i: Int): DeckGroupListItem {
        return DeckGroupListItem(
            resultSet.getInt("id"),
            resultSet.getString("collection_name"),
            resultSet.getString("description"),
        )
    }
}

@Transactional
@Repository
class DeckGroupRepository(@Autowired val jdbcTemplate: JdbcTemplate) {

    fun findAll(): List<DeckGroupListItem> {
        return jdbcTemplate.query("select * from deck_group", DeckGroupRowMapper())
    }

    fun insert(deckGroup: DeckGroupCreate): Int {
        return jdbcTemplate.update(
            "insert into deck_group (group_name, description) values(?,?)",
            deckGroup.name,
            deckGroup.description,
        )
    }

    fun update(deckGroup: DeckGroupUpdate): Int {
        return jdbcTemplate.update(
            "insert into deck_group (group_name, description) values(?,?) where id = ?",
            deckGroup.name,
            deckGroup.description,
            deckGroup.id,
        )
    }

    fun deleteById(id: Int): Int {
        return jdbcTemplate.update("delete deck_group where id = ?", id)
    }

    fun findById(id: Int): DeckGroup? {
        // TODO: return deckGroup with list of decks
        return null
    }
}
