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
import com.google.firebase.auth.FirebaseUser;

public class SignInUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_user);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        signInButton = findViewById(R.id.sign_in_button);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            GlobalDatas.currentUser = currentUser;
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void logInUserInformation(View view) {
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        if (!emailText.isEmpty() && !passwordText.isEmpty()) {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(SignInUser.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInUser.this, "Login Successfull.", Toast.LENGTH_SHORT).show();
                                GlobalDatas.currentUser = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignInUser.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                email.setText("");
                                password.setText("");
                            }
                        }
                    });
        } else {
            Toast.makeText(SignInUser.this, "Fields can't be left empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
