package com.example.chatapp.API.Chat;

import com.example.chatapp.API.User.User;
import com.example.chatapp.Session;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    @SerializedName("_id")
    String chatId;
    String chatName;
    Boolean isGroupChat;
    List<User> users;
    String chatImage;
    String latestMessage;

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        if(chatName.equals("sender")){
            if(users.get(0).getId().equals(Session.getInstance().getCurrentUser().getId())){
                chatName = users.get(1).getUsername();
            }
            else{
                chatName = users.get(0).getUsername();
            }
        }
        return chatName;
    }

    public Boolean getGroupChat() {
        return isGroupChat;
    }

    public List<User> getUsers() {
        return users;
    }

    public String getChatImage() {
        return chatImage;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + chatId + '\'' +
                ", chatName='" + chatName + '\'' +
                ", isGroupChat=" + isGroupChat +
                ", users=" + users +
                ", chatImage='" + chatImage + '\'' +
                ", latestMessage=" + latestMessage +
                '}';
    }
}
