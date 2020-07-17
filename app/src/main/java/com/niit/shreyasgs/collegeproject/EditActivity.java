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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private DatabaseReference projectsReference;
    private StorageReference videoReference;

    private Toolbar newProjectPageToolBar;

    private ProgressDialog uploadDialog;

    private EditText projectName;
    private EditText problemStatement;
    private EditText problemSolution;
    private EditText yos;
    private EditText references;
    private EditText Mentor;

    private Button commitChanges;

    private String tag;
    private String imageVideotag;
    private String downloadUrl;

    private long count;
    private String currentUserId;

    private Toolbar projectDetailsToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        projectsReference = FirebaseDatabase.getInstance().getReference().child("projects");

        final String clickedProject = getIntent().getStringExtra("clickedProject");
        projectDetailsToolBar = (Toolbar)findViewById(R.id.NewProjectPage_toolBar);
        setSupportActionBar(projectDetailsToolBar);
        getSupportActionBar().setTitle("Edit Details");

        projectName = (EditText)findViewById(R.id.NewProjectPage_ProjectName);
        problemStatement = (EditText)findViewById(R.id.NewProjectPage_ProblemStatement);
        problemSolution = (EditText)findViewById(R.id.NewProjectPage_ProposedSolution);
        yos = (EditText)findViewById(R.id.NewProjectPage_yos);
        references = (EditText)findViewById(R.id.NewProjectPage_References);
        Mentor = (EditText)findViewById(R.id.NewProjectPage_Mentor);

        commitChanges = (Button)findViewById(R.id.NewProjectPage_submitProject);

        projectsReference.child(clickedProject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nameOfProject = dataSnapshot.child("name").getValue().toString();
                projectName.setText(nameOfProject);
                if(dataSnapshot.hasChild("probstatement")){
                    String probStatement = dataSnapshot.child("probstatement").getValue().toString();
                    problemStatement.setText(probStatement);
                }

                if(dataSnapshot.hasChild("description")){
                    String probSolution = dataSnapshot.child("description").getValue().toString();
                    problemSolution.setText(probSolution);
                }

                if(dataSnapshot.hasChild("reference")){
                    String reference = dataSnapshot.child("reference").getValue().toString();
                    references.setText(reference);
                }

                if(dataSnapshot.hasChild("mentor")){
                    String mentor = dataSnapshot.child("mentor").getValue().toString();
                    Mentor.setText(mentor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        commitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = projectName.getText().toString();
                String probStmt = problemStatement.getText().toString();
                String probSol = problemSolution.getText().toString();
                String ref = references.getText().toString();
                String mentor = Mentor.getText().toString();

                Map commitValues = new HashMap();
                commitValues.put("name", name);
                commitValues.put("probstatement", probStmt);
                commitValues.put("description", probSol);
                commitValues.put("reference", ref);
                commitValues.put("mentor", mentor);

                projectsReference.child(clickedProject).setValue(commitValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditActivity.this, "Changes will soon be reflected ", Toast.LENGTH_LONG).show();
                        Intent allProjectsIntent = new Intent(EditActivity.this, AllProjectsActivity.class);
                        startActivity(allProjectsIntent);
                        finish();
                    }
                });
            }
        });

    }
}
