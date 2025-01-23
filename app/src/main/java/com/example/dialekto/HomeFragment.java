package com.example.dialekto;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
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

        copy.setOnClickListener(v -> copyTextToClipboard());
        like.setOnClickListener(v -> AddToFavourite());

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

        tagalogToKapampangan.put("Magandang gabi", "Mayap a bengi"); tagalogToKapampangan.put("magandang gabi", "Mayap a bengi");
        tagalogToKapampangan.put("Magandang hapon", "Mayap a gatpanapun"); tagalogToKapampangan.put("magandang hapon", "Mayap a gatpanapun");
        tagalogToKapampangan.put("Kumain na", "Mangan naka"); tagalogToKapampangan.put("kumain na", "Mangan naka");
        tagalogToKapampangan.put("Walang anuman", "Alang anuman"); tagalogToKapampangan.put("walang anuman", "Alang anuman");
        tagalogToKapampangan.put("Bahay", "Bale"); tagalogToKapampangan.put("bahay", "Bale");
        tagalogToKapampangan.put("Pamilya", "Pamilyang"); tagalogToKapampangan.put("pamilya", "Pamilyang");
        tagalogToKapampangan.put("Kaibigan", "Kakaluguran"); tagalogToKapampangan.put("kaibigan", "Kakaluguran");
        tagalogToKapampangan.put("Opo", "Owa"); tagalogToKapampangan.put("opo", "Owa");
        tagalogToKapampangan.put("Hindi", "Ali"); tagalogToKapampangan.put("hindi", "Ali");

        tagalogToKapampangan.put("Maligayang bati", "Masayang bati"); tagalogToKapampangan.put("maligayang bati", "Masayang bati");
        tagalogToKapampangan.put("Magandang araw", "Mayap a aldo"); tagalogToKapampangan.put("magandang araw", "Mayap a aldo");
        tagalogToKapampangan.put("Anong pangalan mo", "Nanu ing lagyu mu"); tagalogToKapampangan.put("anong pangalan mo", "Nanu ing lagyu mu");
        tagalogToKapampangan.put("Oo", "Wa"); tagalogToKapampangan.put("oo", "Wa");
        tagalogToKapampangan.put("Pagkain", "Kakanan"); tagalogToKapampangan.put("pagkain", "Kakanan");
        tagalogToKapampangan.put("Inom", "Imnan"); tagalogToKapampangan.put("inom", "Imnan");
        tagalogToKapampangan.put("Bata", "Anak"); tagalogToKapampangan.put("bata", "Anak");
        tagalogToKapampangan.put("Matanda", "Malati"); tagalogToKapampangan.put("matanda", "Malati");
        tagalogToKapampangan.put("Guro", "Maestra"); tagalogToKapampangan.put("guro", "Maestra");
        tagalogToKapampangan.put("Eskwela", "Iskwela"); tagalogToKapampangan.put("eskwela", "Iskwela");
        tagalogToKapampangan.put("Simula", "Umpisa"); tagalogToKapampangan.put("simula", "Umpisa");
        tagalogToKapampangan.put("Kaunti", "Dakal"); tagalogToKapampangan.put("kaunti", "Dakal");
        tagalogToKapampangan.put("Kahapon", "Napon"); tagalogToKapampangan.put("kahapon", "Napon");
        tagalogToKapampangan.put("Ngayon", "Ngeni"); tagalogToKapampangan.put("ngayon", "Ngeni");
        tagalogToKapampangan.put("Bukas", "Bukis"); tagalogToKapampangan.put("bukas", "Bukis");
        tagalogToKapampangan.put("Malapit", "Malapit"); tagalogToKapampangan.put("malapit", "Malapit");

        tagalogToKapampangan.put("Pamilya ko", "Pamilyaku"); tagalogToKapampangan.put("pamilya ko", "Pamilyaku");
        tagalogToKapampangan.put("Tulong", "Saup"); tagalogToKapampangan.put("tulong", "Saup");
        tagalogToKapampangan.put("Trabaho", "Obra"); tagalogToKapampangan.put("trabaho", "Obra");
        tagalogToKapampangan.put("Magkano", "Magkanu"); tagalogToKapampangan.put("magkano", "Magkanu");
        tagalogToKapampangan.put("Araw", "Aldo"); tagalogToKapampangan.put("araw", "Aldo");
        tagalogToKapampangan.put("Gabi", "Bengi"); tagalogToKapampangan.put("gabi", "Bengi");
        tagalogToKapampangan.put("Mahal", "Malagu"); tagalogToKapampangan.put("mahal", "Malagu");
        tagalogToKapampangan.put("Ulan", "Auran"); tagalogToKapampangan.put("ulan", "Auran");
        tagalogToKapampangan.put("Init", "Panayan"); tagalogToKapampangan.put("init", "Panayan");
        tagalogToKapampangan.put("Lakad", "Lakad"); tagalogToKapampangan.put("lakad", "Lakad");
        tagalogToKapampangan.put("Bilog", "Bilug"); tagalogToKapampangan.put("bilog", "Bilug");
        tagalogToKapampangan.put("Pag-ibig", "Kaluguran"); tagalogToKapampangan.put("pag-ibig", "Kaluguran");
        tagalogToKapampangan.put("Panaginip", "Damdam"); tagalogToKapampangan.put("panaginip", "Damdam");
        tagalogToKapampangan.put("Kasama", "Kuyab"); tagalogToKapampangan.put("kasama", "Kuyab");
        tagalogToKapampangan.put("Walang tao", "Ala nang tau"); tagalogToKapampangan.put("walang tao", "Ala nang tau");
        tagalogToKapampangan.put("Pagod", "Pamalaylay"); tagalogToKapampangan.put("pagod", "Pamalaylay");
        tagalogToKapampangan.put("Masaya", "Masaya"); tagalogToKapampangan.put("masaya", "Masaya");
        tagalogToKapampangan.put("Malungkot", "Magmuya"); tagalogToKapampangan.put("malungkot", "Magmuya");
        tagalogToKapampangan.put("Mahalaga", "Maimportante"); tagalogToKapampangan.put("mahalaga", "Maimportante");

        // Vise versa
        kapampanganToTagalog.put("Kaluguran daka", "Mahal kita"); kapampanganToTagalog.put("kaluguran daka", "Mahal kita");
        kapampanganToTagalog.put("Musta ka", "Kamusta ka"); kapampanganToTagalog.put("musta ka", "Kamusta ka");
        kapampanganToTagalog.put("Dakal a salamat", "Salamat"); kapampanganToTagalog.put("dakal a salamat", "Salamat");
        kapampanganToTagalog.put("Magandang aldo", "Magandang umaga"); kapampanganToTagalog.put("magandang aldo", "Magandang umaga");
        kapampanganToTagalog.put("Pamagbayu", "Paalam"); kapampanganToTagalog.put("pamagbayu", "Paalam");
        kapampanganToTagalog.put("Ima mu", "Tang ina mo"); kapampanganToTagalog.put("ima mu", "Tang ina mo");

        kapampanganToTagalog.put("Mayap a bengi", "Magandang gabi"); kapampanganToTagalog.put("mayap a bengi", "Magandang gabi");
        kapampanganToTagalog.put("Mayap a gatpanapun", "Magandang hapon"); kapampanganToTagalog.put("mayap a gatpanapun", "Magandang hapon");
        kapampanganToTagalog.put("Mangan naka", "Kumain na"); kapampanganToTagalog.put("mangan naka", "Kumain na");
        kapampanganToTagalog.put("Alang anuman", "Walang anuman"); kapampanganToTagalog.put("alang anuman", "Walang anuman");
        kapampanganToTagalog.put("Bale", "Bahay"); kapampanganToTagalog.put("bale", "Bahay");
        kapampanganToTagalog.put("Pamilyang", "Pamilya"); kapampanganToTagalog.put("pamilyang", "Pamilya");
        kapampanganToTagalog.put("Kakaluguran", "Kaibigan"); kapampanganToTagalog.put("kakaluguran", "Kaibigan");
        kapampanganToTagalog.put("Owa", "Opo"); kapampanganToTagalog.put("owa", "Opo");
        kapampanganToTagalog.put("Ali", "Hindi"); kapampanganToTagalog.put("ali", "Hindi");

        kapampanganToTagalog.put("Masayang bati", "Maligayang bati"); kapampanganToTagalog.put("masayang bati", "Maligayang bati");
        kapampanganToTagalog.put("Mayap a aldo", "Magandang araw"); kapampanganToTagalog.put("mayap a aldo", "Magandang araw");
        kapampanganToTagalog.put("Nanu ing lagyu mu", "Anong pangalan mo"); kapampanganToTagalog.put("nanu ing lagyu mu", "Anong pangalan mo");
        kapampanganToTagalog.put("Wa", "Oo"); kapampanganToTagalog.put("wa", "Oo");
        kapampanganToTagalog.put("Kakanan", "Pagkain"); kapampanganToTagalog.put("kakanan", "Pagkain");
        kapampanganToTagalog.put("Imnan", "Inom"); kapampanganToTagalog.put("imnan", "Inom");
        kapampanganToTagalog.put("Anak", "Bata"); kapampanganToTagalog.put("anak", "Bata");
        kapampanganToTagalog.put("Malati", "Matanda"); kapampanganToTagalog.put("malati", "Matanda");
        kapampanganToTagalog.put("Maestra", "Guro"); kapampanganToTagalog.put("maestra", "Guro");
        kapampanganToTagalog.put("Iskwela", "Eskwela"); kapampanganToTagalog.put("iskwela", "Eskwela");
        kapampanganToTagalog.put("Umpisa", "Simula"); kapampanganToTagalog.put("umpisa", "Simula");
        kapampanganToTagalog.put("Dakal", "Kaunti"); kapampanganToTagalog.put("dakal", "Kaunti");
        kapampanganToTagalog.put("Napon", "Kahapon"); kapampanganToTagalog.put("napon", "Kahapon");
        kapampanganToTagalog.put("Ngeni", "Ngayon"); kapampanganToTagalog.put("ngeni", "Ngayon");
        kapampanganToTagalog.put("Bukis", "Bukas"); kapampanganToTagalog.put("bukis", "Bukas");
        kapampanganToTagalog.put("Malapit", "Malapit"); kapampanganToTagalog.put("malapit", "Malapit");

        kapampanganToTagalog.put("Pamilyaku", "Pamilya ko"); kapampanganToTagalog.put("pamilyaku", "Pamilya ko");
        kapampanganToTagalog.put("Saup", "Tulong"); kapampanganToTagalog.put("saup", "Tulong");
        kapampanganToTagalog.put("Obra", "Trabaho"); kapampanganToTagalog.put("obra", "Trabaho");
        kapampanganToTagalog.put("Magkanu", "Magkano"); kapampanganToTagalog.put("magkanu", "Magkano");
        kapampanganToTagalog.put("Aldo", "Araw"); kapampanganToTagalog.put("aldo", "Araw");
        kapampanganToTagalog.put("Bengi", "Gabi"); kapampanganToTagalog.put("bengi", "Gabi");
        kapampanganToTagalog.put("Malagu", "Mahal"); kapampanganToTagalog.put("malagu", "Mahal");
        kapampanganToTagalog.put("Auran", "Ulan"); kapampanganToTagalog.put("auran", "Ulan");
        kapampanganToTagalog.put("Panayan", "Init"); kapampanganToTagalog.put("panayan", "Init");
        kapampanganToTagalog.put("Lakad", "Lakad"); kapampanganToTagalog.put("lakad", "Lakad");
        kapampanganToTagalog.put("Bilug", "Bilog"); kapampanganToTagalog.put("bilug", "Bilog");
        kapampanganToTagalog.put("Kaluguran", "Pag-ibig"); kapampanganToTagalog.put("kaluguran", "Pag-ibig");
        kapampanganToTagalog.put("Damdam", "Panaginip"); kapampanganToTagalog.put("damdam", "Panaginip");
        kapampanganToTagalog.put("Kuyab", "Kasama"); kapampanganToTagalog.put("kuyab", "Kasama");
        kapampanganToTagalog.put("Ala nang tau", "Walang tao"); kapampanganToTagalog.put("ala nang tau", "Walang tao");
        kapampanganToTagalog.put("Pamalaylay", "Pagod"); kapampanganToTagalog.put("pamalaylay", "Pagod");
        kapampanganToTagalog.put("Masaya", "Masaya"); kapampanganToTagalog.put("masaya", "Masaya");
        kapampanganToTagalog.put("Magmuya", "Malungkot"); kapampanganToTagalog.put("magmuya", "Malungkot");
        kapampanganToTagalog.put("Maimportante", "Mahalaga"); kapampanganToTagalog.put("maimportante", "Mahalaga");

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

    private void AddToFavourite(){
        String inputText = input.getText().toString().trim();
        String translationText = translation.getText().toString().trim();

        // Get selected languages
        String inputLanguage = dialect1.getSelectedItem().toString();
        String translationLanguage = dialect2.getSelectedItem().toString();

        if (!TextUtils.isEmpty(inputText) && !TextUtils.isEmpty(translationText) &&
                !translationText.equals("Translation") && !translationText.equals("Translation not available.")) {

            // Add to favorites with language info
            CardItem favoriteItem = new CardItem(inputText, translationText, inputLanguage, translationLanguage);

            // Pass the context to getInstance()
            FavoritesManager.getInstance(requireContext()).addFavorite(favoriteItem);

            // Notify the user
            Toast.makeText(getContext(), "Added to favorites!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No valid input or translation to add to favorites.", Toast.LENGTH_SHORT).show();
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
