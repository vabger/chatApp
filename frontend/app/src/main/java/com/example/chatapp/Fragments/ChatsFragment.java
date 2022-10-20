package com.example.chatapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.API.API;
import com.example.chatapp.API.Chat.Chat;
import com.example.chatapp.API.Chat.Chats;
import com.example.chatapp.Activities.ChatRoomActivity;
import com.example.chatapp.Adapters.ChatsAdapter;
import com.example.chatapp.R;
import com.example.chatapp.utils.FailedRequestHandler;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsFragment extends Fragment {


    private void setupChatsUI(View view, ArrayList<Chat> chats){
        RecyclerView chatsRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        chatsRecyclerView.setAdapter(new ChatsAdapter(chats, new ChatsAdapter.OnChatClickListener() {
            @Override
            public void onChatClick(View v, int pos) {
                Intent intent = new Intent(getContext(),ChatRoomActivity.class);
                intent.putExtra("chat",chats.get(pos));
                startActivity(intent);
            }
        }));
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chats_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Call<Chats> call = API.getChatApi().getChats();
        call.enqueue(new Callback<Chats>() {
            @Override
            public void onResponse(Call<Chats> call, Response<Chats> response) {
                if(!response.isSuccessful()){
                    new FailedRequestHandler(getContext(), response.code(), response.errorBody()).handle();
                    return;
                }
                Chats chats = response.body();
                setupChatsUI(view,chats.getChats());

            }

            @Override
            public void onFailure(Call<Chats> call, Throwable t) {
                Log.e("Failed request",t.getMessage());
            }
        });



    }

}