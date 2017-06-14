DROP TABLE IF EXISTS Sugar; 
DROP TABLE IF EXISTS Goals; 
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

CREATE TABLE Goals (
    goalId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
    userId INTEGER NOT NULL,
    targetSugar DECIMAL (5, 2), 
    targetCalories DECIMAL(5, 2), 
    targetFat DECIMAL(5, 2), 
    targetSaturates DECIMAL(5, 2), 
    targetCarbs DECIMAL(5, 2), 
    targetSalt DECIMAL(5, 2), 
    targetProtein DECIMAL(5, 2),
    CONSTRAINT unique_userId UNIQUE (userId),
    CONSTRAINT fk_userId FOREIGN KEY (userId) REFERENCES User(userId)
);


CREATE TABLE Sugar (
    goalId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
    userId INTEGER NOT NULL,
    allowance DECIMAL(5, 2), 
    reduction DECIMAL(5, 2),
    CONSTRAINT unique_userId UNIQUE (userId),
    CONSTRAINT fk_userId FOREIGN KEY (userId) REFERENCES User(userId)
);
