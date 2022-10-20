package com.example.chatapp.API.Auth;

import com.google.gson.annotations.SerializedName;

public class OtpVerificationBody {
    String hash;
    @SerializedName("phone")
    String mobileNumber;
    String otp;
    String publicKey;

    public OtpVerificationBody(String hash, String mobileNumber, String otp, String publicKey) {
        this.hash = hash;
        this.mobileNumber = mobileNumber;
        this.otp = otp;
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "OtpVerificationBody{" +
                "hash='" + hash + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", otp='" + otp + '\'' +
                '}';
    }
}
