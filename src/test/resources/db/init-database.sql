
-- deck_group
INSERT INTO deck_group (group_name, description)
VALUES ('Languages', 'A group of decks for learning languages.'),
       ('Movies', 'A group of decks for learning movies topics.'),
       ('Science', 'A group of decks for learning science topics.');

-- deck
INSERT INTO deck (group_id, deck_name, description)
VALUES (1, 'Spanish Vocabulary', 'A deck for learning Spanish vocabulary.'),
       (1, 'French Vocabulary', 'A deck for learning French vocabulary.'),
       (3, 'Physics Basics', 'A deck for learning the basics of Physics.');

-- card_type
INSERT INTO card_type (type_name)
VALUES ('Basic'),
       ('Close');

-- card_tag
INSERT INTO card_tag (tag_name)
VALUES ('Beginner'),
       ('Intermediate'),
       ('Advanced');

-- card_content
INSERT INTO card_content (card_content_type, text, media_url)
VALUES ('FRONT', 'What is the Spanish word for apple?', NULL),
       ('BACK', 'manzana', NULL),
       ('FRONT', 'Translate to French: cheese', NULL),
       ('BACK', 'fromage', NULL),
       ('FRONT', 'What is Newton\'s first law of motion?', NULL),
       ('BACK', 'An object at rest stays at rest and an object in motion stays in motion with the same speed and in the same direction unless acted upon by an unbalanced force.', NULL),
       ('FRONT', 'What is the Spanish word for milk?', NULL),
       ('BACK', 'la leche', NULL);

-- flashcard
INSERT INTO flashcard (deck_id, card_type_id, front_content_id, back_content_id, study_status)
VALUES (1, 1, 1, 2, 'NEW'),
       (2, 1, 3, 4, 'NEW'),
       (3, 1, 5, 6, 'NEW'),
       (1, 1, 7, 8, 'NEW');

-- flashcard_tag
INSERT INTO flashcard_tag (flashcard_id, tag_id)
VALUES (1, 1),
       (2, 1),
       (3, 1);
