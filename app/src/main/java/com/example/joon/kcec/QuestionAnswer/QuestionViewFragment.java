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
import android.widget.TextView;

import com.example.joon.kcec.Model.QuestionInfo;
import com.example.joon.kcec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionViewFragment extends Fragment {
    private static final String TAG = "QuestionViewFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private CircleImageView mProfile_iv;
    private TextView username_tv, posted_date, qusetionTopic, questionDes;

    //vars
    private Context mContext;
    private QuestionInfo mQuestionInfo;
    private String mQuestionTopic, mQuestionDescription;
    private String user_id;

    public QuestionViewFragment(){
        super();
        setArguments(new Bundle());
        mContext = getContext();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_question, container, false);
        Log.d(TAG, "onCreateView: getting the question info from the bundle");


        mProfile_iv = view.findViewById(R.id.profile_image_qanda);
        username_tv = view.findViewById(R.id.username_qanda);
        posted_date = view.findViewById(R.id.date_qanda);
        qusetionTopic = view.findViewById(R.id.question_topic);
        questionDes = view.findViewById(R.id.question_description);

        try{
            mQuestionInfo = getQuestionInfoFromBundle();

            /**
             * set widgets
             *
             */
            setWidgests(mQuestionInfo);
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException"+e.getMessage());
        }

        //setupFirebase();
        Log.d(TAG, "onCreateView: user id :"+FirebaseAuth.getInstance().getCurrentUser().getUid());

        return view;
    }

    private void setWidgests(QuestionInfo questionInfo) {
        //mProfile_iv
        username_tv.setText(questionInfo.getUser_id());
        posted_date.setText(questionInfo.getPosted_date());
        qusetionTopic.setText(questionInfo.getQuestion_topic());
        questionDes.setText(questionInfo.getQuestion_description());
    }

    private QuestionInfo getQuestionInfoFromBundle(){
        Log.d(TAG, "getQuestionInfoFromBundle.");
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            return bundle.getParcelable(getString(R.string.question_info));
            //return bundle.getString("example");
        } else{
            return null;
        }
    }




    /*private void setupFirebase() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                getUsernameFromDB(user);

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
    }*/

}
