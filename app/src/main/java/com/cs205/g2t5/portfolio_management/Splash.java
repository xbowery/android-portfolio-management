package com.cs205.g2t5.portfolio_management;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash screen which acts as an Activity which is shown before the Main Activity
 * screen is shown to the user.
 */
public class Splash extends AppCompatActivity {
    public final int SPLASH_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(getMainLooper()).postDelayed(() -> {
            //Do any action here. Now we are moving to next page
            Intent mySuperIntent = new Intent(Splash.this, MainActivity.class);
            startActivity(mySuperIntent);

            //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            finish();

        }, SPLASH_TIME);
    }
}
