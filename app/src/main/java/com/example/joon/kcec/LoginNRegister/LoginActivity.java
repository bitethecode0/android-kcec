package com.example.joon.kcec.LoginNRegister;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.kcec.Home.HomeActivity;
import com.example.joon.kcec.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private EditText mEmail, mPassword;
    private Button mLogin_btn;
    private ProgressBar mProgressBar;
    private TextView mCreateOne_tv;

    //vars
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mCreateOne_tv = findViewById(R.id.createId_tv);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        init();
        setupFirebase();
    }

    private void init() {
        mLogin_btn = findViewById(R.id.login_btn);
        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempt to login.");
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Toast.makeText(mContext, "You need to fill out every field to login.", Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(!task.isSuccessful()){
                                Log.d(TAG, "onComplete: sign in failed");
                                Toast.makeText(mContext, "Failed to sign in.", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            } else{
                                Log.d(TAG, "onComplete: sign in sucess. navigate to the homepage.");
                                Toast.makeText(mContext, "Sign in success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }


            }
        });

        mCreateOne_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to the page for creating new account.");
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * ----------------------firebase------------------------
     */

    private void setupFirebase() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in"+user.getUid());
                } else{
                    Log.d(TAG, "onAuthStateChanged: sign out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in.
        if(mAuthStateListener!=null) mAuth.removeAuthStateListener(mAuthStateListener);
    }




}
