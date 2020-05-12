package com.example.whatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.MessageActivity;
import com.example.whatsapp.Model.User;
import com.example.whatsapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> {

    static ArrayList<User> users;
    static Context context;

    public CallsAdapter(Context ctx, ArrayList<User> users) {
        this.context = ctx;
        this.users = users;
    }

    @NonNull
    @Override
    public CallsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.calls_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallsAdapter.ViewHolder holder, int i) {

        User user = users.get(i);
        holder.txtUserName.setText(user.name);
        Glide.with(context).load(user.profilePicUrl).into(holder.userProfilePic);

        if(user.status.toLowerCase().equals("online")) {
            holder.userOnline.setVisibility(View.VISIBLE);
            holder.userOffline.setVisibility(View.GONE);
        } else if(user.status.toLowerCase().equals("offline")) {
            holder.userOnline.setVisibility(View.GONE);
            holder.userOffline.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserName, txtLastMessage;
        CircleImageView userProfilePic, userOnline, userOffline;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txtUserName1);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage1);
            userProfilePic = itemView.findViewById(R.id.userProfilePic1);
            userOffline = itemView.findViewById(R.id.userOffline1);
            userOnline = itemView.findViewById(R.id.userOnline1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("userId", users.get(getAdapterPosition()).id);
                    context.startActivity(intent);
                }
            });

        }
    }
}
