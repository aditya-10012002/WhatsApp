package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.MessageActivity;
import com.example.whatsapp.Model.Message;
import com.example.whatsapp.Model.User;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {

    static ArrayList<Message> messages;
    static Context context;

    public ChatAdapter(Context ctx, ArrayList<Message> messages) {
        this.context = ctx;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        View view;
        if(i == 1) {
            view = inflater.inflate(R.layout.chat_row_right, viewGroup, false);
        } else {
            view = inflater.inflate(R.layout.chat_row_left, viewGroup, false);
        }

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.Holder holder, int i) {

        Message chat = messages.get(i);
        final ImageView view = holder.userProfilePic;

            holder.userMessage.setText(chat.message);
            if(getItemViewType(i) == 0) {
                FirebaseDatabase.getInstance().getReference("Users").child(chat.sender)
                        .child("profilePicUrl").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Glide.with(context).load(dataSnapshot.getValue()).into(view);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String id = firebaseUser.getUid();
        String senderId = messages.get(position).sender;

        if(id.equals(senderId))
            return 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView userMessage;
        CircleImageView userProfilePic;

        Holder(@NonNull View itemView) {
            super(itemView);

            userMessage = itemView.findViewById(R.id.userMessage);
            userProfilePic = itemView.findViewById(R.id.userImage);

        }
    }
}
