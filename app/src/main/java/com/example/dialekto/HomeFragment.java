package com.example.dialekto;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private Spinner dialect1;
    private Spinner dialect2;
    private ImageButton switchDialect;
    private EditText input;
    private TextView translation;
    // Create HashMaps for translation
    private HashMap<String, String> tagalogToKapampangan;
    private HashMap<String, String> kapampanganToTagalog;

    //For Icon
    private ImageView like, copy, speech;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");

        View view = inflater.inflate(R.layout.home_fragment, null);
        dialect1 = view.findViewById(R.id.spinnerDialect1);
        dialect2 = view.findViewById(R.id.spinnerDialect2);
        switchDialect = view.findViewById(R.id.switchDialect);
        input = view.findViewById(R.id.etInput);
        translation = view.findViewById(R.id.tvTranslation);

        like = view.findViewById(R.id.likeIcon);
        copy = view.findViewById(R.id.copyIcon);
        speech = view.findViewById(R.id.volumeIcon);

        // Initialize translation maps
        tagalogToKapampangan = new HashMap<>();
        kapampanganToTagalog = new HashMap<>();

        // Add translations to the maps
        tagalogToKapampangan.put("Mahal kita", "Kaluguran daka");
        tagalogToKapampangan.put("Kamusta ka?", "Musta ka?");
        tagalogToKapampangan.put("Salamat", "Dakal a salamat");
        tagalogToKapampangan.put("Magandang umaga", "Magandang aldo");
        tagalogToKapampangan.put("Paalam", "Pamagbayu");
        tagalogToKapampangan.put("Tang ina mo", "Ima mu");

        kapampanganToTagalog.put("Kaluguran daka", "Mahal kita");
        kapampanganToTagalog.put("Musta ka?", "Kamusta ka?");
        kapampanganToTagalog.put("Dakal a salamat", "Salamat");
        kapampanganToTagalog.put("Magandang aldo", "Magandang umaga");
        kapampanganToTagalog.put("Pamagbayu", "Paalam");
        kapampanganToTagalog.put("Ima mu", "Tang ina mo");

        List<String> dialects = new ArrayList<>();
        dialects.add("Tagalog");
        dialects.add("Kapampangan");

        // Create separate adapters for dialect1 and dialect2
        ArrayAdapter<String> dialect1Adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, dialects);
        dialect1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> dialect2Adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, dialects);
        dialect2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Apply the adapters to the Spinners
        dialect1.setAdapter(dialect1Adapter);
        dialect2.setAdapter(dialect2Adapter);

        // Manually set the selection for each Spinner
        dialect1.setSelection(0);  // This will set "Tagalog" in dialect1
        dialect2.setSelection(1);  // This will set "Kapampangan" in dialect2

        // Set up the TextWatcher to update translation as the user types
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get the text input by the user
                String inputText = charSequence.toString().trim();

                // Get the current selected items from both Spinners
                String dialect1Value = dialect1.getSelectedItem().toString();
                String dialect2Value = dialect2.getSelectedItem().toString();

                // Variables to hold the translation
                String translatedText = "";

                // Check the current selection and provide the correct translation
                if (dialect1Value.equals("Tagalog") && dialect2Value.equals("Kapampangan")) {
                    // Translate from Tagalog to Kapampangan
                    translatedText = tagalogToKapampangan.get(inputText);
                } else if (dialect1Value.equals("Kapampangan") && dialect2Value.equals("Tagalog")) {
                    // Translate from Kapampangan to Tagalog
                    translatedText = kapampanganToTagalog.get(inputText);
                }

                // If a translation exists, display it
                if (translatedText != null) {
                    translation.setText(translatedText);  // Display the translation in the TextView
                } else {
                    translation.setText("Translation not available.");  // If no translation found
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set the click listener for the switchDialect button
        switchDialect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current selected items from both Spinners
                String dialect1Value = dialect1.getSelectedItem().toString();
                String dialect2Value = dialect2.getSelectedItem().toString();

                // Swap selections in the spinners
                dialect1.setSelection(((ArrayAdapter<String>) dialect1.getAdapter()).getPosition(dialect2Value));
                dialect2.setSelection(((ArrayAdapter<String>) dialect2.getAdapter()).getPosition(dialect1Value));

                // Swap the input text and the translated text based on the current dialect selections
                String temp = input.getText().toString().trim();  // Get the current text from the input field
                String tempTranslation = translation.getText().toString().trim();

                // Check if the translation is "Translation Not Available"
                if (tempTranslation.equals("Translation not available.")) {
                    // If translation is not available, clear both fields
                    input.setText("");
                    translation.setText("Translation");
                } else {
                        // Swap the values
                        input.setText(tempTranslation);  // Set the input field to show the translation
                        translation.setText(temp);}
            }
        });

        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set the language to Tagalog
                Locale tagalogLocale = new Locale("tl", "PH");
                int result = textToSpeech.setLanguage(tagalogLocale);
            } else {
                Toast.makeText(getContext(), "Text-to-Speech initialization failed.", Toast.LENGTH_LONG).show();
            }
        });

        speech.setOnClickListener(v -> {
            String textToSpeak = translation.getText().toString();
            if (!textToSpeak.equals("Translation") && !textToSpeak.equals("Translation not available.")) {
                if (textToSpeech != null) {
                    textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });


        return view;

    }
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
