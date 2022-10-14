package com.example.chatapp.API;

import com.example.chatapp.API.Auth.AuthApi;
import com.example.chatapp.API.User.User;
import com.example.chatapp.API.User.UserApi;
import com.example.chatapp.Config.RetrofitClient;

public class API {
    private static AuthApi authApi;
    private static UserApi userApi;

    public static AuthApi getAuthApi(){
        if(authApi==null){
            authApi = RetrofitClient.getInstance().create(AuthApi.class);
        }
        return authApi;
    }

    public static UserApi getUserApi(){
        if(userApi==null){
            userApi = RetrofitClient.getInstance().create(UserApi.class);
        }
        return userApi;
    }
}
