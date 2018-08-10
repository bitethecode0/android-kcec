package com.example.joon.kcec.Events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.kcec.Model.Event;
import com.example.joon.kcec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventNewEventFragment extends Fragment{
    private static final String TAG = "EventNewEventFragment";

    public interface OnDataPass{
        public void onDatapass(String category, Date date);
    }
    OnDataPass dataPasser;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    //widgest
    private ImageView mBackArrow_btn;
    private TextView mDateshow_tv;
    private RelativeLayout mSaveNCloseLayout;
    private Spinner mSpinner;
    private EditText mInput_eventname, mInput_eventLocation, mInput_eventDescription;

    //vars
    private Date date;
    private String year, month, day;
    private Context mContext;
    private ArrayList<String> event_category;
    private String category;

    public EventNewEventFragment(){
        super();
        setArguments(new Bundle());
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        setupFirebase();

        mDateshow_tv = view.findViewById(R.id.show_date_tv);
        try{
            date = (Date) getDateFromBundle();
            Log.d(TAG, "onCreateView: date : "+date);
            year = (String) DateFormat.format("yyyy", date);
            month = (String) DateFormat.format("MM", date);
            day = (String) DateFormat.format("dd", date);
            mDateshow_tv.setText(month+"/"+day+"/"+year);
        } catch (NullPointerException e ){
            Log.e(TAG, "onCreateView: NullPointerException"+e.getMessage());
        }

        mBackArrow_btn = view.findViewById(R.id.back_arrow);
        mBackArrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        mSpinner = view.findViewById(R.id.spinner);
        mInput_eventname = view.findViewById(R.id.input_eventname);
        mInput_eventLocation= view.findViewById(R.id.input_eventlocation);
        mInput_eventDescription = view.findViewById(R.id.input_eventdescription);

        /**
         * spinner
         */
        event_category = new ArrayList<>();
        event_category.add(getString(R.string.weekly_event));
        event_category.add(getString(R.string.special_event));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, event_category);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selected item "+event_category.get(position));
                category = event_category.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSaveNCloseLayout = view.findViewById(R.id.saveNclose_layout);
        mSaveNCloseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: save the event and close the fragment");
                Log.d(TAG, "onSelectDate: add event to the firbase database.");

                String eventname = mInput_eventname.getText().toString();
                String eventlocation= mInput_eventLocation.getText().toString();
                String eventDescription = mInput_eventDescription.getText().toString();

                if(!eventname.equals("") && !eventlocation.equals("") && !eventDescription.equals("")){
                    addEventToDatabase(date, eventname, eventlocation, eventDescription);
                    dataPasser.onDatapass(category, date);
                    getActivity().getSupportFragmentManager().popBackStack();
                } else{
                    Toast.makeText(getActivity(), "you need to fill out every field to post new event.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }

    private Serializable getDateFromBundle(){
        Log.d(TAG, "getDateFromBundle: arguments : "+getArguments());
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            return bundle.getSerializable(getString(R.string.date_info));
        } else{
            return null;
        }
    }


    private void addEventToDatabase(Date date, String eventname, String eventLocation, String eventDescription) {
        /**
         * model and set to the firebase
         */
        Log.d(TAG, "addEventToDatabase: add event object to the firebase.");
        Event event = new Event();
        event.setDate(date);
        event.setEvent_category(category);
        event.setEvent_name(eventname);
        event.setEvent_location(eventLocation);
        event.setEvent_description(eventDescription);

        String newKey = myRef.push().getKey();

        myRef.child(getString(R.string.dbname_events)).
                child(getString(R.string.field_date)).child(newKey).setValue(event);

    }

    @Override
    public void onAttach(Context context) {
        try{
            dataPasser =(OnDataPass) getActivity();
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }

    /**
     * firebase
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
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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
