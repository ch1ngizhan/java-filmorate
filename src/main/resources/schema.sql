CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) UNIQUE NOT NULL,
    login    VARCHAR(255)        NOT NULL,
    name     VARCHAR(255)        NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    film_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    release_date  DATE,
    duration      INTEGER,
    mpa_rating_id BIGINT,
    FOREIGN KEY (mpa_rating_id) REFERENCES mpa_rating (mpa_rating_id)
);

CREATE TABLE IF NOT EXISTS film_like
(
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS friendships
(
    user_id   BIGINT       NOT NULL,
    friend_id BIGINT       NOT NULL,
    status    VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (friend_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);