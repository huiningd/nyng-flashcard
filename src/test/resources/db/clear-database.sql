
-- Clear tables with foreign key dependencies first
DELETE FROM flashcard_tag;
DELETE FROM flashcard;
DELETE FROM card_content;

-- Clear the remaining tables
DELETE FROM card_tag;
DELETE FROM card_type;
DELETE FROM deck;
DELETE FROM deck_group;

-- Reset auto-increment values
ALTER TABLE flashcard_tag AUTO_INCREMENT = 1;
ALTER TABLE flashcard AUTO_INCREMENT = 1;
ALTER TABLE card_content AUTO_INCREMENT = 1;
ALTER TABLE card_tag AUTO_INCREMENT = 1;
ALTER TABLE card_type AUTO_INCREMENT = 1;
ALTER TABLE deck AUTO_INCREMENT = 1;
ALTER TABLE deck_group AUTO_INCREMENT = 1;
