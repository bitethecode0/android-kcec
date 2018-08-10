package com.example.joon.kcec.Gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.joon.kcec.Home.HomeActivity;
import com.example.joon.kcec.Model.Photo;
import com.example.joon.kcec.Model.QuestionInfo;
import com.example.joon.kcec.QuestionAnswer.QuestionViewFragment;
import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.PostQuestionAdapter;
import com.example.joon.kcec.Utils.ShowAlbumsAdapter;
import com.example.joon.kcec.Utils.UniversalImageLoader;
import com.example.joon.kcec.Utils.ViewPostFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends HomeActivity implements ShowAllPhotosFragment.OnGridImageSelectedListener{

    @Override
    public void onGridImageSelected(Photo photo, int activity_num) {
        Log.d(TAG, "onGridImageSelected: navigate to the view post fragment with selected photo.");
        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();

        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_num), activity_num);
        fragment.setArguments(args);

        showFrameLayout();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    private static final String TAG = "GalleryActivity";
    private static final int RESULT_LOAD_IMAGE = 1;

    //widgets
    private ImageView all_photos ;
    private FloatingActionButton add_newPhoto_btn;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelParentLayout;
    
    private RecyclerView mRecyclerView;


    //vars
    private Context mContext;
    //upload fn
    private List<String> filenameList;
    private List<Uri> imgURIs;
    private String mAppend = "content:/";
    //recycler view
    private ArrayList<String> album_names;
    private ShowAlbumsAdapter mAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_gallery, mBaseFrameLayout);

        navigation_view.post(new Runnable() {
            @Override
            public void run() {
                navigation_view.getMenu().getItem(2).setChecked(true);
            }
        });
        mTitle.post(new Runnable() {
            @Override
            public void run() {
                mTitle.setText("Gallery");
            }
        });
        mContext= GalleryActivity.this;
        album_names = new ArrayList<>();
        initRecyclerView();

        /**
         * up load fn ----------------------
         *
         *
         */
        imgURIs = new ArrayList<>();
        filenameList = new ArrayList<>();

        initImageLoader();

        add_newPhoto_btn = findViewById(R.id.add_photo_btn);
        add_newPhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select pictures"), RESULT_LOAD_IMAGE);


            }
        });


        //after selecting photos end-------------------------
        mRelParentLayout = findViewById(R.id.relParent);
        mFrameLayout = findViewById(R.id.frameLayout);

    }

    private void initRecyclerView() {
        album_names.add(getString(R.string.all_photos));
        mRecyclerView= findViewById(R.id.recyclerView_gallery);
        mAdapter = new ShowAlbumsAdapter(mContext, album_names);
        /**
         * recycler view onclick listener
         */
        mAdapter.setOnClickListener(new ShowAlbumsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d(TAG, "onItemClicked: show all photos now.");
                showFrameLayout();
                Fragment fragment = new ShowAllPhotosFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(getString(R.string.show_all_photos_fragmet));
                transaction.commit();
            }
        });
                
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);*/
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * photo selection -> result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== RESULT_LOAD_IMAGE && resultCode ==RESULT_OK){
            if(data.getClipData()!=null){
                /**
                 * multiple photos selected                  */

                filenameList.clear();
                int totalItemSelected = data.getClipData().getItemCount();
                for(int i=0; i<totalItemSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String filename = getFileName(fileUri);

                    filenameList.add(filename);
                    imgURIs.add(fileUri);

                }

                Toast.makeText(mContext, "Selected multiple files.", Toast.LENGTH_SHORT).show();
                /**
                 *  --------------- navigate to the ShowSelectedPhotosActivity
                 *
                 */
                Intent intent = new Intent(mContext, ShowSelectedPhotosActivity.class);
                Bundle args = new Bundle();
                args.putStringArrayList(getString(R.string.img_filename), (ArrayList<String>) filenameList);
                args.putParcelableArrayList(getString(R.string.image_uris), (ArrayList <Uri>) imgURIs); //put img uris to upload to the storage

                intent.putExtras(args);
                startActivity(intent);

            } else if(data.getData()!=null){
                // one photo selected
                filenameList.clear();

                Uri fileUri = data.getData();
                String filename = getFileName(fileUri);

                filenameList.add(filename);
                imgURIs.add(fileUri);
                Toast.makeText(mContext, "Select one file.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ShowSelectedPhotosActivity.class);
                Bundle args = new Bundle();
                args.putStringArrayList(getString(R.string.img_filename), (ArrayList<String>) filenameList);
                args.putParcelableArrayList(getString(R.string.image_uris), (ArrayList <Uri>) imgURIs); //put img uris to upload to the storage

                intent.putExtras(args);
                startActivity(intent);
            }

        }
    }

    private void initImageLoader(){
        Log.d(TAG, "initImageLoader: initiated.");
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }



    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void hideFrameLayout(){
        mFrameLayout.setVisibility(View.GONE);
        mRelParentLayout.setVisibility(View.VISIBLE);
    }

    public void showFrameLayout(){
        mFrameLayout.setVisibility(View.VISIBLE);
        mRelParentLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
            hideFrameLayout();
        } else{
            finish();
        }
    }
}
