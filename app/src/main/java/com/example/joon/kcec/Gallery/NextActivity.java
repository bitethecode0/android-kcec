package com.example.joon.kcec.Gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joon.kcec.R;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //widgets
    private EditText mPhotos_caption;
    private ImageView mPhotos_selected, mBackarrow_btn;
    private TextView mShare_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        mBackarrow_btn = findViewById(R.id.back_arrow);
        mShare_btn = findViewById(R.id.share_btn);
        mShare_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPhotos_selected = findViewById(R.id.photos_selected);
        mPhotos_caption = findViewById(R.id.photos_caption);



        getIncomingIntent();
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.image_urls))){
            Log.d(TAG, "getIncomingIntent: new incoming image urls. : "+intent.getStringArrayListExtra(getString(R.string.image_urls)));


        }
    }
}
