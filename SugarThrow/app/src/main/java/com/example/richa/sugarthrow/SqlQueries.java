package com.example.richa.sugarthrow;

/**
 * Created by richa on 31/05/2017.
 */

public class SqlQueries {

    public static final String SQL_REGULAR_FOOD = "SELECT User.userName AS uname, Diary.theDate as entryDate, Food.name " +
            "AS foodName, Food.foodId as fid, COUNT(*) AS quantity FROM Diary INNER JOIN " +
            "Food ON Diary.foodId = Food.foodId INNER JOIN User ON User.userID = Diary.userId " +
            "WHERE User.userName = ? GROUP BY Diary.foodId ORDER BY quantity DESC LIMIT 5";

    public static final String SQL_IN_DIARY= "SELECT Food.name as food, COUNT(*) AS quantity FROM Diary INNER JOIN " +
            "User ON Diary.userId = User.userId INNER JOIN Food ON Diary.foodId = Food.foodId WHERE Diary.theDate = ? " +
            "AND User.userName = ? GROUP BY Diary.foodId";

    public static final String SQL_SELECT_DIARY_ENTRY = "SELECT Diary.diaryId as diaryId, Diary.theDate as theDate, " +
            "User.userName as uname, User.userId as uid, Food.name as foodName, Food.foodId as fid FROM Diary " +
            "INNER JOIN Food ON Diary.foodId = Food.foodId INNER JOIN User ON User.userId = Diary.userId " +
            "WHERE theDate = ? AND uname = ? and foodName = ?";

    public static final String SQL_DELETE_FROM_DIARY = "DELETE FROM Diary WHERE diaryId = ?";

    public static final String SQL_SELECT_USER = "SELECT userId FROM User WHERE userName = ?";

    public static final String SQL_SELECT_FOOD = "SELECT * FROM Food WHERE name = ?";

    public static final String SQL_SUM_FOOD = "SELECT SUM(Food.?) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";

    public static final String SQL_SUM_OF_FOODS = "SELECT SUM(Food.calories), SUM(Food.sugar), SUM(Food.fat), SUM(Food.saturates), " +
            "SUM(Food.carbs), SUM(Food.salt), SUM(Food.protein) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";

    public static final String SQL_SUM_OF_FOOD_CALORIES = "SELECT SUM(Food.calories) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";
    public static final String SQL_SUM_OF_FOOD_SUGAR = "SELECT SUM(Food.sugar) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";
    public static final String SQL_SUM_OF_FOOD_FAT = "SELECT SUM(Food.fat) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";
    public static final String SQL_SUM_OF_FOOD_SATURATES = "SELECT SUM(Food.saturates) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";
    public static final String SQL_SUM_OF_FOOD_CARBS = "SELECT SUM(Food.carbs) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";
    public static final String SQL_SUM_OF_FOOD_SALT = "SELECT SUM(Food.salt) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";
    public static final String SQL_SUM_OF_FOOD_PROTEIN = "SELECT SUM(Food.protein) FROM Food INNER JOIN Diary ON Diary.foodId = Food.foodId INNER JOIN " +
            "User ON User.userId = Diary.userId WHERE User.userName = ? AND theDate = ?";


}
