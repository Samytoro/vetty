package com.example.vetty;

import android.app.Application;

public class VettyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(() -> {
            UserPreferencesManager.getInstance(this);
        }).start();
    }
}