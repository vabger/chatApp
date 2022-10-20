package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.API.Chat.Chat;
import com.example.chatapp.Config.SocketIoClient;
import com.example.chatapp.R;
import com.example.chatapp.Session;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class ChatRoom extends Fragment {

    Chat chat;



    public ChatRoom(Chat chat){
        this.chat = chat;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        JSONObject userId = new JSONObject();
//        try {
//            userId.put("_id", Session.getInstance().getUser().getId());
//            SocketIoClient.getSocket().emit("setup",userId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(chat!=null){
            Gson gson =new Gson();
            try{
                JSONObject chatJson = new JSONObject(gson.toJson(chat));
                SocketIoClient.getSocket().emit("join chat",chatJson);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        SocketIoClient.getSocket().on("message recieved",onMessageReceived);
        SocketIoClient.getSocket().on("connected",onConnected);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

}