package com.example.chatapp.API.Chat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ChatApi {

    @GET("chat/")
    Call<Chats> getChats();

    @POST("messages/")
    Call<Messages> getMessages(@Body CurrentChat currentChat);

    @PUT("messages/")
    Call<Message> sendMessage(@Body MessageBody messageBody);

    @GET("chat/access")
    Call<Chat> accessChat(@Query("userId")String userId);

}
