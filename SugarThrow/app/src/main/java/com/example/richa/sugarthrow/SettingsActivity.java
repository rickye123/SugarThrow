package com.example.richa.sugarthrow;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends MainActivity {

/*    private Execute executeSQL;
    private List<List<String>> users;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_settings);

        Connector database = MainActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
        if (db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }

      //  Execute executeSQL = new Execute(database);

      //  List<List<String>> users = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_SPECIFIC_USER, "re16621");

        final LinearLayout profile = (LinearLayout)findViewById(R.id.profile_layout);
        clickLayout(profile);
        final LinearLayout notifications = (LinearLayout)findViewById(R.id.notifications_layout);
        clickLayout(notifications);
        final LinearLayout colour = (LinearLayout)findViewById(R.id.colour_layout);
        clickLayout(colour);
       // final LinearLayout signOut = (LinearLayout)findViewById(R.id.sign_out_layout);
        //clickLayout(signOut);

    }


    public void clickLayout(final LinearLayout layout) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dropDown = layout.getChildAt(1);
                if (dropDown.getVisibility() == View.GONE) {
                    dropDown.setVisibility(View.VISIBLE);
                    dropDown.setBackgroundResource(R.drawable.border_top);
                }
                else {
                    dropDown.setVisibility(View.GONE);
                }

            }
        });
    }

}
