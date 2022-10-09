package com.example.chatapp;

import android.app.Application;

public class MyApplication extends Application {

    static MyApplication context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = (MyApplication) getApplicationContext();
    }

    public static Application getContext(){
        return context;
    }

}
