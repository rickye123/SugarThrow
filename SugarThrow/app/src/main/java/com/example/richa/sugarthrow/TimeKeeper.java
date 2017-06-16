package com.example.richa.sugarthrow;

/*
This class is responsible for handling the Time (in particular)
the date.
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class TimeKeeper {

    /**
     * Get the current date as a formatted string
     * @return a formatted String of the date, e.g. "01-06-2017"
     */
    String getCurrentDate() {

        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        // pass the date into the calendar object
        cal.setTime(date);

        // get year, month, and day
        int year = cal.get(Calendar.YEAR);
        // months count from 0, so add 1
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // format the date in the form "DD-MM-YYYY"
        return String.format(Locale.ENGLISH, "%02d-%02d-%d", day, month, year);

    }

    String getCurrentTime() {

        Calendar time = Calendar.getInstance();
        time.getTime();
        int minutes = time.get(Calendar.MINUTE);
        int hours= time.get(Calendar.HOUR_OF_DAY);
        int seconds = time.get(Calendar.SECOND);

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d",
                hours, minutes, seconds);

    }

    /**
     * Method which converts the date format from "DD-MM-YYYY"
     * to "YYYY-MM-DD", so that SQL queries will execute properly
     * @param currentDate - the date that will be converted
     * @return the converted date in the form "YYYY-MM-DD"
     */
    String convertDateFormat(String currentDate) {

        // turn "30-05-2017" into "2017-05-30"
        Map<String, String> time = getDateVariables(currentDate);

        int day = Integer.parseInt(time.get("day"));
        int month = Integer.parseInt(time.get("month"));
        int year = Integer.parseInt(time.get("year"));

        // return the converted date as a formatted string
        return String.format(Locale.ENGLISH, "%d-%02d-%02d", year, month, day);

    }

    /**
     * Get the date variables from a String - split the contents of
     * the string into day, month, and year
     * @param currentDate - the currentDate string whose contents
     *                    are split into day, month, and year
     * @return a hashmap containing the day, month, and year of a String
     */
    Map<String, String> getDateVariables(String currentDate) {

        // day is the first 2 characters
        String day = "" + currentDate.charAt(0) + currentDate.charAt(1);
        // month is the 4th and 5th character
        String month = "" + currentDate.charAt(3) + currentDate.charAt(4);
        // year is the 7th - 10th character
        String year = "" + currentDate.charAt(6) + currentDate.charAt(7) +
                currentDate.charAt(8) + currentDate.charAt(9);

        Map<String, String> time = new HashMap<>();
        time.put("day", day);
        time.put("month", month);
        time.put("year", year);

        return time;
    }

    /**
     * Get the previous date String based on the String passed into
     * the function
     * @param currentDate - the current date String which will be changed
     * @return String representing the previous day
     */
    String getPrevDate(String currentDate) {

        // get the currentDate split into day, month, and year
        Map<String, String> time = getDateVariables(currentDate);

        int theDay = Integer.parseInt(time.get("day"));
        // month is reduced by 1 as month starts from 0
        int theMonth = Integer.parseInt(time.get("month")) - 1;
        int theYear = Integer.parseInt(time.get("year"));

        // create GregorianCalendar object containing the time
        Date date = new GregorianCalendar(theYear, theMonth, theDay).getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1); // previous date

        int newYear = c.get(Calendar.YEAR);
        // months count from 0, so add 1
        int newMonth = c.get(Calendar.MONTH) + 1;
        int newDay = c.get(Calendar.DAY_OF_MONTH);

        // return formatted string
        return String.format(Locale.ENGLISH, "%02d-%02d-%d", newDay, newMonth, newYear);
    }

    /**
     * Get the next date based on the current date String
     * @param currentDate - the current date which will be changed
     * @return the String representing the next date from the current date
     */
    String getNextDate(String currentDate) {

        // get the date variables
        Map<String, String> time = getDateVariables(currentDate);

        int theDay = Integer.parseInt(time.get("day"));
        // month is reduced by 1 as it counts from 0
        int theMonth = Integer.parseInt(time.get("month")) - 1;
        int theYear = Integer.parseInt(time.get("year"));
        Date date = new GregorianCalendar(theYear, theMonth, theDay).getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1); // next date

        int newYear = c.get(Calendar.YEAR);
        // months count from 0, so add 1
        int newMonth = c.get(Calendar.MONTH) + 1;
        int newDay = c.get(Calendar.DAY_OF_MONTH);

        // return formatted String
        return String.format(Locale.ENGLISH, "%02d-%02d-%d", newDay, newMonth, newYear);
    }

}
