
CREATE TABLE IF NOT EXISTS deck_group (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    group_name VARCHAR(50) NOT NULL,
    description VARCHAR(250),
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE IF NOT EXISTS deck (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    group_id INTEGER NOT NULL,
    deck_name VARCHAR(50) NOT NULL,
    description VARCHAR(250),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_deck_group FOREIGN KEY (group_id) REFERENCES deck_group (id)
);

CREATE TABLE IF NOT EXISTS card_type (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS card_tag (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    tag_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS card_content (
     id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
     card_content_type enum ('FRONT', 'BACK')  NOT NULL,
     text VARCHAR(2000) NOT NULL,
     media_url VARCHAR(1000),
     created_at TIMESTAMP NOT NULL,
     updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS flashcard (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    deck_id INTEGER NOT NULL,
    card_type_id INTEGER NOT NULL,
    front_content_id INTEGER NOT NULL,
    back_content_id INTEGER NOT NULL,
    study_status enum ('NEW', 'YOUNG', 'MATURE', 'SUSPENDED', 'BURIED') NOT NULL,
    last_viewed TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_flashcard_deck FOREIGN KEY (deck_id) REFERENCES deck (id),
    CONSTRAINT fk_flashcard_card_type FOREIGN KEY (card_type_id) REFERENCES card_type (id),
    CONSTRAINT fk_flashcard_front_card_content FOREIGN KEY (front_content_id) REFERENCES card_content (id) ON DELETE CASCADE,
    CONSTRAINT fk_flashcard_back_card_content FOREIGN KEY (back_content_id) REFERENCES card_content (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS flashcard_tag (
    flashcard_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (flashcard_id, tag_id),
    CONSTRAINT fk_flashcard_tag_flashcard_id FOREIGN KEY (flashcard_id) REFERENCES flashcard (id),
    CONSTRAINT fk_flashcard_tag_tag_id FOREIGN KEY (tag_id) REFERENCES card_tag (id)
);

CREATE TABLE IF NOT EXISTS card_studied_date (
    id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    flashcard_id INTEGER NOT NULL,
    studied_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_card_studied_date_flashcard_id FOREIGN KEY (flashcard_id) REFERENCES flashcard (id)
);

CREATE TABLE IF NOT EXISTS comment (
    id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    text VARCHAR(400) NOT NULL,
    flashcard_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_comment_flashcard_id FOREIGN KEY (flashcard_id) REFERENCES flashcard (id)
);
