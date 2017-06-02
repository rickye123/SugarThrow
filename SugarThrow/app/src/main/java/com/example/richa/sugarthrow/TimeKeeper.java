package com.example.richa.sugarthrow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richa on 02/06/2017.
 */

public class TimeKeeper {

    /**
     *
     * @return
     */
    public String getCurrentDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        // months count from 0, so add 1
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String theDate = day + "-" + month + "-" + year;
        String formattedDate = theDate.format("%02d-%02d-%d", day, month, year);
        return formattedDate;

    }

    public String convertDateFormat(String currentDate) {

        // turn "30-05-2017" into "2017-05-30"
        Map<String, String> time = getDateVariables(currentDate);

        int day = Integer.parseInt(time.get("day"));
        int month = Integer.parseInt(time.get("month"));
        int year = Integer.parseInt(time.get("year"));

        String convertedDate = time.get("year") + "-" + time.get("month")
                + "-" +  time.get("day");
        String formattedDate = convertedDate.format("%d-%02d-%02d", year, month, day);

        return formattedDate;

    }

    public Map<String, String> getDateVariables(String currentDate) {

        String day = "" + currentDate.charAt(0) + currentDate.charAt(1);
        String month = "" + currentDate.charAt(3) + currentDate.charAt(4);
        String year = "" + currentDate.charAt(6) + currentDate.charAt(7) +
                currentDate.charAt(8) + currentDate.charAt(9);

        Map<String, String> time = new HashMap<String, String>();
        time.put("day", day);
        time.put("month", month);
        time.put("year", year);

        return time;
    }

    public String getPrevDate(String currentDate) {

        Map<String, String> time = getDateVariables(currentDate);

        int theDay = Integer.parseInt(time.get("day"));
        int theMonth = Integer.parseInt(time.get("month")) - 1;
        int theYear = Integer.parseInt(time.get("year"));
        Date date = new GregorianCalendar(theYear, theMonth, theDay).getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1); // previous date

        int newYear = c.get(Calendar.YEAR);
        // months count from 0, so add 1
        int newMonth = c.get(Calendar.MONTH) + 1;
        int newDay = c.get(Calendar.DAY_OF_MONTH);

        String prevDate = newDay + "-" + newMonth + "-" + newYear;
        String formattedDatePrev = prevDate.format("%02d-%02d-%d", newDay, newMonth, newYear);

        return formattedDatePrev;
    }

    public String getNextDate(String currentDate) {

        Map<String, String> time = getDateVariables(currentDate);

        int theDay = Integer.parseInt(time.get("day"));
        int theMonth = Integer.parseInt(time.get("month")) - 1;
        int theYear = Integer.parseInt(time.get("year"));
        Date date = new GregorianCalendar(theYear, theMonth, theDay).getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1); // previous date

        int newYear = c.get(Calendar.YEAR);
        // months count from 0, so add 1
        int newMonth = c.get(Calendar.MONTH) + 1;
        int newDay = c.get(Calendar.DAY_OF_MONTH);

        String nextDate = newDay + "-" + newMonth + "-" + newYear;
        String formattedDateNext = nextDate.format("%02d-%02d-%d", newDay, newMonth, newYear);

        return formattedDateNext;
    }

}
