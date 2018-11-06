package com.example.bitmani.carbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpUser extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user);

        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.signup_button);
    }

    public void signUpUserInformation(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        mAuth = FirebaseAuth.getInstance();

        if (!emailText.isEmpty() && !passwordText.isEmpty()) {
            if (passwordText.length() < 6) {
                Toast.makeText(SignUpUser.this, "Password should contain atleast 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(SignUpUser.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpUser.this, "Signup Successfull.", Toast.LENGTH_SHORT).show();
                                    GlobalDatas.currentUser = mAuth.getCurrentUser();
                                    Intent intent = new Intent(getApplicationContext(), SignInUser.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpUser.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    email.setText("");
                                    password.setText("");
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(SignUpUser.this, "Fields can't be left empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
