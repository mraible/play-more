# --- !Ups

CREATE TABLE Comment (
    id SERIAL PRIMARY KEY,
    author varchar(255) NOT NULL,
    content text NOT NULL,
    postedAt timestamp NOT NULL,
    workoutId bigint NOT NULL REFERENCES Workout ON DELETE CASCADE
);

# --- !Downs

DROP TABLE Comment;
