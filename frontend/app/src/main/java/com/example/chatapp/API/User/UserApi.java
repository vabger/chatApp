package com.example.chatapp.API.User;

import com.example.chatapp.API.Auth.Phone;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    @GET("user/")
    Call<User> getCurrentUser();

    @POST("user/search")
    Call<Users> getUsers(@Body PhoneNumbers phoneNumbers);

}
