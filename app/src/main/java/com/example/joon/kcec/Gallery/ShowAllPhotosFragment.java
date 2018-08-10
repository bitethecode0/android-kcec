package com.example.joon.kcec.Gallery;

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
import android.widget.GridView;
import android.widget.ImageView;

import com.example.joon.kcec.Model.Photo;
import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.GridImageAdapter;
import com.example.joon.kcec.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowAllPhotosFragment extends Fragment {

    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activity_num);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;


    private static final String TAG = "ShowAllPhotosFragment";
    private static final int NUM_GRID_COLUMS = 3;
    private static final int ACTIVITY_NUM = 3;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    //widgets
    private GridView mGridView;
    private ImageView mBackArrow_btn;



    //vars
    public static final ArrayList<Photo> photos = new ArrayList<>();
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_allphotos, container, false);

        mContext = getActivity();
        mGridView = view.findViewById(R.id.gridview);
        mBackArrow_btn = view.findViewById(R.id.back_arrow);
        mBackArrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to the previous page.");
                getActivity().getSupportFragmentManager().popBackStack();
                ((GalleryActivity)mContext).hideFrameLayout();
            }
        });
        setupFirebase();
        initImageLoader();
        setupGridview();




        return view;
    }




    private void setupGridview() {
        Log.d(TAG, "setupGridview: setting up image grid.");
        photos.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(getString(R.string.category_test));



        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    try{

                        // now needs to use hashmap
                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>)ds.getValue();
                        photo.setCaption(objectMap.get("caption").toString());
                        photo.setImage_path(objectMap.get("image_path").toString());

//                        Log.d(TAG, "onDataChange: data searching...");
//                        Photo photo = new Photo();
//
//                        photo.setImage_path(ds.getValue(Photo.class).getImage_path());
//                        photo.setCaption(ds.getValue(Photo.class).getCaption());
//                        Log.d(TAG, "onDataChange: image path : "+photo.getImage_path());
                        photos.add(photo);
                    } catch (NullPointerException e){
                        Log.e(TAG, "onDataChange: NullPointerException"+e.getMessage());
                    }


                }
                /**
                 *  img urls for gridview settings
                 */
                ArrayList<String> imageUrls=new ArrayList<>();
                for(int i =photos.size()-1; i>= 0; i--){
                    imageUrls.add(photos.get(i).getImage_path());

                }
                Log.d(TAG, "setupGridview: img urls : "+imageUrls);

                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMS;
                mGridView.setColumnWidth(imageWidth);

                GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview,"", imageUrls);
                mGridView.setAdapter(adapter);



                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void initImageLoader(){
        Log.d(TAG, "initImageLoader: initiated.");
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    /**
     * setup firebase
     */
    private void setupFirebase() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in, user id : "+user.getUid());
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

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener =(OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
        super.onAttach(context);
    }




}
