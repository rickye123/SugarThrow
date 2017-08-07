package com.example.richa.sugarthrow;

/*
This is the class responsible for handling the Unity game. The Unity game is embedded
into the app, and therefore no variables can be passed between the two.
 */

import com.unity3d.player.UnityPlayer;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class UnityGame extends MainActivity {

    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private String username, previousActivity;

	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

		setContentView(R.layout.unity_game_activity);
		setNavigationUsername(username);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if(getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
		createDrawer(toolbar);
		createNavigationView(R.id.nav_game);

		startGame();

	}

    // start game if user has at least 7 foods logged
	private void startGame() {

        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

        ImageView button = (ImageView)findViewById(R.id.exit_game);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnityPlayer.quit();
                launchActivity(MainActivity.class);
            }
        });

        mUnityPlayer = new UnityPlayer(this);
        FrameLayout layout = (FrameLayout)findViewById(R.id.game_frame);
        layout.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();
    }

	// Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
