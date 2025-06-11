-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/6hyZIt
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE "Film" (
    "id" long   NOT NULL,
    "name" varchar(255)   NOT NULL,
    "description" varchar   NOT NULL,
    "releaseDate" Date   NOT NULL,
    "duration" int   NOT NULL,
    "rating_id" int   NOT NULL,
    CONSTRAINT "pk_Film" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "Rating" (
    "id" int   NOT NULL,
    "name" varchar(10)   NOT NULL,
    CONSTRAINT "pk_Rating" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "Genre" (
    "id" int   NOT NULL,
    "name" varchar(50)   NOT NULL,
    CONSTRAINT "pk_Genre" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "FilmGenre" (
    "film_id" int   NOT NULL,
    "genre_id" int   NOT NULL
);

CREATE TABLE "User" (
    "id" long   NOT NULL,
    "email" varchar(255)   NOT NULL,
    "login" varchar(20)   NOT NULL,
    "name" varchar(255)   NOT NULL,
    "birthday" Date   NOT NULL,
    CONSTRAINT "pk_User" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE "FilmLike" (
    "film_id" int   NOT NULL,
    "user_id" int   NOT NULL
);

CREATE TABLE "FriendShip" (
    "user_id" int   NOT NULL,
    "friend_id" int   NOT NULL,
    "confirm" boolean   NOT NULL
);

ALTER TABLE "Film" ADD CONSTRAINT "fk_Film_rating_id" FOREIGN KEY("rating_id")
REFERENCES "Rating" ("id");

ALTER TABLE "FilmGenre" ADD CONSTRAINT "fk_FilmGenre_film_id" FOREIGN KEY("film_id")
REFERENCES "Film" ("id");

ALTER TABLE "FilmGenre" ADD CONSTRAINT "fk_FilmGenre_genre_id" FOREIGN KEY("genre_id")
REFERENCES "Genre" ("id");

ALTER TABLE "FilmLike" ADD CONSTRAINT "fk_FilmLike_film_id" FOREIGN KEY("film_id")
REFERENCES "Film" ("id");

ALTER TABLE "FilmLike" ADD CONSTRAINT "fk_FilmLike_user_id" FOREIGN KEY("user_id")
REFERENCES "User" ("id");

ALTER TABLE "FriendShip" ADD CONSTRAINT "fk_FriendShip_user_id" FOREIGN KEY("user_id")
REFERENCES "User" ("id");

ALTER TABLE "FriendShip" ADD CONSTRAINT "fk_FriendShip_friend_id" FOREIGN KEY("friend_id")
REFERENCES "User" ("id");

