package com.sahilhans0605.bygbrains.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilhans0605.bygbrains.databinding.ActivityStartingPageBinding;

import java.util.Objects;

public class StartingPage extends AppCompatActivity {

    ActivityStartingPageBinding binding;
    FirebaseUser user;
    FirebaseDatabase db;
    ProgressDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartingPageBinding.inflate(getLayoutInflater());
        db = FirebaseDatabase.getInstance();
        dialog2 = new ProgressDialog(this);
        dialog2.setMessage("loading...");
        dialog2.setCanceledOnTouchOutside(false);
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            dialog2.show();
            DatabaseReference dbRef = db.getReference("BasicQuestions");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    System.out.println(snapshot1.getKey());
//                    System.out.println(FirebaseAuth.getInstance().getUid());
                        if (Objects.equals(snapshot1.getKey(), FirebaseAuth.getInstance().getUid())) {
                            dialog2.dismiss();
                            Intent intent = new Intent(StartingPage.this, EmotionRatingBarActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        binding.getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartingPage.this, UserDetails.class);
                startActivity(intent);
            }
        });

//        binding.login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(StartingPage.this,OTPSignIn.class);
//                startActivity(intent);
//            }
//        });
    }
}