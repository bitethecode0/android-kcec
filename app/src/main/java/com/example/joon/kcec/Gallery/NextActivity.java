package com.example.joon.kcec.Gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.kcec.Model.Photo;
import com.example.joon.kcec.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private EditText mPhotos_caption;
    private ImageView mPhotos_selected, mBackarrow_btn;
    private TextView mShare_btn;


    //vars
    private Context mContext;
    private List<String> imgFilename;
    private List<Uri> imgURIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mContext = this;
        mBackarrow_btn = findViewById(R.id.back_arrow);
        mBackarrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mShare_btn = findViewById(R.id.share_btn);
        mShare_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *  1. add new photos to the firebase
                 *  2. navigate back to the gallery
                 */
                if (imgURIs != null && imgFilename != null) {
                    uploadPhotoToStorage(imgURIs);
                } else {
                    Toast.makeText(mContext, "something went wrong.", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(mContext, "Photos uploaded.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NextActivity.this, GalleryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPhotos_selected = findViewById(R.id.photos_selected);
        mPhotos_caption = findViewById(R.id.photos_caption);

        imgFilename = new ArrayList<>();
        imgURIs = new ArrayList<>();

        setupFirebase();
        getIncomingIntent();

    }

    private void getIncomingIntent() {
        Intent intent = getIntent();


        if (intent.hasExtra(getString(R.string.img_filename))) {
            Log.d(TAG, "getIncomingIntent: new image filenames : " + intent.getStringArrayListExtra(getString(R.string.img_filename)));
            imgFilename = intent.getStringArrayListExtra(getString(R.string.img_filename));
        }
        if (intent.hasExtra(getString(R.string.image_uris))) {
            Log.d(TAG, "getIncomingIntent: new incoming image uris to upload to the firebase storage."
                    + intent.getParcelableArrayListExtra(getString(R.string.image_uris)));
            imgURIs = intent.getParcelableArrayListExtra(getString(R.string.image_uris));

        }
    }

    public void uploadPhotoToStorage(List<Uri> imageUris) {

        for (int i = 0; i < imageUris.size(); i++) {

            String filename = imgFilename.get(i);
            final StorageReference fileToUpload = mStorageReference.child("photos").child("new album").child(filename);

            UploadTask uploadTask = fileToUpload.putFile(imageUris.get(i));

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(mContext, "files uploaded.", Toast.LENGTH_LONG).show();
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileToUpload.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (!task.isSuccessful()) {

                    } else {
                        Uri downloadUri = task.getResult();
                        addNewPhotoToDatabase(downloadUri.toString());
                    }
                }
            });
        }

    }


    public void addNewPhotoToDatabase(String url) {

        Log.d(TAG, "addNewPhotoToDatabase: add photo to the firbase database.");


        Photo photo = new Photo();
        photo.setImage_path(url);
        photo.setCaption(mPhotos_caption.getText().toString());
        String newKey = myRef.child(getString(R.string.dbname_user_photos)).push().getKey();
        assert newKey != null;

        myRef.child(getString(R.string.dbname_user_photos)).child(getString(R.string.category_test))
                .child(newKey).setValue(photo);


    }

    /**
     * firebase
     */
    private void setupFirebase() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: sign in" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: sign out");
                }
            }
        };
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
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
        if (mAuthStateListener != null) mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
