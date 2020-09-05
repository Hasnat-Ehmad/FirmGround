package com.firmground.evs.firmground.splashscreen;

/**
 * Created by HP on 2/16/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.firmground.evs.firmground.activity.LoginActivity;
import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.MainActivity;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.contains("yes_login")){
            if (sharedPreferences.getString("yes_login","").equals("yes")){

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();

            }else {
                setContentView(R.layout.activity_splash);
                int SPLASH_TIME_OUT = 1500;
                new Handler().postDelayed(new Runnable() {

                    /*
                     * Showing splash screen with a timer. This will be useful when you
                     * want to show case your app logo / company
                     */

                    @Override
                    public void run() {

                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        if (sharedPreferences.contains("yes_login")){

                            if (sharedPreferences.getString("yes_login","").equals("yes")){
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }else {
                                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }else {
                            Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }, SPLASH_TIME_OUT);

            }
        }else {
            setContentView(R.layout.activity_splash);
            
            int SPLASH_TIME_OUT = 1500;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (sharedPreferences.contains("yes_login")){

                        if (sharedPreferences.getString("yes_login","").equals("yes")){
                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }else {
                            Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }else {
                        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);
        }
    }

    SharedPreferences sharedPreferences;
    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}