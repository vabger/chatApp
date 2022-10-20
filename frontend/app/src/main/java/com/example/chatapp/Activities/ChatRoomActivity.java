package com.example.chatapp.Activities;

import android.os.Bundle;

import com.example.chatapp.AES;
import com.example.chatapp.API.API;
import com.example.chatapp.API.Chat.Chat;
import com.example.chatapp.API.Chat.CurrentChat;
import com.example.chatapp.API.Chat.Message;
import com.example.chatapp.API.Chat.MessageBody;
import com.example.chatapp.API.Chat.Messages;
import com.example.chatapp.API.User.User;
import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Config.SocketIoClient;
import com.example.chatapp.Session;
import com.example.chatapp.utils.FailedRequestHandler;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chatapp.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity {
    Chat chat;
    User currentUser;
    Socket socket;
    EditText editText;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    AES aes;


    public void backButtonClicked(View view){
        finish();
    }


    public void sendMessageClicked(View view) {
        String content = editText.getText().toString();
        if (content.equals("")) {
            return;
        }

        Call<Message> call = API.getChatApi().sendMessage(new MessageBody(chat.getChatId(), aes.encrypt(content)));

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();
                    try {
                        socket.emit("new message", new JSONObject(gson.toJson(response.body())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addNewMessage(response.body());

                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    public void addNewMessage(Message message){
        message.setContent(aes.decrypt(message.getContent()));
        messageAdapter.getMessageList().add(message);
        messageAdapter.notifyItemInserted(messageAdapter.getItemCount());
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        editText.setText("");
    }


    public void fetchMessages(){
        Call<Messages> call = API.getChatApi().getMessages(new CurrentChat(chat.getChatId()));

        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {
                if(!response.isSuccessful()){
                    new FailedRequestHandler(ChatRoomActivity.this, response.code(),response.errorBody());
                    return;
                }
                setUpMessagesUI(response.body().getMessages());
            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                Log.i("Failure",t.getMessage());
            }
        });

    }

    public void setUpMessagesUI(List<Message> messages){
        for(Message m:messages){
            m.setContent(aes.decrypt(m.getContent()));
        }

        recyclerView = findViewById(R.id.messageRecyclerView);
        messageAdapter = new MessageAdapter(currentUser,messages);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.smoothScrollToPosition(messages.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        chat = (Chat)getIntent().getSerializableExtra("chat");

        aes = new AES();


        TextView chatName = findViewById(R.id.chatName);
        chatName.setText(chat.getChatName());
        ImageView chatImage = findViewById(R.id.chatImage);
        Picasso.get().load(chat.getChatImage()).into(chatImage);

        ImageButton imageButton = findViewById(R.id.chatBackButton);
        imageButton.setOnClickListener(this::backButtonClicked);

        ImageButton sendMessageButton = findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(this::sendMessageClicked);


        editText = findViewById(R.id.chatEditText);



        currentUser = Session.getInstance().getCurrentUser();

        if(chat.getUsers().size()==2){
            if(chat.getUsers().get(0).getId().equals(currentUser.getId())){
                aes.computeSharedKey(chat.getUsers().get(1).getPublicKey());
            }
            else{
                aes.computeSharedKey(chat.getUsers().get(0).getPublicKey());
            }
        }


        socket = SocketIoClient.getSocket();


        socket.emit("join chat",chat.getChatId());
        socket.on("message recieved",onMessageReceived);



        fetchMessages();






    }


    Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            Log.i("new message",jsonObject.toString());
            Gson gson = new Gson();
            Message message = gson.fromJson(String.valueOf(jsonObject),Message.class);
            Log.i("message",message.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("messageID",message.getChat().getChatId());
                    Log.i("chatId",chat.getChatId());
                    if(message.getChat().getChatId().equals(chat.getChatId())){
                        message.setContent(aes.decrypt(message.getContent()));
                        messageAdapter.getMessageList().add(message);
                        messageAdapter.notifyItemInserted(messageAdapter.getItemCount());
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                    }
                }
            });
        }
    };









}