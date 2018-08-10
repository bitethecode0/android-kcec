package com.example.joon.kcec.QuestionAnswer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.joon.kcec.Home.HomeActivity;
import com.example.joon.kcec.Model.QuestionInfo;
import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.PostQuestionAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QandAActivity extends HomeActivity {
    private static final String TAG = "QandAActivity";
    //widgets
    private RelativeLayout mainLayout;
    private RecyclerView mQuestionList;
    private FloatingActionButton mFab;

    //vars
    private Context mContext;
    private PostQuestionAdapter mPostQuestionAdapter;
    private ArrayList<QuestionInfo> mQuestionListFromDB;
   /* private ArrayList<String> mQuestionTopic;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = QandAActivity.this;

        getPostedQuestionFromDB();
        getLayoutInflater().inflate(R.layout.activity_qand_a, mBaseFrameLayout);
        mainLayout = findViewById(R.id.mainLayout);
        navigation_view.post(new Runnable() {
            @Override
            public void run() {
                navigation_view.getMenu().getItem(3).setChecked(true);
            }
        });
        mTitle.post(new Runnable() {
            @Override
            public void run() {
                mTitle.setText("Q & A");
            }
        });
        mQuestionListFromDB = new ArrayList<>();




        mFab = findViewById(R.id.fab_newpost);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMainLayout();
                Fragment fragment = new QuestionDetailFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout_base, fragment);
                transaction.addToBackStack(getString(R.string.question_detail_fragment));
                transaction.commit();
            }
        });


    }

    private void initRecyclerView(ArrayList<QuestionInfo> questionInfoList) {
        mQuestionList= findViewById(R.id.recyclerView_qanda);
        mPostQuestionAdapter = new PostQuestionAdapter(this, questionInfoList);
        /**
         * recycler view onclick listener
         */
        mPostQuestionAdapter.setOnClickListner(new PostQuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, QuestionInfo questionInfo) {
                Log.d(TAG, "onItemClicked: navigate to the view question detail fragment : "+questionInfo.getQuestion_topic());
                Log.d(TAG, "onItemClicked: navigate to the view question detail fragment : "+questionInfo.getPosted_date());
                Log.d(TAG, "onItemClicked: navigate to the view question detail fragment : "+questionInfo.getQuestion_description());
                hideMainLayout();
                Fragment fragment = new QuestionViewFragment();
                Bundle args = new Bundle();
                /*args.putString("example", questionInfo.getQuestion_topic());*/
                args.putParcelable(getString(R.string.question_info),questionInfo);

                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout_base, fragment);
                transaction.addToBackStack(getString(R.string.question_view_fragment));
                transaction.commit();
            }
        });

        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);*/
        mQuestionList.setLayoutManager(new LinearLayoutManager(this));
        mQuestionList.setHasFixedSize(true);
        mQuestionList.setAdapter(mPostQuestionAdapter);


    }

    /*public void onItemSelected(QuestionInfo info){
        Log.d(TAG, "onItemSelected: info"+info.toString());
        Fragment fragment = new QuestionViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_base, fragment);
        transaction.addToBackStack(getString(R.string.question_view_fragment));
        transaction.commit();
    }
*/
    private void getPostedQuestionFromDB() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_questions));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    mQuestionListFromDB.add(ds.getValue(QuestionInfo.class));
                    /*mQuestionTopic.add(ds.getValue(QuestionInfo.class).getQuestion_topic());*/
                    initRecyclerView(mQuestionListFromDB);
                }
                Log.d(TAG, "onDataChange: question number is : "+mQuestionListFromDB.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void hideMainLayout(){
        mainLayout.setVisibility(View.GONE);
    }

    public void showMainLayout(){
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
            showMainLayout();
        } else{
            finish();
        }
    }
}