package com.example.whatsapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapters.UserAdapter;
import com.example.whatsapp.Model.Message;
import com.example.whatsapp.Model.User;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chats extends Fragment {

    RecyclerView recyclerView;
    ArrayList<User> users;
    UserAdapter userAdapter;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;
    FirebaseUser fuser;
    ArrayList<String> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Chats");
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chatsRecyclerView);
        users = new ArrayList<>();
        userIds = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fuser = auth.getCurrentUser();
        grabUserList(fuser);
        return view;
    }

    public void grabUserList(final FirebaseUser fuser) {
        DatabaseReference ref = firebaseDatabase.getReference("chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIds.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message chat = ds.getValue(Message.class);
                    if(chat.sender.equals(fuser.getUid())) {
                        userIds.add(chat.reciever);
                    }
                    if(chat.reciever.equals(fuser.getUid())) {
                        userIds.add(chat.sender);
                    }
                    fetchUsers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchUsers() {
        DatabaseReference ref = firebaseDatabase.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    for(String userId : userIds) {
                        if(userId.equals(user.id)) {
                            if(!userExists(userId)) {

                                users.add(user);
                            }
                        }
                    }

                }

                UserAdapter userAdapter = new UserAdapter(getContext(), users);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean userExists(String userId) {
        for(User user : users) {
            if(user.id.equals(userId))
                return true;
        }
        return false;
    }
}
