package com.muththamizh.tamily.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.ui.activities.admin.Dashboard;
import com.muththamizh.tamily.ui.activities.admin.Login;
import com.muththamizh.tamily.utils.Constants;
import com.muththamizh.tamily.utils.PrefManager;

import static com.muththamizh.tamily.utils.Constants.SPLASH_TIMEOUT;

public class Splash extends AppCompatActivity {

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefManager = new PrefManager(this);
        //FirebaseApp.initializeApp(this);
        runApp();
    }


    private void runApp(){
        if (Constants.USER_MODE.equalsIgnoreCase("user")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this,Categories.class));
                    onBackPressed();
                }
            },SPLASH_TIMEOUT);
        }else {
//            startActivity(new Intent(Splash.this, Login.class));
//            if (prefManager.isAdminLoggedIn(PrefManager.IS_ADMIN_LOGGED_IN)){
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(Splash.this, Dashboard.class));
//                        onBackPressed();
//                    }
//                },SPLASH_TIMEOUT);
//            }else {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(Splash.this, Login.class));
//                        onBackPressed();
//                    }
//                },SPLASH_TIMEOUT);
//            }
        }
    }





}