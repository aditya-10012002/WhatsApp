package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsapp.MainActivity.mAuth;
import static com.google.firebase.auth.PhoneAuthProvider.getInstance;

public class OtpActivity extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String TAG = "OtpActivity";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    //private FirebaseAuth mAuth;
    String mobile;
    EditText otpEditText, userName;
    CircleImageView profileImage;
    private FirebaseDatabase firebaseDatabase;
    final int requestCode = 1;
    final int pickImageResource = 2;
    Uri imagePath;
    String imageUrl;
    Button verify, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        otpEditText = findViewById(R.id.otpEditText);
        userName = findViewById(R.id.txtUserName);
        profileImage = findViewById(R.id.profile_image);
        verify = findViewById(R.id.button2);
        btnSave = findViewById(R.id.btnSave);
        try {
            Intent intent = getIntent();
            mobile = intent.getStringExtra("mobile");
        } catch (Exception e) {
            Log.i("erroris", e.getMessage());
        }
        //mAuth = FirebaseAuth.getInstance();


        firebaseDatabase = FirebaseDatabase.getInstance();

        applyCallBack();
        generateotp();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted()) {
                    chooseImage();
                } else {
                    requestPermission();
                }

                try {
                    if(imagePath != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                        profileImage.setImageBitmap(bitmap);
                    } else {
                        Log.w(TAG, "bitmap is the ass fucking problem bro!!!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void generateotp() {

        try {
            getInstance().verifyPhoneNumber(
                    mobile,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        } catch (Exception e) {
            Log.i("erroris", e.getMessage());
        }
    }

    public void applyCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                Toast.makeText(OtpActivity.this, "Verification completed", Toast.LENGTH_SHORT).show();

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.i(TAG, "SMS quota");
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
                Toast.makeText(OtpActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            otpEditText.setVisibility(View.GONE);
                            verify.setVisibility(View.GONE);
                            profileImage.setVisibility(View.VISIBLE);
                            userName.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.VISIBLE);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void verifyCode(View view) {
        String code = otpEditText.getText().toString();
        PhoneAuthCredential credential;

        try {
            if (code != null) {
                credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void saveInfo(View view) {
        DatabaseReference ref = firebaseDatabase.getReference("Users");

        FirebaseUser user1 = mAuth.getCurrentUser();
        String user_name = userName.getText().toString();

        uploadImage();
        User user = new User(user_name, user1.getUid().toString(), imageUrl, "offline", user1.getPhoneNumber().toString());
        ref.child(user.id).setValue(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
    }

    public void uploadImage() {
        if (imagePath != null) {
            final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            final StorageReference reference = firebaseStorage.getReference("images/" + UUID.randomUUID().toString());
            reference.putFile(imagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(OtpActivity.this, "Profile pic uploaded", Toast.LENGTH_SHORT).show();
                            imageUrl = reference.getName();

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUrl = String.valueOf(uri);
                                    DatabaseReference reference1 = firebaseDatabase.getReference("Users");

                                    reference1.child(mAuth.getCurrentUser().getUid()).child("profilePicUrl").setValue(imageUrl);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else
                return false;
        } else {
            return true;
        }
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission", "Permission Granted");
                chooseImage();
            }
        } else {
            Log.i("permission", "not granted");
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Picture"), pickImageResource);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pickImageResource && resultCode == RESULT_OK) {
            imagePath = data.getData();
            profileImage.setImageURI(imagePath);

        }
    }

    private void status(final String status)  {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid()).child("status").setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("online", status);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("online", status);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            status("online");
            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
