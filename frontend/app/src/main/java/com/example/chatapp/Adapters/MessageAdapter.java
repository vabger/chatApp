package com.example.chatapp.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.API.Chat.Message;
import com.example.chatapp.API.User.User;
import com.example.chatapp.R;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    User currentUser;
    List<Message> messageList;

    public MessageAdapter(User currentUser, List<Message> messageList) {
        this.currentUser = currentUser;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType==1){
            view = inflater.inflate(R.layout.message_sender,parent,false);
        }
        else{
            view = inflater.inflate(R.layout.message_receiver,parent,false);
        }

        return new ViewHolder(view,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.content.setText(messageList.get(position).getContent());
    }

    @Override
    public int getItemViewType(int position) {
        if(!Objects.equals(messageList.get(position).getSender().getId(), currentUser.getId())){
            return 0;
        }
        else{
            return 1;
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        public ViewHolder(@NonNull View itemView,int viewType) {
            super(itemView);
            Log.i("view type", Integer.toString(viewType));
            if(viewType==1){
                content = itemView.findViewById(R.id.senderTextView);
            }
            else{
                content = itemView.findViewById(R.id.receiverTextView);
            }
        }
    }

    public List<Message> getMessageList() {
        return messageList;
    }
}
