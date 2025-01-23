package com.example.dialekto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardLogin extends AppCompatActivity {
    private Button LogoutButton;
    FirebaseAuth firebaseAut;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private TextView usernameTextView, emailTextView;
    private Button easy, medium, hard;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_login);

        firebaseAut = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAut.getCurrentUser();

        menuButton = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        easy = findViewById(R.id.easy);
        medium = findViewById(R.id.medium);
        hard = findViewById(R.id.hard);

        // Set up the navigation header views
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // Get the header view
        usernameTextView = headerView.findViewById(R.id.name); // Username TextView
        emailTextView = headerView.findViewById(R.id.email); // Email TextView

        // quiz button navigation

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardLogin.this, EasyQuiz.class));
                finish();
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardLogin.this, MediumQuiz.class));
                finish();
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardLogin.this, HardQuiz.class));
                finish();
            }
        });


        // Set up the menu button to open the drawer
        menuButton.setOnClickListener(view -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(Gravity.LEFT); // Open drawer when menu button is clicked
            }
        });

        // Set up navigation item listener for NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_logout:
                    Logout();
                    break;
            }
            drawerLayout.closeDrawer(Gravity.LEFT); // Close the drawer after menu item click
            return true;
        });

        // Retrieve user information and update the navigation header
        updateNavHeader();
    }

    private void updateNavHeader() {
        // Get current user from Firebase
        FirebaseUser currentUser = firebaseAut.getCurrentUser();

        if (currentUser != null) {
            // Get the user's display name and email
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            // Set the values in the TextViews
            if (name != null) {
                usernameTextView.setText(name);
            } else {
                usernameTextView.setText("Your Name Here");
            }

            if (email != null) {
                emailTextView.setText(email);
            } else {
                emailTextView.setText("youremail@example.com");
            }
        }
    }

    private void Logout(){
        firebaseAut.signOut();
        // Sign out from Google Sign-In
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(DashboardLogin.this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Log out completed successfully, navigate to Login screen
                Intent intent = new Intent(DashboardLogin.this, Login.class);
                Toast.makeText(DashboardLogin.this, "Logout!", Toast.LENGTH_SHORT).show();
                // Clear the activity stack
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onBackPressed() {
        // Check if the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If logged in, go to the home screen (launcher activity) of the phone
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        } else {
            // If not logged in, proceed with default back button behavior
            super.onBackPressed();
        }
    }
}