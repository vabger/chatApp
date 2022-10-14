package com.example.chatapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chatapp.API.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.MediaType;
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

}
