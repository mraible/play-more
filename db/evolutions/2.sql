# --- !Ups

CREATE TABLE Workout (
    id SERIAL PRIMARY KEY,
    title varchar(255) NOT NULL,
    description text NOT NULL,
    distance float8 NOT NULL,
    duration float8 NOT NULL,
    postedAt timestamp NOT NULL,
    athlete_id bigint REFERENCES Athlete
);

# --- !Downs

DROP TABLE Workout;
