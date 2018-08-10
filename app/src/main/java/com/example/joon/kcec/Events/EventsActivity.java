package com.example.joon.kcec.Events;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import com.example.joon.kcec.Home.HomeActivity;
import com.example.joon.kcec.Model.Event;
import com.example.joon.kcec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventsActivity extends HomeActivity implements EventNewEventFragment.OnDataPass {
    private static final String TAG = "EventsFragment";

    @Override
    public void onDatapass(String category, Date date) {
        Log.d(TAG, "onDatapass: category : "+category);
        setSelectedDateColor(category, date);
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private CalendarView mCalendarView;
    private CaldroidFragment caldroidFragment;

    //vars
    private Date selectedDate;
    private Date testDate;
    private ArrayList<Event> allEvents = new ArrayList<>(); // firebase database
    private String category;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_events, mBaseFrameLayout);

        navigation_view.post(new Runnable() {
            @Override
            public void run() {
                navigation_view.getMenu().getItem(4).setChecked(true);
            }
        });
        mTitle.post(new Runnable() {
            @Override
            public void run() {
                mTitle.setText("Events");
            }
        });
        setupFirebase();

    }

    private void setCalendarFragment(ArrayList<Event> events) {
        Log.d(TAG, "setCalendarFragment: set up calendar fragment first.");

        caldroidFragment = new CaldroidFragment();

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH)+1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);
        for (int i = 0; i < events.size(); i++) {
            setSelectedDateColor(events.get(i).getEvent_category(), events.get(i).getDate());
        }


        final android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.test_container, caldroidFragment);
        transaction.commit();



        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate =date;
                Log.d(TAG, "onSelectDate: date : "+selectedDate);

                String year = (String) android.text.format.DateFormat.format("yyyy", date);
                String month = (String) android.text.format.DateFormat.format("MM", date);
                String day = (String) android.text.format.DateFormat.format("dd", date);

                Log.d(TAG, "onSelectDate: month  : "+month+"/"+day+"/"+year);

                /**
                 * show dialog
                 */
                showDialog();


            }
        };
        caldroidFragment.setCaldroidListener(listener);


    }

    private void showDialog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = EventDialogFragment.newInstance(selectedDate);
        newFragment.show(transaction, "dialog");

    }



    public void setSelectedDateColor(String category, Date selectedDate){
        Log.d(TAG, "setSelectedDateColor: color the background of events.");
        ColorDrawable green = new ColorDrawable(getResources().getColor(R.color.green));
        ColorDrawable purple = new ColorDrawable(getResources().getColor(R.color.purple));

        try{
            if (category.equals(getString(R.string.weekly_event))) {
                caldroidFragment.setBackgroundDrawableForDate(green, selectedDate);
            } else if (category.equals(getString(R.string.special_event))) {
                caldroidFragment.setBackgroundDrawableForDate(purple, selectedDate);
            }
        } catch (NullPointerException e){
            Log.e(TAG, "setSelectedDateColor: NullPointerException"+e.getMessage() );
        }

    }



    /*private void showAllEvent() {
        Log.d(TAG, "showAllEvent: here.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_events))
                .child(getString(R.string.field_date));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    allEvents.add(ds.getValue(Event.class));
                }

                Log.d(TAG, "onDataChange: selected date/event category : "+allEvents.get(0).getDate()+" "+allEvents.get(0).getEvent_category());

                for(int i=0; i<allEvents.size(); i++){
                    setSelectedDateColor(allEvents.get(i).getEvent_category(), allEvents.get(i).getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/

    /**
     * firebase
     */
    private void setupFirebase() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in : "+user.getUid());


                } else{
                    Log.d(TAG, "onAuthStateChanged: sign out");
                }
            }
        };


//        Query query = myRef.child(getString(R.string.dbname_events))
//                .child(getString(R.string.field_date));
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    allEvents.add(ds.getValue(Event.class));
//                }
//
//                Log.d(TAG, "onDataChange: selected date/event category : "+allEvents.get(0).getDate()+" "+allEvents.get(0).getEvent_category());
//                testDate =allEvents.get(0).getDate();
//                for(int i=0; i<allEvents.size(); i++){
//                    setSelectedDateColor(allEvents.get(i).getEvent_category(), allEvents.get(i).getDate());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        myRef.child(getString(R.string.dbname_events)).child(getString(R.string.field_date))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            allEvents.add(ds.getValue(Event.class));
                        }

                        //Log.d(TAG, "onDataChange: selected date/event category : " + allEvents.get(0).getDate() + " " + allEvents.get(0).getEvent_category());
                        /*for (int i = 0; i < allEvents.size(); i++) {
                            setSelectedDateColor(allEvents.get(i).getEvent_category(), allEvents.get(i).getDate());
                        }*/

                        setCalendarFragment(allEvents);
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
