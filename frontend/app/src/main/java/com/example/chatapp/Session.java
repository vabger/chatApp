package com.example.chatapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.chatapp.API.Auth.Token;

import java.util.concurrent.TimeUnit;

public class Session {
    Token token;
    SharedPreferences preferences;
    static Session session;

    private Session(){
        preferences = MyApplication.getContext().getSharedPreferences("token",MODE_PRIVATE);
        getTokenFromPrefs();
    }

    public Token getToken() {
        return token;
    }

    public static Session getInstance(){
        if(session==null){
            session = new Session();
        }
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
