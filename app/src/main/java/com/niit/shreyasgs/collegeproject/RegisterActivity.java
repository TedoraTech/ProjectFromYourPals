package com.niit.shreyasgs.collegeproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth registerAuth;
    private DatabaseReference registerReference;

    private EditText userName;
    private EditText emailID;
    private EditText dateOfJoining;
    private EditText usnNumber;
    private EditText collegeName;
    private EditText passWord;
    private EditText repeatPassword;
    private Button registerButton;
    private Button LoginButton;
    private RadioGroup roleGroup;

    private Toolbar registerPageToolBar;

    private ProgressDialog registerDialouge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerAuth = FirebaseAuth.getInstance();
        registerReference = FirebaseDatabase.getInstance().getReference();

        userName = (EditText)findViewById(R.id.RegisterPage_username);
        emailID = (EditText)findViewById(R.id.RegisterPage_emailID);
        dateOfJoining = (EditText)findViewById(R.id.RegisterPage_dateOfJoining);
        usnNumber = (EditText)findViewById(R.id.RegisterPage_USN);
        collegeName = (EditText)findViewById(R.id.RegisterPage_collegeName);
        passWord = (EditText)findViewById(R.id.RegisterPage_password);
        repeatPassword = (EditText)findViewById(R.id.RegisterPage_repeatPassword);
        registerButton = (Button)findViewById(R.id.RegisterPage_registerButton);
        LoginButton = (Button)findViewById(R.id.RegisterPage_loginButton);

        roleGroup = (RadioGroup) findViewById(R.id.RoleGroup);
        roleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton SelectedButton = (RadioButton) findViewById(i);
                if(SelectedButton.getText().equals("Teacher")){
                    usnNumber.setHint("Mobile Number");
                }else{
                    usnNumber.setHint("USN");
                }
            }
        });

        registerPageToolBar = (Toolbar)findViewById(R.id.RegisterPage_toolBar);
        setSupportActionBar(registerPageToolBar);
        getSupportActionBar().setTitle("Register");

        registerDialouge = new ProgressDialog(this);
        registerDialouge.setTitle("Signing up");
        registerDialouge.setMessage("Please wait while we store your credentials...");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userName.getText().toString();
                final String email = emailID.getText().toString();
                String password = passWord.getText().toString();
                final String dateofjining = dateOfJoining.getText().toString();
                final String usn = usnNumber.getText().toString();
                String repPassword = repeatPassword.getText().toString();
                final int selectId = roleGroup.getCheckedRadioButtonId();
                final RadioButton SelectedButton = (RadioButton) findViewById(selectId);


                if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !dateofjining.isEmpty()
                        && !usn.isEmpty() && !repPassword.isEmpty()){

                    if(password.equals(repPassword)){
                        registerDialouge.show();

                        registerAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    registerDialouge.dismiss();


                                    String current_user = registerAuth.getCurrentUser().getUid();

                                    Map updateRegistrationDetailsMap = new HashMap();
                                    updateRegistrationDetailsMap.put("username", username);
                                    updateRegistrationDetailsMap.put("email_id", email);
                                    updateRegistrationDetailsMap.put("class", SelectedButton.getText().toString().toLowerCase());
                                    updateRegistrationDetailsMap.put("dateofjoining", dateofjining);
                                    updateRegistrationDetailsMap.put(usnNumber.getHint().toString(), usn);

                                    registerReference.child("users").child(current_user).updateChildren(updateRegistrationDetailsMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            Toast.makeText(RegisterActivity.this, "Successful loading of data", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(RegisterActivity.this, "Please check the password and try again", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "Please fill in all the details and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
