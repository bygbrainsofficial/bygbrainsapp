package com.sahilhans0605.bygbrains.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sahilhans0605.bygbrains.R;
import com.sahilhans0605.bygbrains.databinding.ActivityUserDetailsBinding;

import java.util.concurrent.TimeUnit;

public class UserDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivityUserDetailsBinding binding;
    String choice;
    FirebaseAuth auth;
    ProgressDialog dialog2;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog2 = new ProgressDialog(this);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setMessage("Fetching OTP...");
        auth=FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(this);
        binding.getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.userName.getText().toString().equals("") || binding.phoneNumber.getText().toString().equals("") || binding.userAge.getText().toString().equals("") || binding.spinner.toString().equals("Select Gender")) {
                    Toast.makeText(UserDetails.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (binding.phoneNumber.getText().toString().length() < 10) {
                    binding.phoneNumber.setError("Invalid phone number");
                } else {
                    dialog2.show();
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(binding.countryCodePicker.getSelectedCountryCodeWithPlus()+binding.phoneNumber.getText().toString().trim())
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(UserDetails.this).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(UserDetails.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    dialog2.dismiss();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    dialog2.dismiss();
                                    Toast.makeText(UserDetails.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                    verificationId = s;

                                    Intent intent = new Intent(UserDetails.this, PhoneVerification.class);
                                    intent.putExtra("name", binding.userName.getText().toString().trim());
                                    intent.putExtra("age", binding.userAge.getText().toString().trim());
                                    intent.putExtra("gender", choice);
                                    intent.putExtra("verificationId", verificationId);
                                    intent.putExtra("phoneNumber", binding.countryCodePicker.getSelectedCountryCodeWithPlus() + binding.phoneNumber.getText().toString().trim());
                                    startActivity(intent);

                                }
                            }).build();
                    PhoneAuthProvider.verifyPhoneNumber(options);


                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        choice = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}