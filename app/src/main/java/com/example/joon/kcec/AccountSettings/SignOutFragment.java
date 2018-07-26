package com.example.joon.kcec.AccountSettings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.joon.kcec.LoginNRegister.LoginActivity;
import com.example.joon.kcec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgtes
    private Button singOut_btn;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);
        singOut_btn = view.findViewById(R.id.btnConfirmSignout);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        setupFirebase();
        singOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mAuth.signOut();
                getActivity().finish();

            }
        });


        return view;
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
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
