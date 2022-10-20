package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.API.User.User;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> exists;
    List<String> phone;
    List<String> name;
    OnUserClickListener listener;

    public static interface OnUserClickListener{
        public void onUserClicked(int position,View view);
    }

    public UserAdapter(List<User> exists, List<String> phone, List<String> name,OnUserClickListener listener){
        this.exists = exists;
        this.phone = phone;
        this.name = name;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        if(position>=exists.size()){
            int newPos = position-exists.size();
            holder.heading.setText(name.get(newPos));
            holder.sub.setText(phone.get(newPos));
            holder.invite.setVisibility(View.VISIBLE);
            return;
        }
        User user = exists.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClicked(holder.getAdapterPosition(),v);
            }
        });
        holder.heading.setText(user.getUsername());
        holder.sub.setText(user.getPhone());
        Picasso.get().load(user.getAvatar()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return (phone.size()+exists.size());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView heading;
        TextView sub;
        TextView invite;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatarImageView) ;
            this.heading = itemView.findViewById(R.id.headingTextView);
            this.sub = itemView.findViewById(R.id.subTextView);
            this.invite = itemView.findViewById(R.id.inviteTextView);
            this.itemView = itemView;
        }
    }
}
