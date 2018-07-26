package com.example.joon.kcec.Utils;

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

import com.example.joon.kcec.Model.Photo;
import com.example.joon.kcec.R;

public class ViewPostFragment extends Fragment{
    private static final String TAG = "ViewPostFragment";

    
    //widgets
    private SquareImageView mPostImage;
    private TextView mImageCaptions;
    private ImageView mBackArrow_btn;
    //vars
    private Photo mPhoto;

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmet_viewpost, container, false);
        mBackArrow_btn = view.findViewById(R.id.back_arrow);
        mBackArrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to the previous page.");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        
        mPostImage = view.findViewById(R.id.post_image);
        mImageCaptions = view.findViewById(R.id.captions);

        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException"+e.getMessage() );
        }


        return view;
    }

    private Photo getPhotoFromBundle(){
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            return bundle.getParcelable(getString(R.string.photo));
        } else{
            return null;
        }
    }
}
