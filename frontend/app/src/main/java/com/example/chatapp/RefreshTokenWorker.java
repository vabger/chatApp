package com.example.chatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.chatapp.API.API;
import com.example.chatapp.API.Auth.Token;
import com.example.chatapp.utils.ToastError;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class RefreshTokenWorker extends Worker {

    SharedPreferences sharedPreferences;
    public RefreshTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        sharedPreferences = getApplicationContext().getSharedPreferences("token",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Result doWork() {
        if(getRunAttemptCount()>5){
            return Result.failure();
        }

        Token currToken = Session.getInstance().getToken();
        Call<Token> call = API.getAuthApi().refreshToken(currToken);

        Response<Token> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
        if(response.isSuccessful()&&response.body()!=null){
            Token token = response.body();
            sharedPreferences
                    .edit()
                    .putString("accessToken",token.getAccessToken())
                    .putString("refreshToken",token.getRefreshToken())
                    .apply();
            return Result.success();
        }
        sharedPreferences.edit().clear().apply();
        return Result.retry();

    }



}
