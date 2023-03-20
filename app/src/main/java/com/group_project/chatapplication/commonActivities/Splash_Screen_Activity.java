package com.group_project.chatapplication.commonActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.registration.Registration_Activity;

public class Splash_Screen_Activity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        isConnected();
    }

    private void isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if ((activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) || (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        auth = FirebaseAuth.getInstance();
                        if (auth.getCurrentUser() != null) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), Registration_Activity.class));
                        }
                        finish();
                    }
                }, 2500);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(getApplicationContext(), No_Internet_Check_Activity.class));
                    finish();
                }
            }, 2500);
        }
    }
}