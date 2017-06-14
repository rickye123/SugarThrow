package com.example.richa.sugarthrow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FeedbackActivityPopup extends AppCompatActivity {

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                message = "No message";
            }
            else {
                message = extras.getString("message");
            }
        }
        else {
            message = (String)savedInstanceState.getSerializable("message");
        }

        setContentView(R.layout.feedback_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.5));

        TextView feedbackMessage = (TextView)findViewById(R.id.feedback_message);

        feedbackMessage.setText(message);

        Button yes = (Button)findViewById(R.id.yes_sign_out);
        Button no = (Button)findViewById(R.id.no_remain);

        yes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });

        no.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });

    }

}
