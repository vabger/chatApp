package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.blongho.country_data.World;

public class MainActivity extends AppCompatActivity {
    Fragment enterMobileFragment = new EnterMobileFragment();
    Fragment verifyOtpFragment = new VerifyOtpFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        World.init(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginfl,enterMobileFragment).commit();

    }

}