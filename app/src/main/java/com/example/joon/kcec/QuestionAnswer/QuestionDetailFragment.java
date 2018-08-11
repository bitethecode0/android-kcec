package com.example.joon.kcec.QuestionAnswer;

import android.content.Context;
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

import com.example.joon.kcec.Model.QuestionInfo;
import com.example.joon.kcec.Model.User;
import com.example.joon.kcec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QuestionDetailFragment extends Fragment{
    private static final String TAG = "QuestionDetailFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    //widgets
    private ImageView mUserProfile ;
    private EditText mPostDescription, mPostTopic;
    private TextView mUsername, mPostedDate, addNewQuestion_btn;


    //vars
    private Context mContext;
    private User user;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newquestion, container, false);
        mContext = getActivity();
        mUserProfile = view.findViewById(R.id.profile_image_qanda);
        mUsername = view.findViewById(R.id.username_qanda);
        mPostedDate = view.findViewById(R.id.date_qanda);
        mPostTopic = view.findViewById(R.id.question_topic);
        mPostDescription = view.findViewById(R.id.question_description);


        addNewQuestion_btn = view.findViewById(R.id.newQuestion_btn);

        setupFirebase();
        addNewQuestion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: save the question and navigate back to the activity.");

                addQuestionInfoToDB();
            }
        });


        return view;
    }

    private void addQuestionInfoToDB() {

        QuestionInfo questionInfo = new QuestionInfo();
        questionInfo.setPosted_date(getPostedTime());
        questionInfo.setQuestion_topic(mPostTopic.getText().toString());
        questionInfo.setQuestion_description(mPostDescription.getText().toString());

        Log.d(TAG, "addQuestionInfoToDB: user name "+user.getUsername());
        questionInfo.setUser_id(user.getUsername());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String newKey = reference.push().getKey();
        Log.d(TAG, "addQuestionInfoToDB: random number : "+newKey);
        reference.child(getString(R.string.dbname_questions)).child(newKey).setValue(questionInfo);


        Toast.makeText(getActivity(), "Your question is submitted.", Toast.LENGTH_SHORT).show();
        ((QandAActivity)mContext).showMainLayout();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public String getPostedTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentTime = sdf.format(new Date());
        Log.d(TAG, "getPostedTime: posted time : "+currentTime);

        return currentTime;
    }




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
        Log.d(TAG, "getUsernameFromDB: user id : "+FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(getString(R.string.dbname_users)).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onDataChange: username here "+user.getUsername());
                username = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
