package com.example.joon.kcec.LoginNRegister;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.kcec.Model.User;
import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    //widgets
    private TextView top_tv;
    private EditText mUserName, mEmail, mPassword;
    private Button mRegister_btn;
    private ProgressBar mProgressBar;
    //vars
    private String username, email, password;
    private String append;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;

        top_tv=findViewById(R.id.register_tv);
        mUserName = findViewById(R.id.input_name);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mFirebaseMethods = new FirebaseMethods(mContext);


        setupFirebaseAuth();
        init();
    }

    private void init() {

        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                top_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                top_tv.setText("At least 8 characters.");
            }
        });


        mRegister_btn = findViewById(R.id.register_btn);
        mRegister_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUserName.getText().toString();
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                mProgressBar.setVisibility(View.VISIBLE);

                if(username.equals("") || email.equals("")|| password.equals("")){
                    Toast.makeText(mContext, "You need to fill out every field to register.", Toast.LENGTH_SHORT).show();
                } else{
                    Log.d(TAG, "onClick: attempt to register.");
                    mFirebaseMethods.registerNewAccount(email, password);

                }
            }
        });
    }

    private void checkIfUserExists(final String username) {
        Log.d(TAG, "checkIfUserExists: cheking if "+username
        +" already exists");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnap : dataSnapshot.getChildren()){
                    if(singleSnap.exists()){
                        Log.d(TAG, "onDataChange: found the match "+singleSnap.getValue(User.class).getUsername());
                        append = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "onDataChange: chnaged the username temporilily as"+username+append.substring(3,7) );
                    }
                }

                String modifiedUsername;
                if(append!=null){
                    modifiedUsername = username+append;
                } else{
                    modifiedUsername = username;
                }
                mFirebaseMethods.addNewUserToDatabase(email,0, modifiedUsername);
                mAuth.signOut();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * ----------------------firebase------------------------
     */

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in"+user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            checkIfUserExists(username);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    finish();
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
