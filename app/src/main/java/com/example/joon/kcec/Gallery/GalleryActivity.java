package com.example.joon.kcec.Gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.kcec.Model.Photo;
import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.UniversalImageLoader;
import com.example.joon.kcec.Utils.UploadListAdatper;
import com.example.joon.kcec.Utils.ViewPostFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ShowAllPhotosFragment.OnGridImageSelectedListener
{



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
    private ImageView all_photos, add_newPhoto_btn;
    private TextView next_btn;
    private FrameLayout mFrameLayout;
    private FrameLayout mFrameLayout_only_recyclerview;
    private RelativeLayout mRelParentLayout;
    private RelativeLayout mNext_toolbar_layout;
    // upload fn
    private RecyclerView mUploadList;
    private UploadListAdatper mUploadListAdatper;



    //vars
    private Context mContext;
    //upload fn
    private List<String> filenameList;
    private List<String> fileUploadedList;
    private List<Uri> imgURIs;
    private List<String> imgfileUrls;
    private String mAppend = "content:/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);



        mContext= GalleryActivity.this;

        /**
         * up load fn ----------------------
         *
         *
         */
        imgURIs = new ArrayList<>();
        imgfileUrls = new ArrayList<>();


        initImageLoader();

        mUploadList = findViewById(R.id.recyclerView);
        filenameList = new ArrayList<>();
        fileUploadedList = new ArrayList<>();
        mUploadListAdatper = new UploadListAdatper(filenameList, fileUploadedList, imgfileUrls);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mUploadList.setLayoutManager(layoutManager);

        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(mUploadListAdatper);


        add_newPhoto_btn = findViewById(R.id.add_photo_btn);
        add_newPhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRecyclerLayout();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select pictures"), RESULT_LOAD_IMAGE);
            }
        });

        //up load fn end ------------------------


        //after selecting photos -------------------------

        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NextActivity.class);
                Bundle args = new Bundle();
                args.putStringArrayList(getString(R.string.img_filename), (ArrayList<String>) filenameList);
                args.putStringArrayList(getString(R.string.image_urls), (ArrayList<String>) imgfileUrls); // put img urls
                args.putParcelableArrayList(getString(R.string.image_uris), (ArrayList < Uri>) imgURIs); //put img uris to upload to the storage

                intent.putExtras(args);

                startActivity(intent);
            }
        });

        //after selecting photos end-------------------------
        mRelParentLayout = findViewById(R.id.relParent);
        mFrameLayout = findViewById(R.id.frameLayout);
        mFrameLayout_only_recyclerview = findViewById(R.id.frameLayout_only_recyclerview);
        mNext_toolbar_layout = findViewById(R.id.next_toolbar);
        mNext_toolbar_layout.setVisibility(View.GONE);


        all_photos = findViewById(R.id.all_photos);
        if(ShowAllPhotosFragment.photos.size()!=0){
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(ShowAllPhotosFragment.photos.get(0).getImage_path(), all_photos);
        }
        all_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to show all photos.");

                showFrameLayout();
                Fragment fragment = new ShowAllPhotosFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(getString(R.string.show_all_photos_fragmet));
                transaction.commit();

            }
        });
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
                 * multiple photos selected
                  */

                filenameList.clear();


                int totalItemSelected = data.getClipData().getItemCount();
                for(int i=0; i<totalItemSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String filename = getFileName(fileUri);

                    filenameList.add(filename);
                    fileUploadedList.add("uploading");
                    imgURIs.add(fileUri);
                    imgfileUrls.add(fileUri.toString());
                    mUploadListAdatper.notifyDataSetChanged();


                    Log.d(TAG, "onActivityResult: filenameList so far : "+ filename);
                    Log.d(TAG, "onActivityResult: image file without append "+ fileUri.toString());
                    Log.d(TAG, "onActivityResult: image file urls so far : "+imgfileUrls);


                    final int finalI = i;
                    Log.d(TAG, "onActivityResult: current index : "+finalI);
//                    final StorageReference fileToUpload =mStorageReference.child("photos").child("new album").child(filename);
//                    /**
//                     * upload files
//                     */
//                    UploadTask uploadTask = fileToUpload.putFile(fileUri);
//
//                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            fileUploadedList.remove(finalI);
//                            fileUploadedList.add(finalI, "done");
//
//
//                            Toast.makeText(mContext, "files succeessfully uploaded.", Toast.LENGTH_SHORT).show();
//
//
//
//
//                        }
//                    });
//
//                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                        @Override
//                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                            if(!task.isSuccessful()){
//                                throw  task.getException();
//                            }
//
//                            return fileToUpload.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if(!task.isSuccessful()){
//
//                            } else{
//                                Uri downloadUri = task.getResult();
//
//                                imgfileUrls.add(downloadUri.toString());
//
//                                Log.d(TAG, "onComplete: img file urls : "+imgfileUrls);
//                                mUploadListAdatper.notifyDataSetChanged();
//
//                                addNewPhotoToDatabase(downloadUri.toString());
//                            }
//                        }
//                    });
//
                }

//                fileUploadedList.clear();

                Toast.makeText(mContext, "Selected multiple files.", Toast.LENGTH_SHORT).show();
            } else if(data.getData()!=null){
                // one photo selected
                filenameList.clear();

                Uri fileUri = data.getData();
                String filename = getFileName(fileUri);

                filenameList.add(filename);
                fileUploadedList.add("uploading");
                imgURIs.add(fileUri);
                imgfileUrls.add(fileUri.toString());
                mUploadListAdatper.notifyDataSetChanged();


                Log.d(TAG, "onActivityResult: filenameList so far : " + filename);
                Log.d(TAG, "onActivityResult: image file without append " + fileUri.toString());
                Log.d(TAG, "onActivityResult: image file urls so far : " + imgfileUrls);


                Toast.makeText(mContext, "Select one file.", Toast.LENGTH_SHORT).show();
//                fileUploadedList.clear();
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

    public void showRecyclerLayout(){
        mRelParentLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.GONE);
        mFrameLayout_only_recyclerview.setVisibility(View.VISIBLE);
        mNext_toolbar_layout.setVisibility(View.VISIBLE);
    }

    public void hideFrameLayout(){
        mFrameLayout.setVisibility(View.GONE);
        mRelParentLayout.setVisibility(View.VISIBLE);
        mFrameLayout_only_recyclerview.setVisibility(View.GONE);
        mNext_toolbar_layout.setVisibility(View.GONE);
    }

    public void showFrameLayout(){
        mFrameLayout.setVisibility(View.VISIBLE);
        mRelParentLayout.setVisibility(View.GONE);
        mFrameLayout_only_recyclerview.setVisibility(View.GONE);
        mNext_toolbar_layout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
            hideFrameLayout();
        } else {

            Log.d(TAG, "onBackPressed: image url in the activity size : "+imgfileUrls.size());
            Log.d(TAG, "onBackPressed: image urls size : "+UploadListAdatper.imgfileUrls.size());


            if(mFrameLayout_only_recyclerview.getVisibility() == View.VISIBLE
                    && mRelParentLayout.getVisibility()== View.GONE){
                Intent intent = new Intent(this, GalleryActivity.class);
                startActivity(intent);
            } else if(mFrameLayout_only_recyclerview.getVisibility() == View.VISIBLE){
                showRecyclerLayout();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select pictures"), RESULT_LOAD_IMAGE);


            }




        }

    }



}
