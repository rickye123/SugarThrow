package com.example.richa.sugarthrow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedbackActivityPopup extends AppCompatActivity {

    private String message;
    private boolean positive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                message = "No message";
                positive = true;
            }
            else {
                message = extras.getString("message");
                positive = extras.getBoolean("positive");
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

        feedbackMessage();

    }

    private void feedbackMessage() {

        TextView feedbackMessage = (TextView)findViewById(R.id.feedback_message);

        feedbackMessage.setText(message);

        ImageView close = (ImageView)findViewById(R.id.close_feedback);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView feedbackImage = (ImageView)findViewById(R.id.feedback_image);

        if(!positive) {
            feedbackImage.setImageResource(R.drawable.ic_error_black);
            feedbackImage.setColorFilter(getColor(R.color.removeRed));
        }


    }

}
