package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.Activities.LoginActivity;
import com.example.chatapp.R;
import com.example.chatapp.Session;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Session.getInstance().isLoggedIn()){
            Intent intent = new Intent(this, ChatActivity.class);
            finish();
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

    }
}