package com.example.joon.kcec.AccountSettings;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.joon.kcec.R;
import com.example.joon.kcec.Utils.SectionPagerAdapter;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    //widgets
    private ImageView backArrow_btn;
    private ViewPager mViewPager;
    private RelativeLayout mParentRelLayout;

    //vars
    private SectionPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mParentRelLayout = findViewById(R.id.relLayoutParent);
        mViewPager = findViewById(R.id.viewpager_container);

        backArrow_btn= findViewById(R.id.back_arrow);
        backArrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to the previous page.");
                finish();
            }
        });
        setupFragment();
        setupSettingsList();
    }

    private void setupFragment() {
        mPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new EditProfileFragment());
        mPagerAdapter.addFragment(new SignOutFragment());

    }


    private void setupSettingsList() {
        ListView listView = findViewById(R.id.listView);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));
        options.add(getString(R.string.sign_out_frament));

        ArrayAdapter adapter = new ArrayAdapter(AccountActivity.this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setViewPager(position);
            }
        });


    }

    private void setViewPager(int position) {
        mParentRelLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(position);
    }
}
