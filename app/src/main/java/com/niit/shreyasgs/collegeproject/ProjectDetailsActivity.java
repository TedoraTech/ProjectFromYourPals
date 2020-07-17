package com.niit.shreyasgs.collegeproject;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProjectDetailsActivity extends AppCompatActivity {

    private DatabaseReference projectsReference;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private Toolbar projectDetailsToolBar;

    private TextView projectName;
    private TextView yearOfSub;
    private TextView problemStatement;
    private TextView problemSolution;
    private TextView references;
    private TextView mentors;

    private RelativeLayout videoLayout;
    private RelativeLayout reportLayout;

    private ImageView videoImageView;
    private TextView videotextView;
    private ImageView reportImageView;
    private TextView reportTextview;

    private String clickedProject;
    private String user_id;
    private String current_user;
    private String otherUserId;

    private String projectByID = "abc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        projectsReference = FirebaseDatabase.getInstance().getReference().child("projects");
        user_id = getIntent().getStringExtra("userId");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        current_user = getIntent().getStringExtra("userId");

        projectDetailsToolBar = (Toolbar)findViewById(R.id.ProjectDetailsPage_toolBar);
        setSupportActionBar(projectDetailsToolBar);
        getSupportActionBar().setTitle("Project Details");


        projectName = (TextView)findViewById(R.id.ProjectDetailsPage_projectName);
        yearOfSub = (TextView)findViewById(R.id.ProjectDetailsPage_yearOfSub);
        problemStatement = (TextView)findViewById(R.id.ProjectDetailsPage_problemStatement);
        problemSolution = (TextView)findViewById(R.id.ProjectDetailsPage_proposedSolution);
        references = (TextView)findViewById(R.id.ProjectDetailsPage_references);
        mentors = (TextView)findViewById(R.id.ProjectDetailsPage_mentors);

        videoLayout = (RelativeLayout)findViewById(R.id.ProjectDetailsPage_videoRelativeLayout);
        reportLayout = (RelativeLayout)findViewById(R.id.ProjectDetailsPage_reportRelativeLayout);

        videoImageView = (ImageView)findViewById(R.id.ProjectDetailsPage_videoImageView);
        videotextView = (TextView)findViewById(R.id.ProjectDetailsPage_videoName);
        reportImageView = (ImageView)findViewById(R.id.ProjectDetailsPage_reportImageView);
        reportTextview = (TextView)findViewById(R.id.ProjectDetailsPage_reportText);

        clickedProject = getIntent().getStringExtra("clickedProject");

        mentors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=+918549860223";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        projectsReference.child(clickedProject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String nameOfProject = dataSnapshot.child("name").getValue().toString();
                projectName.setText("Project Name : " + nameOfProject);
                if(dataSnapshot.hasChild("projectbyid")){
                    projectByID = dataSnapshot.child("projectbyid").getValue().toString();
                }
                if(dataSnapshot.hasChild("probstatement")){
                    String probStatement = dataSnapshot.child("probstatement").getValue().toString();
                    problemStatement.setText("Problem Statement : "+ probStatement);
                }else{
                    problemStatement.setVisibility(View.GONE);
                }

                if(dataSnapshot.hasChild("projectbyid")){
                    otherUserId = dataSnapshot.child("projectbyid").getValue().toString();
                }

                if(dataSnapshot.hasChild("yos")){
                    String yob = dataSnapshot.child("yos").getValue().toString();
                    yearOfSub.setText("Year of Submission : " + yob);
                }else{
                    yearOfSub.setVisibility(View.INVISIBLE);
                }

                if(dataSnapshot.hasChild("description")){
                    String probSolution = dataSnapshot.child("description").getValue().toString();
                    problemSolution.setText("Problem Solution : " + probSolution);
                }else{
                    problemSolution.setVisibility(View.GONE);
                }

                if(dataSnapshot.hasChild("reference")){
                    String reference = dataSnapshot.child("reference").getValue().toString();
                    references.setText("Reference : " + reference);
                }else{
                    references.setVisibility(View.GONE);
                }

                if(dataSnapshot.hasChild("mentor")){
                    String mentor = dataSnapshot.child("mentor").getValue().toString();
                    mentors.setText("Mentor : "+ mentor);
                }else{
                    mentors.setVisibility(View.INVISIBLE);
                }

                if(dataSnapshot.hasChild("videourl") && dataSnapshot.hasChild("imageurl")){
                    videoLayout.setClickable(true);
                    videoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String downloadUrl = dataSnapshot.child("videourl").getValue().toString();
                            Intent videoIntent = new Intent(ProjectDetailsActivity.this, VideoPlayer.class);
                            videoIntent.putExtra("downloadurl", downloadUrl);
                            startActivity(videoIntent);
                        }
                    });

                    videoImageView.setVisibility(View.VISIBLE);
                    videotextView.setVisibility(View.VISIBLE);

                    Picasso.get().load(dataSnapshot.child("imageurl").getValue().toString()).placeholder(R.drawable.icon).into(videoImageView);
                    videotextView.setText(nameOfProject + ".mp4");
                }

                if(dataSnapshot.hasChild("reporturl") && dataSnapshot.hasChild("imageurl")){
                    reportLayout.setClickable(true);
                    reportLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String downloadUrl = dataSnapshot.child("reporturl").getValue().toString();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setDataAndType(Uri.parse(downloadUrl), "application/pdf");

                            Intent chooser = Intent.createChooser(browserIntent, "hello");
                            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

                            startActivity(chooser);
                        }
                    });

                    reportImageView.setVisibility(View.VISIBLE);
                    reportTextview.setVisibility(View.VISIBLE);

                    Picasso.get().load(dataSnapshot.child("imageurl").getValue().toString()).placeholder(R.drawable.icon).into(reportImageView);
                    reportTextview.setText(nameOfProject + ".pdf");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.project_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.projectDetailsPage_chatMenuOption);
        MenuItem editItem = menu.findItem(R.id.ProjectDetailsPage_edit);
        if(projectByID.equals(current_user) || projectByID.equals("abc")){
            item.setVisible(false);
            editItem.setVisible(true);
        }else{
            editItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.projectDetailsPage_chatMenuOption){
            Toast.makeText(ProjectDetailsActivity.this, "Opening chat..", Toast.LENGTH_SHORT).show();
            Intent chatIntent = new Intent(ProjectDetailsActivity.this, ChatActivity.class);
            chatIntent.putExtra("otherUserId", otherUserId);
            chatIntent.putExtra("clickedProject", clickedProject);
            startActivity(chatIntent);
            return true;
        }else if(id == R.id.ProjectDetailsPage_fav){
            final DatabaseReference favReference = FirebaseDatabase.getInstance().getReference().child("users").child(current_user).child("fav");
            final Map updateFav = new HashMap();

            projectsReference.child(clickedProject).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    String nameOfProject = dataSnapshot.child("name").getValue().toString();
                    updateFav.put("name", nameOfProject);
                    if(dataSnapshot.hasChild("probstatement")){
                        String probStatement = dataSnapshot.child("probstatement").getValue().toString();
                        updateFav.put("probstatement", probStatement);
                    }

                    if(dataSnapshot.hasChild("projectbyid")){
                        otherUserId = dataSnapshot.child("projectbyid").getValue().toString();
                        updateFav.put("projectbyid", otherUserId);
                    }

                    if(dataSnapshot.hasChild("yos")){
                        String yob = dataSnapshot.child("yos").getValue().toString();
                        updateFav.put("yos",yob);
                    }

                    if(dataSnapshot.hasChild("description")){
                        String probSolution = dataSnapshot.child("description").getValue().toString();
                        updateFav.put("description", probSolution);
                    }

                    if(dataSnapshot.hasChild("reference")){
                        String reference = dataSnapshot.child("reference").getValue().toString();
                        updateFav.put("reference", reference);
                    }

                    if(dataSnapshot.hasChild("mentor")){
                        String mentor = dataSnapshot.child("mentor").getValue().toString();
                        updateFav.put("mentor", mentor);
                    }

                    if(dataSnapshot.hasChild("videourl") && dataSnapshot.hasChild("imageurl")){
                        updateFav.put("videourl", dataSnapshot.child("videourl").getValue().toString());
                        updateFav.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());
                    }

                    if(dataSnapshot.hasChild("reporturl") && dataSnapshot.hasChild("imageurl")){
                        updateFav.put("reporturl", dataSnapshot.child("reporturl").getValue().toString());
                        updateFav.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());

                    }
                    favReference.child(clickedProject).updateChildren(updateFav, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(ProjectDetailsActivity.this, "Added to fav successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if(id == R.id.ProjectDetailsPage_edit){
            Intent editIntent = new Intent(ProjectDetailsActivity.this, EditActivity.class);
            editIntent.putExtra("clickedProject", clickedProject);
            startActivity(editIntent);
        }
        return true;
    }
}
