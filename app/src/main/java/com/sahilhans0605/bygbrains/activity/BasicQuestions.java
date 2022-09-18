package com.sahilhans0605.bygbrains.activity;

import
        androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sahilhans0605.bygbrains.R;
import com.sahilhans0605.bygbrains.databinding.ActivityBasicQuestionsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BasicQuestions extends AppCompatActivity {
    ArrayList<com.sahilhans0605.bygbrains.modelClass.BasicQuestions> questions;
    ActivityBasicQuestionsBinding binding;
    int currentQuestionIndex = 0;
    com.sahilhans0605.bygbrains.modelClass.BasicQuestions question;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase db;
    ProgressDialog dialog;
    FirebaseUser user;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBasicQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        questions = new ArrayList<>();
        questions.add(new com.sahilhans0605.bygbrains.modelClass.BasicQuestions("Most of the time you feel?", "Good", "Stressed", "Sad", "Indifferent", "Happy", "Alright!,Let's proceed", "BQ1"));
        questions.add(new com.sahilhans0605.bygbrains.modelClass.BasicQuestions("What profession are you in?", "Student", "Job", "Self work", "HouseWife", "Other", ":) Awesome you are doing a great job!", "BQ2"));
        questions.add(new com.sahilhans0605.bygbrains.modelClass.BasicQuestions("You want to improve in", "Anxiety", "Depression", "Sleep", "Other Disorder", "Be happy", "Be relaxed let's work on this!", "BQ3"));

        setNextQuestion();

    }

    void setNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            question = questions.get(currentQuestionIndex);
            binding.questionD.setText(question.getQuestion());
            binding.option1D.setText(question.getOption1());
            binding.option2D.setText(question.getOption2());
            binding.option3D.setText(question.getOption3());
            binding.option4D.setText(question.getOption4());
            binding.option5D.setText(question.getOption5());
        }
    }

    String chosen = "";

    public void next(View view) {
        switch (view.getId()) {
            case R.id.option1D:
                chosen = binding.option1D.getText().toString();
                break;
            case R.id.option2D:
                chosen = binding.option2D.getText().toString();
                break;
            case R.id.option3D:
                chosen = binding.option3D.getText().toString();
                break;
            case R.id.option4D:
                chosen = binding.option4D.getText().toString();
                break;
            case R.id.option5D:
                chosen = binding.option5D.getText().toString();
                break;
        }

        switch (view.getId()) {

            case R.id.option1D:
            case R.id.option2D:
            case R.id.option3D:
            case R.id.option4D:
            case R.id.option5D:

                binding.interactionD.setVisibility(View.VISIBLE);
                binding.interactionD.setText(question.getInteraction());
                binding.buttonD.setVisibility(View.VISIBLE);
                break;


            case R.id.buttonD:

                binding.buttonD.setVisibility(View.INVISIBLE);
                binding.interactionD.setVisibility(View.INVISIBLE);
                dialog.show();
                Map<String, String> map = new HashMap<>();
                map.put(question.getQuestion(), chosen);
                db.getReference().child("BasicQuestions").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(question.getId()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
//                        Toast.makeText(BasicQuestions.this, "Added!!!!", Toast.LENGTH_SHORT).show();
                        currentQuestionIndex++;
                        setNextQuestion();
                        if (currentQuestionIndex == questions.size()) {
                            Intent intent = new Intent(BasicQuestions.this, EmotionRatingBarActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });


                break;
        }
    }
}