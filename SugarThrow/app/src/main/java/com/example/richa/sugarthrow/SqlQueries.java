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
    static final String SQL_INCREMENT_POINTS_2 = "UPDATE User SET points = points + 2 WHERE User.userName = ?";
    static final String SQL_DECREMENT_POINTS_1 = "UPDATE User SET points = points - 1 WHERE User.userName = ?";
    static final String SQL_INCREMENT_POINTS_10 = "UPDATE User SET points = points + 10 WHERE User.userName = ?";
    static final String SQL_DECREMENT_POINTS_10 = "UPDATE User SET points = points - 10 WHERE User.userName = ?";
    static final String SQL_INCREMENT_POINTS_5 = "UPDATE User SET points = points + 5 WHERE User.userName = ?";
    static final String SQL_DECREMENT_POINTS_5 = "UPDATE User SET points = points - 5 WHERE User.userName = ?";

    static final String SQL_DAILY_POINTS = "SELECT points FROM User INNER JOIN Diary ON Diary.userId = User.userId WHERE " +
            "Diary.theDate = ? AND User.userName = ? GROUP BY theDate";

    static final String SQL_IN_DIARY= "SELECT Food.name as food, COUNT(*) AS quantity, Food.foodId FROM Diary INNER JOIN " +
            "User ON Diary.userId = User.userId INNER JOIN Food ON Diary.foodId = Food.foodId WHERE Diary.theDate = ? " +
            "AND User.userName = ? GROUP BY Diary.foodId";

    static final String SQL_SIZE_DIARY = "SELECT COUNT(*) FROM (SELECT Food.name as food, COUNT(*) AS quantity, Food.foodId FROM Diary " +
            "INNER JOIN User ON Diary.userId = User.userId INNER JOIN Food ON Diary.foodId = Food.foodId WHERE Diary.theDate = ? " +
            "AND User.userName = ? GROUP BY Diary.foodId) as count";

    static final String SQL_STREAK = "SELECT COUNT(*) FROM (SELECT Food.foodId FROM Diary INNER JOIN User ON User.userId = Diary.userId " +
            "INNER JOIN Food ON Food.foodId = Diary.foodID WHERE theDate = ? AND User.userName = ? GROUP BY Food.foodID) as points";

    static final String SQL_POINTS = "SELECT points FROM User WHERE userName = ?";

    static final String SQL_SELECT_DIARY_ENTRY = "SELECT Diary.diaryId as diaryId, Diary.theDate as theDate, " +
            "User.userName as uname, User.userId as uid, Food.name as foodName, Food.foodId as fid FROM Diary " +
            "INNER JOIN Food ON Diary.foodId = Food.foodId INNER JOIN User ON User.userId = Diary.userId " +
            "WHERE theDate = ? AND uname = ? and foodName = ?";

    static final String SQL_SELECT_USER = "SELECT userId FROM User WHERE userName = ?";

    static final String SQL_USER = "SELECT * FROM User WHERE userName = ?";

    static final String SQL_SELECT_SPECIFIC_USER = "SELECT * FROM User WHERE userName = ?";

    static final String SQL_SELECT_FOOD = "SELECT * FROM Food WHERE name = ?";

    static final String SQL_SELECT_CURRENT_DIARY = "SELECT SUM(Food.sugar), SUM(Food.calories), SUM(Food.fat), SUM(Food.saturates), " +
            "SUM(Food.carbs), SUM(Food.salt), SUM(Food.protein) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE theDate = date('now') AND User.userName = ?";

    static final String SQL_SELECT_GOAL = "SELECT User.userName FROM Goals INNER JOIN User ON User.userId = Goals.userId WHERE User.userName = " +
            "?";

    static final String SQL_SELECT_SUGAR = "SELECT User.userName FROM Sugar INNER JOIN User ON User.userId = Sugar.userId WHERE User.userName = " +
            "?";

    static final String SQL_UPDATE_ALLOWANCE = "UPDATE Sugar SET allowance = ? WHERE userId = ?";
    static final String SQL_UPDATE_REDUCTION = "UPDATE Sugar SET reduction = ? WHERE userId = ?";

    static final String SQL_SELECT_GOALS = "SELECT Goals.targetSugar, Goals.targetCalories, Goals.targetSaturates, Goals.targetFat, " +
            "Goals.targetSalt, Goals.targetProtein, Goals.targetCarbs FROM Goals INNER JOIN User ON User.userId = Goals.userId WHERE " +
            "User.userName = ?";

    static final String SQL_SELECT_WEEKLY_SUGAR = "SELECT Sugar.allowance, Sugar.reduction FROM Sugar INNER JOIN User ON User.userId = " +
            "Sugar.userId WHERE User.userName = ?";

    static final String SQL_UPDATE_GOAL = "UPDATE Goals SET ? WHERE userId = ?";
    static final String SQL_UPDATE_SUGAR_GOAL = "UPDATE Goals SET targetSugar = ? WHERE userId = ?";
    static final String SQL_UPDATE_CALORIES_GOAL = "UPDATE Goals SET targetCalories = ? WHERE userId = ?";
    static final String SQL_UPDATE_SATURATES_GOAL = "UPDATE Goals SET targetSaturates = ? WHERE userId = ?";
    static final String SQL_UPDATE_FAT_GOAL = "UPDATE Goals SET targetFat = ? WHERE userId = ?";
    static final String SQL_UPDATE_PROTEIN_GOAL = "UPDATE Goals SET targetProtein = ? WHERE userId = ?";
    static final String SQL_UPDATE_CARBS_GOAL = "UPDATE Goals SET targetCarbs = ? WHERE userId = ?";
    static final String SQL_UPDATE_SALT_GOAL = "UPDATE Goals SET targetSalt = ? WHERE userId = ?";

    static final String SQL_GROUP_SUM = "SELECT SUM(Food.sugar), SUM(Food.calories), SUM(Food.fat), SUM(Food.saturates)," +
            " SUM(Food.carbs), SUM(Food.salt), SUM(Food.protein) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            " User ON User.userId = Diary.userId WHERE theDate = ? AND User.userName = ?";

    static final String SQL_GROUP_QUANTITY = "SELECT Food.name, Food.sugar, Food.calories, Food.fat, Food.salt, Food.saturates, Food.protein, " +
    "Food.carbs, COUNT(*) as quantity FROM Food INNER JOIN Diary ON Food.foodId = Diary.foodId " +
    "INNER JOIN User ON User.userId = Diary.userId WHERE theDate = ? AND User.userName = ? GROUP BY Food.foodId ORDER BY quantity";

}
