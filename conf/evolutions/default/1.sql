# --- !Ups

CREATE TABLE Athlete (
    id SERIAL PRIMARY KEY,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    firstName varchar(100) NOT NULL,
    lastName varchar(100) NOT NULL
);
 
# --- !Downs
 
DROP TABLE Athlete;