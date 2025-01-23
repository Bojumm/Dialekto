package com.example.dialekto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HardQuiz extends AppCompatActivity {

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
            "What is the Kapampangan translation of 'Hindi ko na kayang maghintay'?",
            "What is the Tagalog translation of 'Makanyan ya ning aring kasulatan'?",
            "What is the Kapampangan translation of 'Saan ka pupunta ngayon?'?",
            "What is the Tagalog translation of 'Atyu ku king kayang dako'?",
            "What is the Kapampangan translation of 'Ang mahal ng bilihin ngayon'?",
            "What is the Tagalog translation of 'Atyu ku king Lungsod ng Angeles'?",
            "What is the Kapampangan translation of 'Maganda ang panahon ngayon'?",
            "What is the Tagalog translation of 'Nanu ya ing sitwasyon?'?",
            "What is the Kapampangan translation of 'Anong ginagawa mo kanina?'?",
            "What is the Tagalog translation of 'Makakanta ya king aliwang pamamaran'?"
    };

    private String[][] optionsData = {
            {"E ku na dakal na pasensya", "E ku kayaning maghintay", "E ku yamu kayang maghintay", "E ku dapat kayang maghintay"},    // "Hindi ko na kayang maghintay" (1)
            {"Ganito na ang mga papeles", "Ito na ang kasulatan", "Ganyan ang sulat", "Ito na ang mga dokumento"},                   // "Makanyan ya ning aring kasulatan" (1)
            {"Atyu ka king kakaluguran?", "Dakal a pamaglakad?", "Atyu ka kung nukarin?", "Atyu ka, mangan keni?"},                   // "Saan ka pupunta ngayon?" (2)
            {"Dito ko a mag-umpisa", "Atyu ku king kayang dako", "Atyu ku king pagkatawan", "Mangatuknang ku king aking tanggapan"},   // "Atyu ku king kayang dako" (1)
            {"Masalese ya ning bilihin", "Atyu ya ning mahal", "E ku aram kung anu", "Mahal yang bilihin ngeni"},                    // "Ang mahal ng bilihin ngayon" (3)
            {"Atyu ku king Cebu", "Atyu ku king Angeles", "Nasa Metro Manila", "E ku ku ikwa king Baguio"},                           // "Atyu ku king Lungsod ng Angeles" (1)
            {"Atyu ku king tagpo", "Atyu ku keng taluktok", "Magalang a pamangan", "Mayap a panahon ngeni"},                         // "Maganda ang panahon ngayon" (3)
            {"Anong nangyayari", "Nasa estado ka ba?", "Paborito ko ito", "Patanungin mo ang oras"},                                   // "Nanu ya ing sitwasyon?" (0)
            {"Anu dakal kang itangke", "Anu ti panang labu", "Pangungusap lang", "Lunchen, pambisita lang nge"},                     // "Anong ginagawa mo kanina?" (0)
            {"Makakanta ya king aliwang pamamaran", "Atyu ku king makata", "Magkausap kaming taga Pampanga", "Bibigyan ka ng pera"}   // "Makakanta ya king aliwang pamamaran" (0)
    };

    private int[] correctAnswers = {1, 1, 2, 1, 3, 1, 3, 0, 0, 0}; // Correct answer indices

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_quiz);

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
        Intent intent = new Intent(HardQuiz.this, QuizScore.class);
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