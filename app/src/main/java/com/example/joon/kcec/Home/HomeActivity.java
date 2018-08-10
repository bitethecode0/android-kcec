package com.example.joon.kcec.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joon.kcec.AccountSettings.AccountActivity;
import com.example.joon.kcec.Announcement.AnnoucementActivity;
import com.example.joon.kcec.Documents.DocsActivity;
import com.example.joon.kcec.Events.EventsActivity;
import com.example.joon.kcec.Gallery.GalleryActivity;
import com.example.joon.kcec.LoginNRegister.LoginActivity;
import com.example.joon.kcec.Model.User;
import com.example.joon.kcec.QuestionAnswer.QandAActivity;
import com.example.joon.kcec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    //widgtes
    private ImageView profileMenu;
    private ImageView mainMenu;
    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    protected NavigationView navigation_view;
    protected FrameLayout mBaseFrameLayout;
    protected TextView mTitle;
    protected Toolbar mToolbar;

    //vars
    private Context mContext;
    private User user;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupFirebase();

        mContext= HomeActivity.this;
        mBaseFrameLayout = findViewById(R.id.frameLayout_base);
        mTitle = findViewById(R.id.profileUsername);


        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerLayout.setBackgroundResource(R.color.background_menu);
        navigation_view = findViewById(R.id.navigation_view);
        setupDrawerContent(navigation_view);

        mainMenu = findViewById(R.id.menu);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        profileMenu = findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate to activity for managing account");
                Intent intent = new Intent(mContext, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    /*protected void checkItemSelected(MenuItem item, boolean trueOrFalse){
        item.setCheckable(trueOrFalse);
    }*/

    private void setupDrawerContent(final NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                /*checkItemSelected(item, true);*/
                item.setCheckable(true);
                selectDrawerItem(item);

                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {

        switch (item.getItemId()){
            case R.id.announcement:
                Intent intent1 =new Intent(this, AnnoucementActivity.class);
                startActivity(intent1);
                break;

            case R.id.document:
                Intent intent2 =new Intent(this, DocsActivity.class);
                startActivity(intent2);
                break;

            case R.id.gallery:
                Intent intent3 =new Intent(this, GalleryActivity.class);
                startActivity(intent3);
                break;

            case R.id.qAndA:
                Intent intent4 =new Intent(this, QandAActivity.class);
                startActivity(intent4);
                break;

            case R.id.calendarIcon:
                Intent intent5 =new Intent(this, EventsActivity.class);
                startActivity(intent5);
                break;
        }
        mDrawerLayout.closeDrawers();

    }

    /**
     * firebase --------------------------------------------
     * @param user
     */

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: check if user is currently logged in or not.");
        if(user==null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }


    private void setupFirebase() {
        Log.d(TAG, "setupFirebase: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkCurrentUser(user);

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in"+user.getUid());
                } else{
                    Log.d(TAG, "onAuthStateChanged: sign out");
                }
            }
        };


        try{
            DatabaseReference ref  = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child(getString(R.string.dbname_users)).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());


            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: username here "+user.getUsername());




                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e){
            Log.e(TAG, "setupFirebase: NullPointerException"+e.getMessage() );
        }

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

}
