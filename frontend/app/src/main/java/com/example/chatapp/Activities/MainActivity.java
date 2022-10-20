package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatapp.API.API;
import com.example.chatapp.API.User.User;
import com.example.chatapp.Activities.ChatActivity;
import com.example.chatapp.Activities.LoginActivity;
import com.example.chatapp.Config.SocketIoClient;
import com.example.chatapp.R;
import com.example.chatapp.Session;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {



    Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Session.getInstance().isLoggedIn()){
            Call<User> call = API.getUserApi().getCurrentUser();
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        Session.getInstance().setCurrentUser(response.body());
                        Socket socket = SocketIoClient.getSocket();

                        Gson gson = new Gson();
                        try {
                            socket.emit("setup",new JSONObject(gson.toJson(Session.getInstance().getCurrentUser())));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        socket.on("connected",onConnected);


                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });

        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

    }
}