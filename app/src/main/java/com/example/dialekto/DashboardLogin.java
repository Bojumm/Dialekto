package com.example.dialekto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardLogin extends AppCompatActivity {
    private Button LogoutButton;
    FirebaseAuth firebaseAut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_login);

        firebaseAut = FirebaseAuth.getInstance();

        LogoutButton = findViewById(R.id.LogoutButton);

        LogoutButton.setOnClickListener(view -> Logout());
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
                // Clear the activity stack
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}