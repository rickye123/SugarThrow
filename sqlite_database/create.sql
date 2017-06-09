DROP TABLE IF EXISTS Diary; 
DROP TABLE IF EXISTS Food; 
DROP TABLE IF EXISTS User; 

CREATE TABLE User (
    userId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
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

CREATE TABLE Food (
    foodId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
    name VARCHAR(100) NOT NULL,
    calories DECIMAL(5,2), 
    sugar DECIMAL(5, 2),
    fat DECIMAL(5, 2), 
    saturates DECIMAL(5, 2), 
    carbs DECIMAL(5, 2), 
    salt DECIMAL(5, 4), 
    protein DECIMAL(5, 2),
    CONSTRAINT name_unique UNIQUE (name)
);

CREATE TABLE Diary (
    diaryId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
    theDate DATE NOT NULL, 
    userId INTEGER NOT NULL, 
    foodId INTEGER NOT NULL, 
    CONSTRAINT fk_userId FOREIGN KEY (userId) REFERENCES User(userId), 
    CONSTRAINT fk_foodId FOREIGN KEY (foodId) REFERENCES Food(foodId)
); 

INSERT INTO User (userName, name, gender, dob, height, weight, password, points) VALUES ('re16621', 'Rich Ellor', 'M', '18-09-1993', '5.8', '10.0', 'password', 0);
INSERT INTO User (userName, name, gender, dob, height, weight, password, points) VALUES ('bb16622', 'Becca Brown', 'F', '15-11-1993', '5.8', '12.0', 'password', 0);

INSERT INTO Food (name, calories, sugar, fat, saturates, carbs, salt, protein) VALUES ('Test Heinz Baked Beans', '239', '20.2', '0.9', '0.2', '53.7', '0.8712', '12.1'); 
INSERT INTO Food (name, calories, sugar, fat, saturates, carbs, salt, protein) VALUES ('Test Apple', '95', '18.9', '0.3', '0.1', '25.1', '0.18', '0.5');
INSERT INTO Food (name, calories, sugar, fat, saturates, carbs, salt, protein) VALUES ('Test Slice of Pizza', '285', '3.8', '10.4', '4.8', '35.7', '0.6399', '12.2');

INSERT INTO Diary(theDate, userId, foodId) VALUES (DATE('now'), 1, 1);
INSERT INTO Diary(theDate, userId, foodId) VALUES (DATE('now'), 1, 1);
INSERT INTO Diary(theDate, userId, foodId) VALUES (DATE('now'), 1, 2);
INSERT INTO Diary(theDate, userId, foodId) VALUES (DATE('now'), 1, 3);

