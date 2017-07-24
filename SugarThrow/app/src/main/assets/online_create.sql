DROP TABLE IF EXISTS Contents; 
DROP TABLE IF EXISTS Users; 

CREATE TABLE Users (
    userId INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, 
    userName VARCHAR(100) NOT NULL, 
    name VARCHAR(100) NOT NULL, 
    gender VARCHAR(1) NOT NULL, 
    dob DATE NOT NULL, 
    height DECIMAL(3, 2) NOT NULL, 
    weight DECIMAL(4, 2) NOT NULL,
    password VARCHAR(100) NOT NULL, 
    points INTEGER NOT NULL, 
    CONSTRAINT userName_unique UNIQUE (userName)
);

CREATE TABLE Contents(
    contentId INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, 
    userId INTEGER NOT NULL, 
    theDate DATE NOT NULL, 
    calories DECIMAL(6,2), 
    sugar DECIMAL(5, 2),
    fat DECIMAL(5, 2), 
    saturates DECIMAL(5, 2), 
    carbs DECIMAL(5, 2), 
    salt DECIMAL(5, 4), 
    protein DECIMAL(5, 2)
); 

