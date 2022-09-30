package com.sahilhans0605.bygbrains.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sahilhans0605.bygbrains.databinding.ActivityPhoneVerificationBinding;
import com.sahilhans0605.bygbrains.modelClass.User;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PhoneVerification extends AppCompatActivity {

    ActivityPhoneVerificationBinding binding;
    FirebaseAuth auth;
    String verificationId;
    ProgressDialog dialog2;
    ProgressDialog dialog3;
    private CountDownTimer countDownTimer;
    long timeLeftInMilliSeconds = 60000;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    String password;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toast.makeText(this, "OTP is valid for 60 seconds!", Toast.LENGTH_SHORT).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialog2 = new ProgressDialog(this);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setMessage("Setting up your profile...");
        auth = FirebaseAuth.getInstance();
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String name = getIntent().getStringExtra("name");
        String age = getIntent().getStringExtra("age");
        String gender = getIntent().getStringExtra("gender");
        verificationId = getIntent().getStringExtra("verificationId");
        String userId = getIntent().getStringExtra("userId");
        password = getIntent().getStringExtra("password");
        binding.wecomeName.setText("WELCOME " + name);
        binding.verifyPhone.setText("Verify " + phoneNumber);
        password = EncryptMessage(password);


        binding.verifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.otpEnter.getText().toString().trim().length() < 6) {
                    binding.otpEnter.setError("Please enter a valid OTP!");
                } else if (binding.otpEnter.getText().toString().isEmpty()) {
                    binding.otpEnter.setError("It can't be empty!");
                } else {
                    dialog2.show();
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, binding.otpEnter.getText().toString().trim());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.verifybutton.setVisibility(View.INVISIBLE);
                            if (task.isSuccessful()) {
                                User users = new User(name, age, gender, phoneNumber, task.getResult().getUser().getUid(), userId, password);

                                firebaseFirestore.collection("users").document(userId).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog2.dismiss();
                                            Intent intent = new Intent(PhoneVerification.this, BasicQuestions.class);
                                            intent.putExtra("name", name);
                                            startActivity(intent);
                                            finishAffinity();
                                        } else {
                                            Toast.makeText(PhoneVerification.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            dialog2.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(PhoneVerification.this, "Failed!", Toast.LENGTH_SHORT).show();
                                dialog2.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String EncryptMessage(String myMsg) {
        byte[] encryptedMsg = new byte[0];
        encryptedMsg = myMsg.getBytes(StandardCharsets.UTF_8);
        String myEncodeMsg = Base64.getEncoder().encodeToString(encryptedMsg);
        return myEncodeMsg;
    }


}