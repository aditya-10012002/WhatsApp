package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.PhoneAuthProvider.*;

public class MainActivity extends AppCompatActivity {
    EditText mobileEditText;
    public static FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mobileEditText = findViewById(R.id.mobileEditText);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Log.i("Current user", mAuth.getCurrentUser().getUid());
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void gotootp(View view) {
        Intent intent = new Intent(MainActivity.this, OtpActivity.class);
        String mobile = String.valueOf(mobileEditText.getText());
        if(mobile.length() == 13) {
            intent.putExtra("mobile", mobile);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Enter a valid mobile number with country code", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
