package com.example.joon.kcec.Gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.UploadFileAdatper;

import java.util.ArrayList;

public class ShowSelectedPhotosActivity extends AppCompatActivity {
    private static final String TAG = "ShowSelectedPhotosActiv";
    private static final int RESULT_LOAD_IMAGE = 1;

    //widgets
    private RecyclerView mUploadList;
    private TextView next_btn;

    //vars
    private Context mContext;
    private ArrayList<String> filenameList;
    private ArrayList<Uri> imgURIs;
    private UploadFileAdatper mUploadFileAdatper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_photos);
        mContext = this;
        getIncomingIntent();
        mUploadList = findViewById(R.id.recyclerView);

        mUploadFileAdatper = new UploadFileAdatper(filenameList, imgURIs);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mUploadList.setLayoutManager(layoutManager);
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(mUploadFileAdatper);

        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NextActivity.class);
                Bundle args = new Bundle();
                args.putStringArrayList(getString(R.string.img_filename), (ArrayList<String>) filenameList);
                /*args.putStringArrayList(getString(R.string.image_urls), (ArrayList<String>) imgfileUrls); // put img urls*/
                args.putParcelableArrayList(getString(R.string.image_uris), (ArrayList <Uri>) imgURIs); //put img uris to upload to the storage

                intent.putExtras(args);

                startActivity(intent);
            }
        });



    }

    private void getIncomingIntent() {
        Intent intent = getIntent();


        if (intent.hasExtra(getString(R.string.img_filename))) {
            Log.d(TAG, "getIncomingIntent: new image filenames : " + intent.getStringArrayListExtra(getString(R.string.img_filename)));
            filenameList = intent.getStringArrayListExtra(getString(R.string.img_filename));
        }
        if (intent.hasExtra(getString(R.string.image_uris))) {
            Log.d(TAG, "getIncomingIntent: new incoming image uris to upload to the firebase storage."
                    + intent.getParcelableArrayListExtra(getString(R.string.image_uris)));
            imgURIs = intent.getParcelableArrayListExtra(getString(R.string.image_uris));

        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: back btn pressed. navigate back to the photo selection part");

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select pictures"), RESULT_LOAD_IMAGE);
    }

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

}
