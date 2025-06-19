-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/6hyZIt
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE "films" (
    "id"  SERIAL  NOT NULL,
    "name" varchar(255)   NOT NULL,
    "description" varchar(200)   NULL,
    "releaseDate" date   NULL,
    "duration" int   NULL,
    "rating_id" int   NOT NULL,
    CONSTRAINT "pk_films" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "ratings" (
    "id"  SERIAL  NOT NULL,
    "name" varchar(10)   NOT NULL,
    CONSTRAINT "pk_ratings" PRIMARY KEY (
        "id"
     ),
    CONSTRAINT "uc_ratings_name" UNIQUE (
        "name"
    )
);

CREATE TABLE "genres" (
    "id"  SERIAL  NOT NULL,
    "name" varchar(50)   NOT NULL,
    CONSTRAINT "pk_genres" PRIMARY KEY (
        "id"
     ),
    CONSTRAINT "uc_genres_name" UNIQUE (
        "name"
    )
);

CREATE TABLE "film_genres" (
    "film_id" int   NOT NULL,
    "genre_id" int   NOT NULL
);

CREATE TABLE "users" (
    "id"  SERIAL  NOT NULL,
    "email" varchar(255)   NOT NULL,
    "login" varchar(20)   NOT NULL,
    "name" varchar(255)   NULL,
    "birthday" date   NULL,
    CONSTRAINT "pk_users" PRIMARY KEY (
        "id"
     ),
    CONSTRAINT "uc_users_email" UNIQUE (
        "email"
    ),
    CONSTRAINT "uc_users_login" UNIQUE (
        "login"
    )
);

CREATE TABLE "film_likes" (
    "film_id" int   NOT NULL,
    "user_id" int   NOT NULL
);

CREATE TABLE "friendships" (
    "user_id" int   NOT NULL,
    "friend_id" int   NOT NULL,
    "confirm" boolean  DEFAULT 'false' NOT NULL
);

ALTER TABLE "films" ADD CONSTRAINT "fk_films_rating_id" FOREIGN KEY("rating_id")
REFERENCES "ratings" ("id");

ALTER TABLE "film_genres" ADD CONSTRAINT "fk_film_genres_film_id" FOREIGN KEY("film_id")
REFERENCES "films" ("id");

ALTER TABLE "film_genres" ADD CONSTRAINT "fk_film_genres_genre_id" FOREIGN KEY("genre_id")
REFERENCES "genres" ("id");

ALTER TABLE "film_likes" ADD CONSTRAINT "fk_film_likes_film_id" FOREIGN KEY("film_id")
REFERENCES "films" ("id");

ALTER TABLE "film_likes" ADD CONSTRAINT "fk_film_likes_user_id" FOREIGN KEY("user_id")
REFERENCES "users" ("id");

ALTER TABLE "friendships" ADD CONSTRAINT "fk_friendships_user_id" FOREIGN KEY("user_id")
REFERENCES "users" ("id");

ALTER TABLE "friendships" ADD CONSTRAINT "fk_friendships_friend_id" FOREIGN KEY("friend_id")
REFERENCES "users" ("id");

CREATE INDEX "idx_friendships_user_id"
ON "friendships" ("user_id");

CREATE INDEX "idx_friendships_friend_id"
ON "friendships" ("friend_id");

