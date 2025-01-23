package com.example.dialekto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class QuizScore extends AppCompatActivity {

    TextView scores, details, congrats, mess;
    Button backtoLesson;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_score);

        scores = findViewById(R.id.score);
        backtoLesson = findViewById(R.id.GobackLesson);
        details = findViewById(R.id.over);
        congrats = findViewById(R.id.congrats);
        mess = findViewById(R.id.mess);

        Intent intent = getIntent();
        double percentage = intent.getDoubleExtra("percentage", 0); // Default value is 0.0 if not found
        int totalQuestions = intent.getIntExtra("totalQuestions", 0); // Default value is 0 if not found
        int correctAnswers = intent.getIntExtra("correctAnswers", 0);

        // Format percentage to one decimal place
        String percentageText = String.format("%.1f", percentage) + "%";
        scores.setText("Your Score " + percentageText);

        // Change text color based on percentage
        if (percentage < 75) {
            scores.setTextColor(Color.RED); // Set color to red if percentage is below 75%
            congrats.setText("Failed!");
            mess.setText("You need to retake the\nquiz passing score is 75%.");
        } else {
            scores.setTextColor(getColor(R.color.score)); // Set color to black otherwise
        }


        // Create the details text with styled spans
        String detailsText = "You attempted " + totalQuestions + " questions and\n" +
                "you got " + correctAnswers + " correct answer.";

        SpannableString spannableString = new SpannableString(detailsText);

        // Define placeholders
        String totalQuestionsString = String.valueOf(totalQuestions);
        String correctAnswersString = String.valueOf(correctAnswers);

        /// Apply color to placeholders
        applySpanToText(spannableString, "attempted " + totalQuestionsString, Color.BLUE);
        applySpanToText(spannableString, "got " + correctAnswersString, Color.GREEN);

        // Apply styled text to TextView
        details.setText(spannableString);

        backtoLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizScore.this, DashboardLogin.class));
            }
        });
    }
    public void onBackPressed() {
        // Check if the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If logged in, go to the home screen (launcher activity) of the phone
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        } else {
            // If not logged in, proceed with default back button behavior
            super.onBackPressed();
        }
    }
    private void applySpanToText(SpannableString spannableString, String textToColor, int color) {
        int startIndex = spannableString.toString().indexOf(textToColor);
        if (startIndex >= 0) {
            int endIndex = startIndex + textToColor.length();
            // Check if the span already exists before applying a new one
            if (spannableString.getSpans(startIndex, endIndex, ForegroundColorSpan.class).length == 0) {
                spannableString.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            // Log if text to color is not found
            Log.e("QuizScore", "Text not found for coloring: " + textToColor);
        }
    }
}
