package com.example.dialekto;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;


public class CameraFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private ImageButton button;
    private Uri imageUri;
    private TextView textView;
    private ImageView like, speech, copy;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Camera");
        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        // Initialize views
        imageView = view.findViewById(R.id.imageView);
        button = view.findViewById(R.id.button);
        textView = view.findViewById(R.id.tvTranslation);
        //
        like = view.findViewById(R.id.likeIcon);
        speech = view.findViewById(R.id.volumeIcon);
        copy = view.findViewById(R.id.copyIcon);

        copy.setOnClickListener(v -> copyTextToClipboard());


        /* Open the camera automatically when the fragment starts
        openCamera();*/

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


        // Set up the OnClickListener for the volume icon
        speech.setOnClickListener(v -> {
            String textToRead = textView.getText().toString();
            if (!textToRead.isEmpty()) {
                textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(getContext(), "No text to speak", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
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
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(getContext(), "Error cropping image: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
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
        // Start UCrop for cropping
        UCrop.of(uri, Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg")))
                .withAspectRatio(1, 1) // Set aspect ratio (optional)
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
                        // Display the recognized text in the TextView
                        textView.setText(recognizedText);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Text recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyTextToClipboard() {
        String textToCopy = textView.getText().toString();

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
