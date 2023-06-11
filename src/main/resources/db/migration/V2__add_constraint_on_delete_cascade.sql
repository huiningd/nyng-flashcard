ALTER TABLE deck DROP CONSTRAINT fk_deck_group;
ALTER TABLE deck ADD CONSTRAINT fk_deck_group FOREIGN KEY (group_id) REFERENCES deck_group (id) ON DELETE CASCADE;

ALTER TABLE flashcard DROP CONSTRAINT fk_flashcard_deck;
ALTER TABLE flashcard ADD CONSTRAINT fk_flashcard_deck FOREIGN KEY (deck_id) REFERENCES deck (id) ON DELETE CASCADE;

ALTER TABLE flashcard_tag DROP CONSTRAINT fk_flashcard_tag_flashcard_id;
ALTER TABLE flashcard_tag ADD CONSTRAINT fk_flashcard_tag_flashcard_id FOREIGN KEY (flashcard_id) REFERENCES flashcard (id) ON DELETE CASCADE;

ALTER TABLE flashcard_tag DROP CONSTRAINT fk_flashcard_tag_tag_id;
ALTER TABLE flashcard_tag ADD CONSTRAINT fk_flashcard_tag_tag_id FOREIGN KEY (tag_id) REFERENCES card_tag (id) ON DELETE CASCADE;

ALTER TABLE card_studied_datetime DROP CONSTRAINT fk_card_studied_datetime_flashcard_id;
ALTER TABLE card_studied_datetime ADD CONSTRAINT fk_card_studied_datetime_flashcard_id FOREIGN KEY (flashcard_id) REFERENCES flashcard (id) ON DELETE CASCADE;

ALTER TABLE comment DROP CONSTRAINT fk_comment_flashcard_id;
ALTER TABLE comment ADD CONSTRAINT fk_comment_flashcard_id FOREIGN KEY (flashcard_id) REFERENCES flashcard (id) ON DELETE CASCADE;
