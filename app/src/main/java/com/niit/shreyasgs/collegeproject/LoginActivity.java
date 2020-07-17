package com.niit.shreyasgs.collegeproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference loginDatabase;

    private EditText emailId;
    private EditText passWord;
    private Button loginButton;
    private Button registerButton;

    private Toolbar loginToolBar;
    private ProgressDialog loginDialouge;

    String current_user;
    String tokenId;
    String prevTokenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginDatabase = FirebaseDatabase.getInstance().getReference();

        emailId = (EditText)findViewById(R.id.LoginPage_emailID);
        passWord = (EditText)findViewById(R.id.LoginPage_password);
        loginButton = (Button)findViewById(R.id.Loginpage_loginButton);

        loginToolBar = (Toolbar)findViewById(R.id.LoginPage_toolBar);
        setSupportActionBar(loginToolBar);
        getSupportActionBar().setTitle("Login");

        loginDialouge = new ProgressDialog(this);
        loginDialouge.setTitle("Logging In");
        loginDialouge.setMessage("Please wait while we check your credentials...");

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    return;
                }
                tokenId = task.getResult().getToken();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = emailId.getText().toString();
                String password = passWord.getText().toString();

                if(!username.isEmpty() && !password.isEmpty()){
                    loginDialouge.show();
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                current_user = mAuth.getCurrentUser().getUid();
//                                loginDatabase.child("users").child(current_user).child("token").setValue(tokenId);

                                loginDatabase.child("users").child(current_user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String name = dataSnapshot.child("class").getValue().toString();
                                        prevTokenId = dataSnapshot.child("token").getValue().toString();
                                        Toast.makeText(LoginActivity.this, prevTokenId, Toast.LENGTH_LONG).show();
                                        if(prevTokenId.equals(tokenId)){
                                            if(name.equals("student")){
                                                loginDialouge.dismiss();
                                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            }else if(name.equals("teacher")){
                                                Toast.makeText(LoginActivity.this, "Login Successful as teacher", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                loginDialouge.dismiss();
                                                Toast.makeText(LoginActivity.this, "Error login in check the credentials and try again", Toast.LENGTH_SHORT).show();
                                                Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class);
                                                startActivity(loginIntent);
                                                finish();
                                            }
                                        }else{
                                            loginDialouge.dismiss();
                                            mAuth.signOut();
                                            Toast.makeText(LoginActivity.this, "The device you are using is not the same one", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else {
                                loginDialouge.dismiss();
                                Toast.makeText(LoginActivity.this, "check the credentials and try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Please fill in the details and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton = (Button)findViewById(R.id.LoginPage_registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }
}
