package com.example.chatapp.API.Auth;

public class Token {
    String accessToken;
    String refreshToken;
    Long atExpiresIn;
    Long rtExpiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getAtExpiresIn() {
        return atExpiresIn;
    }

    public Long getRtExpiresIn() {
        return rtExpiresIn;
    }

    @Override
    public String toString() {
        return "Token{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", atExpiresIn=" + atExpiresIn +
                ", rtExpiresIn=" + rtExpiresIn +
                '}';
    }
}
