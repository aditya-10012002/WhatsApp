package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.Adapters.ChatAdapter;
import com.example.whatsapp.Model.Message;
import com.example.whatsapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profilePic;
    TextView txtUserName, txtMessage, txtUserOnline;
    ImageButton sendButton;
    Toolbar toolbar;
    String id;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    ArrayList<Message> messageArrayList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        id = getIntent().getStringExtra("userId");

        profilePic = findViewById(R.id.userProfileIcon);
        txtUserName = findViewById(R.id.txtUser_name);
        toolbar = findViewById(R.id.toolbar);
        txtMessage = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.btnSend);
        txtUserOnline = findViewById(R.id.userisOnline);
        mAuth = FirebaseAuth.getInstance();
        messageArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.messagesRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txtMessage.getText().toString();
                if(!message.equals("")) {

                    Message chat = new Message(mAuth.getCurrentUser().getUid(), id, message, false);
                    sendMessage(chat);
                } else {
                    Toast.makeText(MessageActivity.this, "Enter some Text", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("");
            }
        });

        try {
            setSupportActionBar(toolbar);
            setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getUserInfo();
        }
    }

    private void sendMessage(Message chat) {
        DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("chats").push().setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getUserInfo() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Users").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    if (user.profilePicUrl != null)
                        Glide.with(getApplicationContext()).load(user.profilePicUrl).into(profilePic);
                    else
                        profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                    txtUserName.setText(user.name);

                    txtUserOnline.setText(user.status);

                    getMessages(mAuth.getCurrentUser().getUid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMessages (final String userId) {
        final DatabaseReference reference = firebaseDatabase.getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageArrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Message chat = ds.getValue(Message.class);

                    if(chat != null && (chat.sender.equals(userId) && chat.reciever.equals(id) || chat.sender.equals(id) && chat.reciever.equals(userId))) {
                        messageArrayList.add(chat);
                    }
                }
                ChatAdapter chatAdapter = new ChatAdapter(getApplicationContext(), messageArrayList);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
