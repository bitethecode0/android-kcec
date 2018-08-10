package com.example.joon.kcec.AccountSettings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String user_id;

    //widgest
    private ImageView mBackArrow_btn, mCheckMark;
    private CircleImageView mProfile_image;
    private TextView mChangeProfileImage_btn;
    private EditText mUsername, mEmail;

    //vars
    private User mUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mProfile_image = view.findViewById(R.id.profile_image);
        mChangeProfileImage_btn = view.findViewById(R.id.changePhoto_tv);

        mUsername = view.findViewById(R.id.input_username);
        mEmail = view.findViewById(R.id.input_email);

        mBackArrow_btn = view.findViewById(R.id.back_arrow);
        mBackArrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mCheckMark = view.findViewById(R.id.check_mark);
        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileInfo();
            }
        });

        mFirebaseMethods = new FirebaseMethods(getActivity());
        setupFirebaseAuth();


        return view;
    }

    private void saveProfileInfo() {
//        final String name = mName.getText().toString();
        final String username = mUsername.getText().toString();
        final String userEmail = mEmail.getText().toString();
        if(!mUser.getUsername().equals(username)  ){
            checkIfUsernameExists(username);
        }
        if(!mUser.getEmail().equals(userEmail)){
            checkIfUserEmailExists(userEmail);
        }

    }



    private void setProfileWidgets(User user){
        Log.d(TAG, "setProfileWidgets: set widgets with the data retrieved from the firebase"+user.toString());
        mUser =user;
        mUsername.setText(user.getUsername());
        mEmail.setText(user.getEmail());

    }

    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if "+username+ " already exists.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username", Toast.LENGTH_SHORT).show();
                    mCheckMark.setImageResource(R.drawable.ic_checkmark_1);
                }

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found a match "+ singleSnapshot.getValue(User.class).getUsername());
                    Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();

                }
                mUsername.setText(mUser.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void checkIfUserEmailExists(final String userEmail) {
        Log.d(TAG, "checkIfUsernameExists: checking if "+userEmail+ " already exists.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_email))
                .equalTo(userEmail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    mFirebaseMethods.updateUserEmail(userEmail);
                    Toast.makeText(getActivity(), "saved user email", Toast.LENGTH_SHORT).show();
                    mCheckMark.setImageResource(R.drawable.ic_checkmark_1);
                }

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found a match "+ singleSnapshot.getValue(User.class).getEmail());
                    Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                }
                mUsername.setText(mUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * ----------------------firebase------------------------
     */
    /**
     *
     * check to see if @param user is logged in.
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebase: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user_id = mAuth.getCurrentUser().getUid();

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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * retrieve user information from the database
                 */
                Log.d(TAG, "onDataChange: data from the database : " + mFirebaseMethods.getUserInfo(dataSnapshot));
                setProfileWidgets(mFirebaseMethods.getUserInfo(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener!=null) mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
