package com.example.eventmanagement;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ManageEventApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

    }
}