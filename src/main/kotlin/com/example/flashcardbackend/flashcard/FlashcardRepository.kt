package com.example.flashcardbackend.flashcard

import CardContentCreate
import CardContentType
import CardContentUpdate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.*

@Transactional
@Repository
class FlashcardRepository(@Autowired val jdbcTemplate: JdbcTemplate) {

    fun findAll(): List<FlashcardListItem> {
        val sql = """
                SELECT flashcard.id, flashcard.deck_id, card_content.text 
                FROM flashcard, card_content 
                WHERE flashcard.front_content_id = card_content.id
        """.trimIndent()

        return jdbcTemplate.query(sql) { resultSet, rowNum ->
            println("Found $rowNum flashcards")
            FlashcardListItem(
                resultSet.getInt("id"),
                resultSet.getInt("deck_id"),
                resultSet.getString("text"),
            )
        }
    }

    private fun insertCardContent(cardContent: CardContentCreate, type: CardContentType): Int {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        val sql = "INSERT INTO card_content (card_content_type, text, media_url) VALUES (?,?,?)"
        jdbcTemplate.update({ connection: Connection ->
            val ps: PreparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, type.name)
            ps.setString(2, cardContent.text)
            ps.setString(3, cardContent.mediaUrl)
            ps
        }, keyHolder)
        val id = keyHolder.keys?.get("ID") ?: throw RuntimeException("Database error")
        return id as Int
    }

    fun insert(card: FlashcardCreate): Int {
        val frontContentId = insertCardContent(card.front, CardContentType.FRONT)
        val backContentId = if (card.back != null) insertCardContent(card.back, CardContentType.BACK) else null
        // TODO insert tags
        return jdbcTemplate.update(
            "INSERT INTO flashcard (deck_id, front_content_id, back_content_id, study_status) VALUES (?,?,?,?)",
            card.deckId,
            frontContentId,
            backContentId,
            StudyStatus.NEW.name,
        )
    }

    private fun updateCardContent(cardContent: CardContentUpdate): Int {
        return jdbcTemplate.update(
            "UPDATE card_content SET card_content_type = ?, text = ?, media_url = ? WHERE id = ?",
            cardContent.cardContentType,
            cardContent.text,
            cardContent.mediaUrl,
            cardContent.id,
        )
    }

    fun update(card: FlashcardUpdate): Int {
        if (card.front != null) {
            updateCardContent(card.front)
        }
        if (card.back != null) {
            updateCardContent(card.back)
        }
        // TODO update tags
        return jdbcTemplate.update(
            "UPDATE flashcard SET deck_id = ?, card_type_id = ? WHERE id = ?",
            card.deckId,
            card.id,
        )
    }

    fun deleteById(id: Int): Int {
        return jdbcTemplate.update("delete flashcard where id = ?", id)
    }

    fun findById(id: Int): Flashcard? {
        // TODO implement this
        return null
    }
}
