package com.example.dialekto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private PreviewView previewView;
    private ImageButton button;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;

    //For Icon
    private ImageView like, copy, speech;
    private TextToSpeech textToSpeech;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Camera");
        View view = inflater.inflate(R.layout.camera_fragment, null);

        // Initialize views
        previewView = view.findViewById(R.id.previewView);
        button = view.findViewById(R.id.button);

        like = view.findViewById(R.id.likeIcon);
        copy = view.findViewById(R.id.copyIcon);
        speech = view.findViewById(R.id.volumeIcon);

        // Check for camera permission
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();  // Start the camera if permission is granted
        }

        button.setOnClickListener(v -> captureImage());

        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set the language to Tagalog
                Locale tagalogLocale = new Locale("tl", "PH");
                int result = textToSpeech.setLanguage(tagalogLocale);
            } else {
                Toast.makeText(getContext(), "Text-to-Speech initialization failed.", Toast.LENGTH_LONG).show();
            }
        });

        /* Pag natpos na yung OCR eto ung sa TTS
        speech.setOnClickListener(v -> {
            String textToSpeak = translation.getText().toString();
            if (!textToSpeak.equals("Translation") && !textToSpeak.equals("Translation not available.")) {
                if (textToSpeech != null) {
                    textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });*/

        return view;
    }

    private void startCamera() {
        // Initialize the CameraX provider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                // Get the camera provider
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(getContext(), "Camera initialization failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindCameraUseCases() {
        // Create a camera selector to choose the back camera
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        // Create a preview use case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Create an image capture use case
        imageCapture = new ImageCapture.Builder().build();

        // Bind use cases to the camera
        cameraProvider.unbindAll();
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void captureImage() {
        // Create a file to save the captured image
        File photoFile = new File(requireContext().getExternalFilesDir(null), "photo.jpg");

        // Create a photo capture request
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Take the picture
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Handle the saved image (e.g., show a toast or display it in an ImageView)
                        Toast.makeText(getContext(), "Image saved: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Handle error
                        Toast.makeText(getContext(), "Image capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
