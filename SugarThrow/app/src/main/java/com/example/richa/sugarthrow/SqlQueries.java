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
            "User.userName = ? GROUP BY Diary.foodId";

}
