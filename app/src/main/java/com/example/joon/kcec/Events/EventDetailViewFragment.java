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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joon.kcec.Model.Event;
import com.example.joon.kcec.R;

import java.text.SimpleDateFormat;

public class EventDetailViewFragment extends Fragment {
    private static final String TAG = "EventDetailViewFragment";

    public EventDetailViewFragment(){
        super();
        setArguments(new Bundle());
        mContext=getActivity();
    }

    //widgets
    private ImageView mBackArrow_btn;
    private Context mContext;
    private TextView mEventDate,mEventName, mEventLoc,mEventDes;

    //vars
    private Event mEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail_onlyview,container,false);
        mBackArrow_btn = view.findViewById(R.id.back_arrow);
        mBackArrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mEventDate = view.findViewById(R.id.show_date_tv);
        mEventName = view.findViewById(R.id.input_eventname);
        mEventLoc = view.findViewById(R.id.input_eventlocation);
        mEventDes = view.findViewById(R.id.input_eventdescription);

        try{
            mEvent = getEventInfoFromBundle();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
            String date = sdf.format(mEvent.getDate());
            mEventDate.setText(date);
            mEventName.setText(mEvent.getEvent_name());
            mEventLoc.setText(mEvent.getEvent_location());
            mEventDes.setText(mEvent.getEvent_description());
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException : "+e.getMessage() );
        }
        return view;
    }

    public Event getEventInfoFromBundle(){
        Bundle bundle =getArguments();
        if(bundle!=null){
            return bundle.getParcelable(getString(R.string.event_detail_info));
        } else{
            return null;
        }

    }
}
