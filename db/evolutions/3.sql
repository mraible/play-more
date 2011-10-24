--- !Ups

CREATE TABLE Comment (
    id bigint(20) NOT NULL SERIAL,
    author varchar(255) NOT NULL,
    content text NOT NULL,
    postedAt date NOT NULL,
    workout_id bigint(20) NOT NULL,
    FOREIGN KEY (workout_id) REFERENCES Workout(id) ON DELETE CASCADE,
    PRIMARY KEY (id)
);

--- !Downs

DROP TABLE Comment;
