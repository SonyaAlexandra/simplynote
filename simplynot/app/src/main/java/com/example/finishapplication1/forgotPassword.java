package com.example.finishapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {

    private EditText mForgotPassword;
    private Button mPasswordRecoveredBtn;
    private TextView mGoBackToLogin;


    FirebaseAuth firebaseAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().hide();

        mForgotPassword = findViewById(R.id.forgotPassword);
        mPasswordRecoveredBtn = findViewById(R.id.passwordRecoverBtn);
        mGoBackToLogin = findViewById(R.id.goBackToLogin);
        firebaseAuth = firebaseAuth.getInstance();

        mGoBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgotPassword.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mPasswordRecoveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=mForgotPassword.getText().toString().trim();
                if(mail.isEmpty())
                {
                    Toast.makeText(getApplication(),"Enter your mail first",Toast.LENGTH_SHORT).show();
                }else
                {
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Mail Sent, You can recover your password using mail",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgotPassword.this, MainActivity.class));
                            }else
                            {
                                Toast.makeText(getApplicationContext(), "Email or Password is wrong or Account Not Exist !",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });
    }
}