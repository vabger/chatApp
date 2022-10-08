package com.example.chatapp.API.Auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("sendOTP")
    Call<OtpHash> getOtp(@Body MobileNumber mobileNumber);

    @POST("verifyOTP")
    Call<Token> verifyOtp(@Body OtpVerificationBody otpVerificationBody);



}
