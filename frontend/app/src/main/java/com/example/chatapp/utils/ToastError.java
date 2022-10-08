package com.example.chatapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.chatapp.API.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class ToastError {
    Context context;

    public ToastError(Context context) {
        this.context = context;
    }

    public void showMessage(String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public void showMessage(ResponseBody responseBody){
        Gson gson = new Gson();
        Type type = new TypeToken<ErrorResponse>() {}.getType();
        ErrorResponse errorResponse = gson.fromJson(responseBody.charStream(),type);

        Toast.makeText(context,errorResponse.getMessage(),Toast.LENGTH_LONG).show();
    }
}
