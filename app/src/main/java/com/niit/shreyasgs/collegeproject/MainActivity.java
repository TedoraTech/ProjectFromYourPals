package com.niit.shreyasgs.collegeproject;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent startIntent = new Intent(MainActivity.this, startActivity.class);
            startActivity(startIntent);
            finish();
        }else{

            DatabaseReference classRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

            classRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String Class = dataSnapshot.child("class").getValue().toString();
                    if(Class.equals("student")){
                        Intent gotoIntent = new Intent(MainActivity.this, AllProjectsActivity.class);
                        startActivity(gotoIntent);
                        finish();
                    }else if(Class.equals("teacher")){
                        Toast.makeText(MainActivity.this, "You are signed up as teacher", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
//                        Intent gotoIntent = new Intent(MainActivity.this, TeachersActivity.class);
//                        startActivity(gotoIntent);
//                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
