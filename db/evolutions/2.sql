# --- !Ups

CREATE TABLE Workout (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    description text NOT NULL,
    distance double(10) NOT NULL,
    duration double(10) NOT NULL,
    postedAt date NOT NULL,
    user_id bigint(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Workout;