package com.example.chatapp.API.Auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/sendOTP")
    Call<OtpHash> getOtp(@Body Phone phone);

    @POST("auth/verifyOTP")
    Call<Token> verifyOtp(@Body OtpVerificationBody otpVerificationBody);

    @POST("auth/refresh-token")
    Call<Token> refreshToken(@Body Token token);


}
