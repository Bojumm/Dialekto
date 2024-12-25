package com.example.dialekto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
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
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;


public class CameraFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private ImageButton button;
    private Uri imageUri;
    private TextView  translation;
    private ImageView like, speech, copy;
    private ImageButton switchD;
    private TextToSpeech textToSpeech;

    private PreviewView previewView;
    private boolean isImageCaptured = false;


    private Spinner dialect1;
    private Spinner dialect2;

    private HashMap<String, String> tagalogToKapampangan;
    private HashMap<String, String> kapampanganToTagalog;

    private String imageText = "";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Camera");
        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        dialect1 = view.findViewById(R.id.spinnerDialect1);
        dialect2 = view.findViewById(R.id.spinnerDialect2);
        switchD = view.findViewById(R.id.switchDialect);

        // Initialize views
        imageView = view.findViewById(R.id.imageView);
        button = view.findViewById(R.id.button);
        translation = view.findViewById(R.id.tvTranslation);
        previewView = view.findViewById(R.id.previewView);
        startCamera();
        //
        like = view.findViewById(R.id.likeIcon);
        speech = view.findViewById(R.id.volumeIcon);
        copy = view.findViewById(R.id.copyIcon);

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

        switchD.setOnClickListener(view1 -> {
            imageText = "";
            imageView.setImageResource(R.drawable.baseline_insert_photo_24);
            // Get the current selected items from both Spinners
            String dialect1Value = dialect1.getSelectedItem().toString();
            String dialect2Value = dialect2.getSelectedItem().toString();

            // Swap selections in the spinners
            dialect1.setSelection(((ArrayAdapter<String>) dialect1.getAdapter()).getPosition(dialect2Value));
            dialect2.setSelection(((ArrayAdapter<String>) dialect2.getAdapter()).getPosition(dialect1Value));

            // Swap the input text and the translated text based on the current dialect selections
            String temp = imageText;  // Get the current text from the input field
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

        // Set the visibility based on whether an image has been captured
        if (isImageCaptured) {
            previewView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            previewView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

        // Set a listener to capture the image on button click
        button.setOnClickListener(v -> openCamera());

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Set the language to Filipino (Tagalog)
                    int langResult = textToSpeech.setLanguage(Locale.forLanguageTag("fil"));
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getContext(), "Language is not supported or missing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
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
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }


    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getContext(), "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Get the captured image as a Bitmap
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                // Convert the Bitmap to a URI and start UCrop for cropping
                imageUri = getImageUri(capturedImage);
                startCrop(imageUri);

                // Set flag to true to indicate that an image has been captured
                isImageCaptured = true;
                // Hide the camera preview and show the image view
                previewView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

            } else if (requestCode == UCrop.REQUEST_CROP) {
                // Handle the cropped image
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    // Set the cropped image to the ImageView
                    imageView.setImageURI(resultUri);
                    // Perform text extraction on the cropped image
                    extractTextFromImage(resultUri);
                    Toast.makeText(getContext(), "Image cropped successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), "Image capture canceled.", Toast.LENGTH_SHORT).show();
        }
    }

    // Convert Bitmap to Uri
    private Uri getImageUri(Bitmap bitmap) {
        // Save the bitmap to a temporary file and return the Uri
        // You can customize this part to save the image to a specific location
        File tempFile = new File(requireContext().getCacheDir(), "temp_image.jpg");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(tempFile);
    }

    private void startCrop(Uri uri) {
        // Define the destination file for the cropped image
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));

        // Set the cropping options
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);  // Set compression quality to 100 for no compression (highest quality)
        options.setMaxBitmapSize(1500); // Set the maximum bitmap size for cropping (optional)
        options.setCircleDimmedLayer(false);  // Optional: disables circular crop

        // Start UCrop for cropping
        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)  // Set aspect ratio (optional)
                .withOptions(options)  // Apply custom options
                .start(requireContext(), this);

    }

    private void extractTextFromImage(Uri imageUri) {
        try {
            InputImage inputImage = InputImage.fromFilePath(requireContext(), imageUri);
            // Pass TextRecognizerOptions to get the client
            TextRecognizer recognizer = TextRecognition.getClient(new TextRecognizerOptions.Builder().build());

            recognizer.process(inputImage)
                    .addOnSuccessListener(text -> {
                        // Handle the recognized text
                        String recognizedText = text.getText();
                        // the recognized text
                        imageText = getTranslation(recognizedText);
                        // Display the recognized text in the TextView
                        translation.setText(imageText);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "No Text Available, Please Try Again.", Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
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
