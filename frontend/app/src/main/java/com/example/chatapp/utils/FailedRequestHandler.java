package com.example.chatapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chatapp.API.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.ResponseBody;

public class FailedRequestHandler {
    Context context;
    ResponseBody responseBody;
    String response;
    int status;
    ToastError toastError;

    public FailedRequestHandler(Context context, int status,ResponseBody responseBody) {
        this.context = context;
        this.status = status;
        this.responseBody = responseBody;
        toastError = new ToastError(context);
        parseResponseBody();
    }

    public void handle(){
        if(status==400){
            handleBadRequest();
        }
        if (status == 401) {
            handleUnAuthorized();
        }
        if(status==403){
            handleForbidden();
        }
    }

    private void parseResponseBody() {
        Gson gson = new Gson();
        Type type = new TypeToken<ErrorResponse>() {}.getType();
        if(Objects.requireNonNull(responseBody.contentType()).toString().equals("application/json; charset=utf-8")){
            ErrorResponse errorResponse = gson.fromJson(responseBody.charStream(),type);
            response = errorResponse.getMessage();
        }
        else{
            try{
                response = responseBody.string();
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    private void handleBadRequest(){
        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
    }

    private void handleForbidden(){

    }

    private void handleUnAuthorized(){
        context.getSharedPreferences("token",Context.MODE_PRIVATE).edit().clear().apply();
        toastError.showMessage("Please try logging in again");
    }

}
