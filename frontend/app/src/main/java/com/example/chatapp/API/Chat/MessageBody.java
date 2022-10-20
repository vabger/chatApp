package com.example.chatapp.API.Chat;

public class MessageBody {
    String chatId;
    String content;

    public MessageBody(String chatId, String content) {
        this.chatId = chatId;
        this.content = content;
    }
}
