package com.example.chatapp.API.User;

import com.example.chatapp.API.Auth.Phone;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserApi {

    @GET("user/")
    Call<User> getCurrentUser();

    @POST("user/search")
    Call<Users> getUsers(@Body PhoneNumbers phoneNumbers);

    @POST("user/update")
    Call<User> updateAvatar(@Part MultipartBody.Part newAvatar);

    @PUT("user/name")
    Call<User> updateUsername(@Query("username") String username);

}
