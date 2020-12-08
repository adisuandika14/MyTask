package com.example.mytask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.mytask.Fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                boolean isLoggedIn = userPref.getBoolean("isLoggedIn", false);
                //boolean isLoggedIn = false;
                if (!isLoggedIn) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else
                    isFirstTime();
            }
        }, 1500);
    }


    public void isFirstTime(){
        SharedPreferences preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime",true);
        if(isFirstTime){
            SharedPreferences.Editor editor = preferences.edit();
            //isFirstTime = false;
            editor.putBoolean("isFirstTime", false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }else{
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        }
    }
}

