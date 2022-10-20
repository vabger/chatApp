package com.example.chatapp.API.Chat;

import com.example.chatapp.API.User.User;

import java.io.Serializable;

public class Message implements Serializable {
    User sender;
    String content;
    Chat chat;



    public User getSender() {
        return sender;
    }

    public Chat getChat() {
        return chat;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", content='" + content + '\'' +
                ", chat=" + chat +
                '}';
    }
}
