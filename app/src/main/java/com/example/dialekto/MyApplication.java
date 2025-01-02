package com.example.dialekto;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
    }
}
