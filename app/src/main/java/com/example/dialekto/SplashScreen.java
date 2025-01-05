package com.example.dialekto;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth firebaseAut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAut = FirebaseAuth.getInstance();
        // Check user after a delay
        new Handler().postDelayed(this::checkUser, 3000);

    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAut.getCurrentUser();

            if (firebaseUser != null) {
                // User is logged in, redirect to UserDashboard
                startActivity(new Intent(SplashScreen.this, DashboardLogin.class));
            } else {
                // No user logged in, redirect to Login
                startActivity(new Intent(SplashScreen.this, Login.class));
            }
            finish();
            return; // Exit to prevent further checks
        }
}