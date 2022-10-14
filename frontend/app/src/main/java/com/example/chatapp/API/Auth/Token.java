package com.example.chatapp.API.Auth;

import com.example.chatapp.API.User.User;
import com.google.gson.annotations.SerializedName;

public class Token {
    String accessToken;
    String refreshToken;

    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
