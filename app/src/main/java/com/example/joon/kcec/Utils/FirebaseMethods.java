package com.example.joon.kcec.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.joon.kcec.Model.User;
import com.example.joon.kcec.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //firebase
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    
    //vars
    private String user_id;
    public FirebaseMethods(Context context){
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        if(mAuth.getCurrentUser()!=null){
            user_id = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * register new id
     * @param email
     * @param password
     */
    public void registerNewAccount(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Log.w(TAG, "onComplete: failed to register", task.getException());
                            Toast.makeText(mContext, "failed to register.", Toast.LENGTH_SHORT).show();

                        } else{
                            Log.d(TAG, "onComplete: register success.");

                            Toast.makeText(mContext, "Reigster success. Sednig verfication email.", Toast.LENGTH_SHORT).show();
                            user_id = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: auth state changed");
                        }
                    }
                });
    }

    public void addNewUserToDatabase(String email, long phone_number, String username){
        User user = new User(email, phone_number, user_id, username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(user_id)
                .setValue(user);




    }

    /**
     * return User's information from the firebase
     * @param dataSnapshot
     * @return
     */
    public User getUserInfo(DataSnapshot dataSnapshot){
        User user = new User();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "getUserInfo: getUserInfo : datasnapshot :" +ds);
                try{
                    user.setUsername(
                            ds.child(user_id)
                            .getValue(User.class)
                            .getUsername()
                    );

                    user.setEmail(
                            ds.child(user_id)
                                    .getValue(User.class)
                                    .getEmail()
                    );



                }catch (NullPointerException e){
                    Log.e(TAG, "getUserInfo: NullPointerException"+e.getMessage() );
                }
            }
        }

        return user;

    }


    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: update username to : "+username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(user_id)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }
}
