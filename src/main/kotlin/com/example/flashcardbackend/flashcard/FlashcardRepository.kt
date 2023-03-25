package com.example.flashcardbackend.flashcard

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class FlashcardRepository(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    private val flashcardResultSetExtractor: ResultSetExtractor<List<Flashcard>> = JdbcTemplateMapperFactory
        .newInstance()
        .addKeys("id")
        .newResultSetExtractor(Flashcard::class.java)

    fun findById(id: Int): Flashcard? {
        val sql = """
            SELECT f.id AS id, f.deck_id as deckId, f.study_status as study_status, f.last_viewed as last_viewed,
            front.text AS front_text, front.media_url AS front_media_url,
            back.text AS back_text, back.media_url AS back_media_url
            FROM flashcard f
            LEFT JOIN card_content front on front.id = f.front_content_id
            LEFT JOIN card_content back on back.id = f.back_content_id
            WHERE f.id = ?
        """.trimIndent()
        val result: List<Flashcard>? = jdbcTemplate.query(sql, flashcardResultSetExtractor, id)
        return if (!result.isNullOrEmpty()) result[0] else null
    }

    fun findAll(): List<FlashcardListItem> {
        val sql = """
                SELECT flashcard.id, flashcard.deck_id, card_content.text 
                FROM flashcard, card_content 
                WHERE flashcard.front_content_id = card_content.id
                ORDER BY flashcard.id
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

    fun deleteById(id: Int) {
        jdbcTemplate.update("DELETE FROM flashcard WHERE id = ?", id)
    }
}
