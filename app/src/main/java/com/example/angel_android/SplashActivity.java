package com.example.angel_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //start main screen after 2seconds
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                //start main screen
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();//finish this activity
            }
        },2000); //2000 means 2 seconds
    }

}