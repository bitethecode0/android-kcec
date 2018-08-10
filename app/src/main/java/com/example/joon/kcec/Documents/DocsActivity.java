package com.example.joon.kcec.Documents;

import android.os.Bundle;

import com.example.joon.kcec.Home.HomeActivity;
import com.example.joon.kcec.R;

public class DocsActivity extends HomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_docs, mBaseFrameLayout);
        navigation_view.post(new Runnable() {
            @Override
            public void run() {
                navigation_view.getMenu().getItem(1).setChecked(true);
            }
        });
        mTitle.post(new Runnable() {
            @Override
            public void run() {
                mTitle.setText("Documents");
            }
        });
    }
}
