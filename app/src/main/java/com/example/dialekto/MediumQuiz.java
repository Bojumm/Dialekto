package com.example.dialekto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MediumQuiz extends AppCompatActivity {

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
            "What is the Kapampangan translation of 'Magandang gabi'?",
            "What is the Tagalog translation of 'E ku balu'?",
            "What is the Kapampangan translation of 'Nasaan ka?'?",
            "What is the Tagalog translation of 'Masanting'?",
            "What is the Kapampangan translation of 'Maligayang kaarawan'?",
            "What is the Tagalog translation of 'Panamdaman'?",
            "What is the Kapampangan translation of 'Pumunta ka dito'?",
            "What is the Tagalog translation of 'Nanu ya ing oras?'?",
            "What is the Kapampangan translation of 'Kumain na tayo'?",
            "What is the Tagalog translation of 'Pekasanting'?"
    };

    private String[][] optionsData = {
            {"Mayap a aldo", "Mayap a bengi", "Magandang aldo", "Magandang bengi"},    // "Magandang gabi" (1)
            {"Hindi ko alam", "Ayoko", "Pagod na ako", "Bahala ka na"},                // "E ku balu" (0)
            {"Ninu ka?", "Atyu ka nukarin?", "Nanu ya?", "Makatuknang ka ba?"},       // "Nasaan ka?" (1)
            {"Masarap", "Maganda", "Mabait", "Matibay"},                              // "Masanting" (1)
            {"Masayang aldo", "Maligayang kaaldawan", "Masayang aldo keng kaarawan", "Malugud ka kaaldawan"}, // "Maligayang kaarawan" (2)
            {"Pangarap", "Pakiramdam", "Pag-aalala", "Pag-iisip"},                    // "Panamdaman" (1)
            {"Munta ka keni", "Dakal kang munta", "Sana dumating ka", "Ingkantu ka keni"}, // "Pumunta ka dito" (0)
            {"Ano ang ginagawa mo?", "Anong oras na?", "Nasa bahay ka ba?", "Pwede ka ba?"}, // "Nanu ya ing oras?" (1)
            {"Tara mangan", "Mangan na ta", "Pakan na ta", "Tara munta ta"},          // "Kumain na tayo" (1)
            {"Maganda", "Matamis", "Masarap", "Pinakamaganda"},                       // "Pekasanting" (3)
    };

    private int[] correctAnswers = {
            1, // "Mayap a bengi" is the translation of "Magandang gabi"
            0, // "Hindi ko alam" is the translation of "E ku balu"
            1, // "Atyu ka nukarin?" is the translation of "Nasaan ka?"
            1, // "Maganda" is the translation of "Masanting"
            2, // "Masayang aldo keng kaarawan" is the translation of "Maligayang kaarawan"
            1, // "Pakiramdam" is the translation of "Panamdaman"
            0, // "Munta ka keni" is the translation of "Pumunta ka dito"
            1, // "Anong oras na?" is the translation of "Nanu ya ing oras?"
            1, // "Mangan na ta" is the translation of "Kumain na tayo"
            3  // "Pinakamaganda" is the translation of "Pekasanting"
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medium_quiz);

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
        Intent intent = new Intent(MediumQuiz.this, QuizScore.class);
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