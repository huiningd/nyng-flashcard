package com.example.flashcardbackend.flashcard

import CardContentCreate
import CardContentType
import CardContentUpdate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class FlashcardRepository(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

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
        jdbcTemplate.update(
            "INSERT INTO card_content (card_content_type, text, media_url) VALUES (?,?,?)",
            type.name,
            cardContent.text,
            cardContent.mediaUrl,
        )
        val cardContentId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Int::class.java)
        requireNotNull(cardContentId) { "Failed to return $type card content ID!" }
        return cardContentId
    }

    fun insert(card: FlashcardCreate): Int {
        val frontContentId = insertCardContent(card.front, CardContentType.FRONT)
        val backContentId = if (card.back != null) insertCardContent(card.back, CardContentType.BACK) else null
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
