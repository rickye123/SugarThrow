package com.example.richa.sugarthrow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class HelpActivity extends MainActivity {

    private String username, previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                username = "Username";
            }
            else {
                username = extras.getString("username");
                previousActivity = extras.getString("activity");

            }
        }
        else {
            username = (String)savedInstanceState.getSerializable("username");
            previousActivity = (String)savedInstanceState.getSerializable("activity");
        }

        startContent();


    }

    private void startContent() {

        setContentView(R.layout.help_activity);
        setNavigationUsername(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_help);

        ImageView nutritionix = (ImageView)findViewById(R.id.nutritionix);
        nutritionix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent();
                redirect.setAction(Intent.ACTION_VIEW);
                redirect.addCategory(Intent.CATEGORY_BROWSABLE);
                redirect.setData(Uri.parse("https://www.nutritionix.com/business/api"));
                startActivity(redirect);
            }
        });

    }
}
