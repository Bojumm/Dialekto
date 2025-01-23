package com.example.dialekto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EasyQuiz extends AppCompatActivity {

    private TextView optionA, optionB, optionC, optionD;

    private TextView questionNumber, question;
    private Button check, next;
    private TextView[] options;

    private int currentQuestionIndex = 0;
    private int selectedOptionIndex = -1; // -1 means no option selected
    private boolean isAnswerChecked = false;

    int correctAnswerss = 0; // Number of correct answers

    // Quiz data: Questions, Options, and Correct Answers
    private String[] questions = {
            "What is the Kapampangan translation of 'Mahal kita'?",
            "What is the Tagalog translation of 'Musta ka'?",
            "What is the Kapampangan translation of 'Salamat'?",
            "What is the Tagalog translation of 'Magandang aldo'?",
            "What is the Kapampangan translation of 'Paalam'?",
            "What is the Tagalog translation of 'Dakal a salamat'?",
            "What is the Kapampangan translation of 'Kamusta ka'?",
            "What is the Tagalog translation of 'Kaluguran daka'?",
            "What is the Kapampangan translation of 'Magandang umaga'?",
            "What is the Tagalog translation of 'Pamagbayu'?"
    };

    private String[][] optionsData = {
            {"Musta ka", "Dakal a salamat", "Kaluguran daka", "Pamagbayu"}, // "Mahal kita" (2)
            {"Kamusta ka", "Mahal kita", "Magandang gabi", "Salamat"},      // "Musta ka" (0)
            {"Musta ka", "Dakal a salamat", "Kaluguran daka", "Pamagbayu"}, // "Salamat" (1)
            {"Magandang umaga", "Paalam", "Salamat", "Magandang aldo"},     // "Magandang aldo" (3)
            {"Kaluguran daka", "Aldo", "Musta ka", "Pamagbayu"},            // "Paalam" (3)
            {"Mahal kita", "Salamat", "Kamusta ka", "Magandang araw"},      // "Dakal a salamat" (1)
            {"Dakal a salamat", "Aldo", "Musta ka", "Kaluguran daka"},      // "Kamusta ka" (2)
            {"Mahal kita", "Salamat", "Kamusta ka", "Magandang umaga"},     // "Kaluguran daka" (0)
            {"Magandang aldo", "Bengi", "Pamagbayu", "Auran"},              // "Magandang umaga" (0)
            {"Magandang gabi", "Paalam", "Salamat", "Mahal kita"}           // "Pamagbayu" (1)
    };

    private int[] correctAnswers = {
            2, // "Kaluguran daka" is the translation of "Mahal kita"
            0, // "Kamusta ka" is the translation of "Musta ka"
            1, // "Dakal a salamat" is the translation of "Salamat"
            1, // "Magandang umaga" is the translation of "Magandang aldo"
            3, // "Pamagbayu" is the translation of "Paalam"
            1, // "Salamat" is the translation of "Dakal a salamat"
            2, // "Musta ka" is the translation of "Kamusta ka"
            0, // "Mahal kita" is the translation of "Kaluguran daka"
            0, // "Magandang aldo" is the translation of "Magandang umaga"
            1  // "Paalam" is the translation of "Pamagbayu"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_quiz);

        // Initialize options
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        questionNumber = findViewById(R.id.QuestionNumber);
        question = findViewById(R.id.question);
        check = findViewById(R.id.check);
        next = findViewById(R.id.next);

        options = new TextView[]{optionA, optionB, optionC, optionD};

        // Set click listeners
        for (TextView option : options) {
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleOptionSelection((TextView) v);
                }
            });
        }

        // Set click listener for the Check button
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();

                // Update the "Next" button text on the last question
                if (currentQuestionIndex == questions.length - 1) {
                    next.setText("Finish");
                }
            }
        });

        // Set click listener for the Next button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex == questions.length - 1) {
                    handleFinish();
                } else {
                    loadNextQuestion();
                    next.setVisibility(View.GONE);
                }
            }
        });

        // Load the first question
        loadQuestion();

    }

    private void loadQuestion() {
        // Display current question and options
        question.setText(questions[currentQuestionIndex]);
        questionNumber.setText((currentQuestionIndex + 1) + "/" + questions.length);

        String[] currentOptions = optionsData[currentQuestionIndex];
        for (int i = 0; i < options.length; i++) {
            options[i].setText(currentOptions[i]);
            options[i].setBackgroundResource(R.drawable.option_default); // Reset background
            options[i].setClickable(true);  // Re-enable clickable behavior
            options[i].setEnabled(true);    // Re-enable interaction with the option
        }

        // Reset selected option and button states
        selectedOptionIndex = -1;
        check.setEnabled(true);
        next.setEnabled(false);

        isAnswerChecked = false; // Reset the flag when loading a new question
    }

    private void checkAnswer() {
        if (selectedOptionIndex == -1) {
            Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show();
            return;
        }

        int correctIndex = correctAnswers[currentQuestionIndex];
        if (selectedOptionIndex == correctIndex) {
            options[selectedOptionIndex].setBackgroundResource(R.drawable.option_correct);
            next.setVisibility(View.VISIBLE);
            correctAnswerss++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            next.setVisibility(View.VISIBLE);
            // Highlight selected option in red
            options[selectedOptionIndex].setBackgroundResource(R.drawable.incorrect);

            // Highlight the correct option in green
            options[correctIndex].setBackgroundResource(R.drawable.option_correct);
            Toast.makeText(this, "Wrong! Correct answer: " + optionsData[currentQuestionIndex][correctIndex], Toast.LENGTH_SHORT).show();
        }

        // Disable Check button and enable Next button
        check.setEnabled(false);
        next.setEnabled(true);

        // Disable options after checking the answer
        isAnswerChecked = true;
        disableOptions();
    }

    private void disableOptions() {
        for (TextView option : options) {
            option.setClickable(false); // Disable the clickable behavior
            option.setEnabled(false);   // Disable interaction with the option
        }
    }

    private void loadNextQuestion() {
        // Move to the next question
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            loadQuestion();
            next.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Quiz Complete!", Toast.LENGTH_SHORT).show();
            finish(); // End the quiz
        }
    }

    private void handleOptionSelection(TextView selectedOption) {
        // Reset all options to default
        for (int i = 0; i < options.length; i++) {
            options[i].setBackgroundResource(R.drawable.option_default); // Default background

            // Check if the clicked option matches the current one and update selectedOptionIndex
            if (options[i] == selectedOption) {
                selectedOptionIndex = i;
            }
        }

        // Highlight the selected option
        selectedOption.setBackgroundResource(R.drawable.option_selected); // Selected background
    }

    private void handleFinish() {
        int totalQuestions = questions.length;


        // Calculate the percentage
        double percentage = (double) correctAnswerss / totalQuestions * 100;

        // Pass data to QuizScore activity
        Intent intent = new Intent(EasyQuiz.this, QuizScore.class);
        intent.putExtra("percentage", percentage);
        intent.putExtra("totalQuestions", totalQuestions);
        intent.putExtra("correctAnswers", correctAnswerss);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Create an intent to navigate back to the previous activity
        super.onBackPressed();
        Intent intent = new Intent(this, DashboardLogin.class);
        startActivity(intent);
        finish(); // Optional: To remove the current activity from the stack
    }
}