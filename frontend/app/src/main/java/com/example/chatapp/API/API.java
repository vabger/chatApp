package com.example.chatapp.API;

import com.example.chatapp.API.Auth.AuthApi;
import com.example.chatapp.Config.RetrofitClient;

public class API {
    private static AuthApi authApi;

    public static AuthApi getAuthApi(){
        if(authApi==null){
            authApi = RetrofitClient.getInstance().create(AuthApi.class);
        }
        return authApi;
    }
}
