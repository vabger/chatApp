package com.example.chatapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.chatapp.API.API;
import com.example.chatapp.API.Auth.Token;
import com.example.chatapp.API.User.User;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class Session {
    User currentUser;
    Token token;
    SharedPreferences preferences;
    static Session session = new Session();

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user){
        currentUser = user;
    }


    private Session(){
        preferences = MyApplication.getContext().getSharedPreferences("token",MODE_PRIVATE);
        getTokenFromPrefs();
    }

    public Token getToken() {
        return token;
    }

    public static Session getInstance(){
        return session;
    }

    public void setToken(Token token) {
        this.token = token;
        saveTokenToPrefs();
        scheduleRefreshTokenWork();
    }

    private void scheduleRefreshTokenWork(){
        WorkManager workManager = WorkManager.getInstance(MyApplication.getContext());

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(RefreshTokenWorker.class,15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        workManager.enqueueUniquePeriodicWork("refresh-token",ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest);

    }

    private void saveTokenToPrefs(){
        preferences
                .edit()
                .putString("accessToken",token.getAccessToken())
                .putString("refreshToken",token.getRefreshToken())
                .apply();
    }

    private void getTokenFromPrefs(){
        String accessToken = preferences.getString("accessToken",null);
        String refreshToken = preferences.getString("refreshToken",null);

        token = new Token(accessToken,refreshToken);

    }

    public Boolean isLoggedIn(){
        return token.getRefreshToken()!=null && token.getAccessToken()!=null;
    }


}
