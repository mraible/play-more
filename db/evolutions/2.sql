# --- !Ups

CREATE TABLE Workout (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    description text NOT NULL,
    distance double(10) NOT NULL,
    duration double(10) NOT NULL,
    postedAt date NOT NULL,
    athlete_id bigint(20) NOT NULL,
    FOREIGN KEY (athlete_id) REFERENCES Athlete(id),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Workout;