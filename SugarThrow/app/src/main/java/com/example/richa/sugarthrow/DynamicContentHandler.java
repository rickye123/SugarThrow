package com.example.richa.sugarthrow;

/*

 */

import android.widget.TextView;

class DynamicContentHandler {

    private TimeKeeper date = new TimeKeeper();

    void getFactContent(TextView fact) {

        if(date.getDay().equals("Sunday")) {
            fact.setText(R.string.fact_message_1);
        }
        else if(date.getDay().equals("Monday")) {
            fact.setText(R.string.fact_message_2);
        }
        else if(date.getDay().equals("Tuesday")) {
            fact.setText(R.string.fact_message_3);
        }
        else if(date.getDay().equals("Wednesday")) {
            fact.setText(R.string.fact_message_4);
        }
        else if(date.getDay().equals("Thursday")) {
            fact.setText(R.string.fact_message_5);
        }
        else if(date.getDay().equals("Friday")) {
            fact.setText(R.string.fact_message_6);
        }
        else {
            // saturday
            fact.setText(R.string.fact_message_7);
        }

    }

    void getTipContent(TextView tip1, TextView tip2) {
        if(date.getDay().equals("Sunday")) {
            tip1.setText(R.string.tip_message_1_p1);
            tip2.setText(R.string.tip_message_1_p2);
        }
        else if(date.getDay().equals("Monday")) {
            tip1.setText(R.string.tip_message_2_p1);
            tip2.setText(R.string.tip_message_2_p2);
        }
        else if(date.getDay().equals("Tuesday")) {
            tip1.setText(R.string.tip_message_3_p1);
            tip2.setText(R.string.tip_message_3_p2);
        }
        else if(date.getDay().equals("Wednesday")) {
            tip1.setText(R.string.tip_message_4_p1);
            tip2.setText(R.string.tip_message_4_p2);
        }
        else if(date.getDay().equals("Thursday")) {
            tip1.setText(R.string.tip_message_5_p1);
            tip2.setText(R.string.tip_message_5_p2);
        }
        else if(date.getDay().equals("Friday")) {
            tip1.setText(R.string.tip_message_6_p1);
            tip2.setText(R.string.tip_message_6_p2);
        }
        else {
            // saturday
            tip1.setText(R.string.tip_message_7_p1);
            tip2.setText(R.string.tip_message_7_p2);
        }

    }

    void getMealContent(TextView breakfast, TextView lunch, TextView dinner) {

        if(date.getDay().equals("Sunday")) {
            breakfast.setText(R.string.breakfast_example_1);
            lunch.setText(R.string.lunch_example_1);
            dinner.setText(R.string.dinner_example_1);
        }
        else if(date.getDay().equals("Monday")) {
            breakfast.setText(R.string.breakfast_example_2);
            lunch.setText(R.string.lunch_example_2);
            dinner.setText(R.string.dinner_example_2);
        }
        else if(date.getDay().equals("Tuesday")) {
            breakfast.setText(R.string.breakfast_example_3);
            lunch.setText(R.string.lunch_example_3);
            dinner.setText(R.string.dinner_example_3);
        }
        else if(date.getDay().equals("Wednesday")) {
            breakfast.setText(R.string.breakfast_example_4);
            lunch.setText(R.string.lunch_example_4);
            dinner.setText(R.string.dinner_example_4);
        }
        else if(date.getDay().equals("Thursday")) {
            breakfast.setText(R.string.breakfast_example_5);
            lunch.setText(R.string.lunch_example_5);
            dinner.setText(R.string.dinner_example_5);
        }
        else if(date.getDay().equals("Friday")) {
            breakfast.setText(R.string.breakfast_example_6);
            lunch.setText(R.string.lunch_example_6);
            dinner.setText(R.string.dinner_example_6);
        }
        else {
            // saturday
            breakfast.setText(R.string.breakfast_example_7);
            lunch.setText(R.string.lunch_example_7);
            dinner.setText(R.string.dinner_example_7);
        }

    }

}
