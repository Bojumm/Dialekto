package com.example.dialekto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private TextView login;
    private Button signnup;

    EditText s_email, s_pass, s_name, s_Cpass;

    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.textView_login);
        signnup = findViewById(R.id.signUpButton);
        s_name = findViewById(R.id.editText_username);
        s_email = findViewById(R.id.editText_email);
        s_pass = findViewById(R.id.editText_password);
        s_Cpass = findViewById(R.id.editText_confirmPassword);
        progressBar = findViewById(R.id.loading);

        login.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        });

        signnup.setOnClickListener(view -> SignUpAccount());

    }
    private void SignUpAccount(){
        String email, password, name, confirmpass;
        name = s_name.getText().toString();
        email = s_email.getText().toString();
        password = s_pass.getText().toString();
        confirmpass = s_Cpass.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(SignUp.this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignUp.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(SignUp.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmpass)) {
            Toast.makeText(SignUp.this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            sendEmailVerification();
                            clearInputFields();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
           }

    private boolean isValidEmail (CharSequence target){
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void sendEmailVerification () {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                // After email is sent, update user info or proceed as needed
                                updateUserInfo();
                            } else {
                                Toast.makeText(SignUp.this, "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    private void updateUserInfo () {
        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can get now
        String uid = FirebaseAuth.getInstance().getUid();

        String username = s_email.getText().toString();
        String name = s_name.getText().toString();

        //setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", username);
        hashMap.put("name", name);
        hashMap.put("profileImg", "");
        hashMap.put("userType", "user");/// possible values are user or admin in RDB
        hashMap.put("timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignUp.this, "Account created.", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                        startActivity(new Intent(SignUp.this, Login.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void clearInputFields() {
        s_name.setText("");
        s_email.setText("");
        s_pass.setText("");
        s_Cpass.setText("");
    }
}