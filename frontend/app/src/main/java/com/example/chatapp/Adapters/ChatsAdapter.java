package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.AES;
import com.example.chatapp.API.Chat.Chat;
import com.example.chatapp.API.Chat.Message;
import com.example.chatapp.R;
import com.example.chatapp.Session;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{

    ArrayList<Chat> chats;
    OnChatClickListener chatClickListener;

    public interface OnChatClickListener{
        void onChatClick(View v,int pos);
    }


    public ChatsAdapter(ArrayList<Chat> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.chatClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(chats.get(position).getChatImage()).into(holder.avatar);

        AES aes = new AES();

        if(chats.get(position).getUsers().size()==2){
            if(chats.get(position).getUsers().get(0).getId().equals(Session.getInstance().getCurrentUser().getId())){
                aes.computeSharedKey(chats.get(position).getUsers().get(1).getPublicKey());
            }
            else{
                aes.computeSharedKey(chats.get(position).getUsers().get(0).getPublicKey());
            }
        }

        holder.heading.setText(chats.get(position).getChatName());


        String lastMessage = aes.decrypt(chats.get(position).getLatestMessage());
        if(lastMessage!=null){
            holder.sub.setText(lastMessage);
        }

        holder.view.setOnClickListener(v -> chatClickListener.onChatClick(v,position));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView heading;
        TextView sub;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            this.avatar = itemView.findViewById(R.id.avatarImageView) ;
            this.heading = itemView.findViewById(R.id.headingTextView);
            this.sub = itemView.findViewById(R.id.subTextView);
        }
    }
}
