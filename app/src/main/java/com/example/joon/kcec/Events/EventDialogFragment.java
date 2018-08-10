package com.example.joon.kcec.Events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.joon.kcec.Model.Event;
import com.example.joon.kcec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDialogFragment extends android.support.v4.app.DialogFragment {
    private static final String TAG = "EventDialogFragment";

    //widgest
    private TextView mEventDate;
    private ListView mEventName;
    private RelativeLayout mNewEvent;

    //vars
    private Context mContext;
    private Date mSelectedDate;
    private List<String> selectedDateEventNames; // only event's name
    private ArrayList<Event> selectedDateEventsList; // event model

    static EventDialogFragment newInstance(Date date){
        EventDialogFragment dialog = new EventDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("date info",  date);
        dialog.setArguments(args);
        return dialog;
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.fragment_event_dialog);
        }



        return builder.create();
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext= getActivity();
        mSelectedDate = (Date)getDateFromBundle();
        selectedDateEventNames = new ArrayList<>();
        //------------------------------------------test
        selectedDateEventsList = new ArrayList<>();
        Log.d(TAG, "onCreate: date picked : "+mSelectedDate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_dialog, container, false);



        mEventDate = view.findViewById(R.id.date_dialog);
        mEventName = view.findViewById(R.id.event_list_dialog);
        mNewEvent = view.findViewById(R.id.newEvent_dialog);
        mNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
                NavToAddNewEventFragment();

            }
        });


        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_events)).child(getString(R.string.field_date));


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    Log.d(TAG, "onDataChange: 1: " + ds.getValue(Event.class).getDate());
                    Log.d(TAG, "onDataChange: 2: " + ds.getKey());
                    Log.d(TAG, "onDataChange: 3: " + ds.getValue());


                    try {
                        if (ds.getValue(Event.class).getDate().compareTo(mSelectedDate)==0) {
                            selectedDateEventNames.add(ds.getValue(Event.class).getEvent_name());
                            selectedDateEventsList.add(ds.getValue(Event.class));
                        }

                    } catch (NullPointerException e) {
                        Log.d(TAG, "onDataChange: NullPointerException" + e.getMessage());
                    }

                }

                setupEventsListview(selectedDateEventNames, selectedDateEventsList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    //--------------------------------------------------------------------------test

        private void setupEventsListview(final List<String> selectedDateEventnames, final ArrayList<Event> selectedDateEvents) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selectedDateEventnames);
        mEventName.setBackgroundResource(R.drawable.background_bottomline);
        mEventName.setAdapter(adapter);
        mEventName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDialog().cancel();
                Fragment fragment = new EventDetailViewFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.event_detail_info), selectedDateEvents.get(position));

                fragment.setArguments(args);

                android.support.v4.app.FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.test_container, fragment);
                transaction1.addToBackStack(getString(R.string.event_view_detail_fragment));
                transaction1.commit();
            }
        });
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

    private void NavToAddNewEventFragment(){
        Fragment fragment = new EventNewEventFragment();
        Bundle args= new Bundle();
        args.putSerializable(getString(R.string.date_info), mSelectedDate);
        fragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.test_container, fragment);
        transaction1.addToBackStack(getString(R.string.event_input_fragment));
        transaction1.commit();
    }
}
