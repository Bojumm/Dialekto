package com.example.dialekto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VoiceFragment extends Fragment {

    private ImageView like, copy, speech;
    private TextView translation;
    private Spinner dialect1, dialect2;
    private ImageButton voiceDialect, playButton, switchD;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private HashMap<String, String> tagalogToKapampangan;
    private HashMap<String, String> kapampanganToTagalog;
    private static final int REQUEST_AUDIO_PERMISSION = 1;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 100;
    private String recognizedText = "";  // Store recognized text to be spoken

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_fragment, null);

        voiceDialect = view.findViewById(R.id.voiceDialect);
        playButton = view.findViewById(R.id.playButton);
        switchD = view.findViewById(R.id.switchDialect);
        dialect1 = view.findViewById(R.id.spinnerDialect1);
        dialect2 = view.findViewById(R.id.spinnerDialect2);
        translation = view.findViewById(R.id.translationText);

        like = view.findViewById(R.id.likeIcon);
        copy = view.findViewById(R.id.CopyIcon);
        speech = view.findViewById(R.id.volumeIcon);

        // Initialize translation maps
        tagalogToKapampangan = new HashMap<>();
        kapampanganToTagalog = new HashMap<>();

        // Add translations to the maps
        tagalogToKapampangan.put("Mahal kita", "Kaluguran daka"); tagalogToKapampangan.put("mahal kita", "Kaluguran daka");
        tagalogToKapampangan.put("Kamusta ka", "Musta ka"); tagalogToKapampangan.put("kamusta ka", "Musta ka");
        tagalogToKapampangan.put("Salamat", "Dakal a salamat"); tagalogToKapampangan.put("salamat", "Dakal a salamat");
        tagalogToKapampangan.put("Magandang umaga", "Magandang aldo"); tagalogToKapampangan.put("magandang umaga", "Magandang aldo");
        tagalogToKapampangan.put("Paalam", "Pamagbayu"); tagalogToKapampangan.put("paalam", "Pamagbayu");
        tagalogToKapampangan.put("Tang ina mo", "Ima mu"); tagalogToKapampangan.put("tang ina mo", "Ima mu");

        kapampanganToTagalog.put("Kaluguran daka", "Mahal kita"); kapampanganToTagalog.put("kaluguran daka", "Mahal kita");
        kapampanganToTagalog.put("Musta ka", "Kamusta ka"); kapampanganToTagalog.put("musta ka", "Kamusta ka");
        kapampanganToTagalog.put("Dakal a salamat", "Salamat"); kapampanganToTagalog.put("dakal a salamat", "Salamat");
        kapampanganToTagalog.put("Magandang aldo", "Magandang umaga"); kapampanganToTagalog.put("magandang aldo", "Magandang umaga");
        kapampanganToTagalog.put("Pamagbayu", "Paalam"); kapampanganToTagalog.put("pamagbayu", "Paalam");
        kapampanganToTagalog.put("Ima mu", "Tang ina mo"); kapampanganToTagalog.put("ima mu", "Tang ina mo");

        List<String> dialects = new ArrayList<>();
        dialects.add("Tagalog");
        dialects.add("Kapampangan");

        // Create separate adapters for dialect1 and dialect2
        ArrayAdapter<String> dialect1Adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dialects);
        dialect1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> dialect2Adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dialects);
        dialect2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the Spinners
        dialect1.setAdapter(dialect1Adapter);
        dialect2.setAdapter(dialect2Adapter);

        // Manually set the selection for each Spinner
        dialect1.setSelection(0);  // This will set "Tagalog" in dialect1
        dialect2.setSelection(1);  // This will set "Kapampangan" in dialect2

        // Check and request permissions
        checkAudioPermission();

        switchD.setOnClickListener(view1 -> {
            recognizedText = "";
            // Get the current selected items from both Spinners
            String dialect1Value = dialect1.getSelectedItem().toString();
            String dialect2Value = dialect2.getSelectedItem().toString();

            // Swap selections in the spinners
            dialect1.setSelection(((ArrayAdapter<String>) dialect1.getAdapter()).getPosition(dialect2Value));
            dialect2.setSelection(((ArrayAdapter<String>) dialect2.getAdapter()).getPosition(dialect1Value));

            // Swap the input text and the translated text based on the current dialect selections
            String temp = recognizedText;  // Get the current text from the input field
            String tempTranslation = translation.getText().toString().trim();

            // Check if the translation is "Translation Not Available"
            if (tempTranslation.equals("Translation not available.")) {
                translation.setText("");
                translation.setHint("Translation..");
            } else {
                translation.setText("");
                translation.setHint("Translation..");
            }

        });

        copy.setOnClickListener(v -> copyTextToClipboard());

        voiceDialect.setOnClickListener(v -> {
            if (speechRecognizer != null) {
                startVoiceRecognition();
            } else {
                Toast.makeText(getContext(), "Speech recognizer is not initialized.", Toast.LENGTH_SHORT).show();
            }
        });

        playButton.setOnClickListener(v -> {
            if (!recognizedText.isEmpty()) {
                playRecognizedText();
            } else {
                Toast.makeText(getContext(), "Use voice speech to have text to play.", Toast.LENGTH_SHORT).show();
            }
        });

        speech.setOnClickListener(v -> {
            String textToSpeak = translation.getText().toString();
            if (textToSpeak.equals("") || textToSpeak.equals("Translation..") || textToSpeak.equals("Translation not available.")) {
                Toast.makeText(getContext(), "No translation Available.", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("No Translation Available", TextToSpeech.QUEUE_FLUSH, null, null);
            }
            else{
                if (textToSpeech != null) {
                    textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        return view;
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
        } else {
            initializeSpeechRecognizer(); // Initialize once permission is granted
            initializeTextToSpeech(); // Initialize TextToSpeech
        }
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(getContext())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        } else {
            Toast.makeText(getContext(), "Speech recognition is not available on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.getDefault());
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getContext(), "Language not supported.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Text to Speech initialization failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startVoiceRecognition() {
        // Create intent for speech recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());  // Default device language
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Voice recognition not supported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                recognizedText = results.get(0);
                Log.d("SpeechRecognition", "Recognized text: " + recognizedText);

                // After recognizing the text, perform the translation based on the selected dialects
                String translatedText = getTranslation(recognizedText);
                if (translatedText != null) {
                    translation.setText(translatedText);  // Display the translation in the TextView
                } else {
                    translation.setText("Translation not available.");
                }
            }
        }
    }

    private String getTranslation(String temp) {
        // Get the selected dialects
        String dialect1Value = dialect1.getSelectedItem().toString();
        String dialect2Value = dialect2.getSelectedItem().toString();

        // Check the dialects and get the translation
        if (dialect1Value.equals("Tagalog") && dialect2Value.equals("Kapampangan")) {
            return tagalogToKapampangan.get(temp);  // Translate from Tagalog to Kapampangan
        } else if (dialect1Value.equals("Kapampangan") && dialect2Value.equals("Tagalog")) {
            return kapampanganToTagalog.get(temp);  // Translate from Kapampangan to Tagalog
        }
        return null;
    }

    private void playRecognizedText() {
        if (textToSpeech != null && !recognizedText.isEmpty()) {
            textToSpeech.speak(recognizedText, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void copyTextToClipboard() {
        String textToCopy = translation.getText().toString();

        // Check if the TextView has any text
        if (!TextUtils.isEmpty(textToCopy)) {
            // Get the ClipboardManager system service
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a ClipData with the text to copy
            android.content.ClipData clip = android.content.ClipData.newPlainText("Recognized Text", textToCopy);

            // Set the clip data to the clipboard
            clipboard.setPrimaryClip(clip);

            // Show a Toast to notify the user
            Toast.makeText(getContext(), "Text copied to clipboard!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No text to copy", Toast.LENGTH_SHORT).show();
        }
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
