package com.example.richa.sugarthrow;

/*
This class contains a list of all the SQL statements that are used within this
app. 
 */

class SqlQueries {

    static final String SQL_REGULAR_FOOD = "SELECT User.userName AS uname, Diary.theDate as entryDate, Food.foodId " +
            "AS fid, Food.name as foodName, COUNT(*) AS quantity FROM Diary INNER JOIN " +
            "Food ON Diary.foodId = Food.foodId INNER JOIN User ON User.userID = Diary.userId " +
            "WHERE User.userName = ? GROUP BY Diary.foodId ORDER BY quantity DESC LIMIT 5";

    static final String SQL_SET_POINTS_0 = "UPDATE User SET points = 0 WHERE User.userName = ?";
    static final String SQL_INCREMENT_POINTS_1 = "UPDATE User SET points = points + 1 WHERE User.userName = ?";
    static final String SQL_DECREMENT_POINTS_1 = "UPDATE User SET points = points - 1 WHERE User.userName = ?";
    static final String SQL_INCREMENT_POINTS_10 = "UPDATE User SET points = points + 10 WHERE User.userName = ?";
    static final String SQL_DECREMENT_POINTS_10 = "UPDATE User SET points = points - 10 WHERE User.userName = ?";

    static final String SQL_IN_DIARY= "SELECT Food.name as food, COUNT(*) AS quantity, Food.foodId FROM Diary INNER JOIN " +
            "User ON Diary.userId = User.userId INNER JOIN Food ON Diary.foodId = Food.foodId WHERE Diary.theDate = ? " +
            "AND User.userName = ? GROUP BY Diary.foodId";

    static final String SQL_STREAK = "SELECT COUNT(*) FROM (SELECT Food.foodId FROM Diary INNER JOIN User ON User.userId = Diary.userId " +
            "INNER JOIN Food ON Food.foodId = Diary.foodID WHERE theDate = ? AND User.userName = ? GROUP BY Food.foodID) as points";

    static final String SQL_POINTS = "SELECT points FROM User WHERE userName = ?";

    static final String SQL_SELECT_DIARY_ENTRY = "SELECT Diary.diaryId as diaryId, Diary.theDate as theDate, " +
            "User.userName as uname, User.userId as uid, Food.name as foodName, Food.foodId as fid FROM Diary " +
            "INNER JOIN Food ON Diary.foodId = Food.foodId INNER JOIN User ON User.userId = Diary.userId " +
            "WHERE theDate = ? AND uname = ? and foodName = ?";

    static final String SQL_SELECT_USER = "SELECT userId FROM User WHERE userName = ?";

    static final String SQL_SELECT_SPECIFIC_USER = "SELECT * FROM User WHERE userName = ?";

    static final String SQL_SELECT_FOOD = "SELECT * FROM Food WHERE name = ?";

    static final String SQL_SELECT_CURRENT_DIARY = "SELECT SUM(Food.sugar), SUM(Food.calories), SUM(Food.fat), SUM(Food.saturates), " +
            "SUM(Food.carbs), SUM(Food.salt), SUM(Food.protein) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE theDate = date('now') AND User.userName = ?";

}
