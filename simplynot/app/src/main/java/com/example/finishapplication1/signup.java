package com.example.finishapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private EditText mSignUpEmail,mSignUpPassword;
    private RelativeLayout mSignUp;
    private TextView mGotoLogin;


     private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
        mSignUpEmail = findViewById(R.id.signUpEmail);
        mSignUpPassword = findViewById(R.id.signUpEmail);
        mSignUp = findViewById(R.id.signup);
        mGotoLogin = findViewById(R.id.goToLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        mGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this,MainActivity.class);
                startActivity(intent);
            }
        });
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=mSignUpEmail.getText().toString().trim();
                String password=mSignUpPassword.getText().toString().trim();

                if(mail.isEmpty() && password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All Fields are Required",Toast.LENGTH_SHORT).show();

                }else if(password.length()<7)
                {
                    Toast.makeText(getApplicationContext(),"Password Should Greater than 7 digits",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Registration Successful", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Failed To Register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Verification Email is Sent, Verify and Log In Again ", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();;
                    finish();
                    startActivity(new Intent(signup.this,MainActivity.class));
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Failed verification your Email ", Toast.LENGTH_SHORT).show();
        }
    }
}