package com.niit.shreyasgs.collegeproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AllProjectsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mainAuth;
    private DatabaseReference projectsReference;
    private DatabaseReference favReference;
    private DatabaseReference rootRef;
    private DatabaseReference eceProjects;
    private DatabaseReference mechProjects;
    private DatabaseReference cseProjects;

    private RecyclerView allProjectsRecyclerView;

    private String currentUserId;
    private long count = 10;
    private int i = 0;

    private String userName;
    private String tag = "All Projects";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_projects);

        mainAuth = FirebaseAuth.getInstance();
        FirebaseUser current_user = mainAuth.getCurrentUser();
        projectsReference = FirebaseDatabase.getInstance().getReference().child("projects");
        rootRef = FirebaseDatabase.getInstance().getReference();

        allProjectsRecyclerView = (RecyclerView)findViewById(R.id.AllProjectsPAge_allProjectsRecyclerView);
        allProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allProjectsRecyclerView.setHasFixedSize(true);

        if(current_user == null){
            Intent startIntent = new Intent(AllProjectsActivity.this, startActivity.class);
            startActivity(startIntent);
            finish();
        }else{
            currentUserId = current_user.getUid();
            sortingFunction();

            rootRef.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userName = dataSnapshot.child("username").getValue().toString();
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    headerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(AllProjectsActivity.this, "Opening account settings", Toast.LENGTH_SHORT).show();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        }
                    });
                    TextView userNameView = (TextView) headerView.findViewById(R.id.AllProjectsPage_userName);
                    userNameView.setText(userName);
                    CircleImageView userImageView = (CircleImageView) headerView.findViewById(R.id.AllProjectsPage_userImage);
                    userImageView.setImageResource(R.drawable.defaultdp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            eceProjects = FirebaseDatabase.getInstance().getReference().child("ece");
            mechProjects = FirebaseDatabase.getInstance().getReference().child("mech");
            cseProjects = FirebaseDatabase.getInstance().getReference().child("cse");
            projectsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    count = dataSnapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Projects");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    public void sortingFunction(){
        i++;
        if(i <= count){
            projectsReference.child("project"+i).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("tag")){
                        String tag = dataSnapshot.child("tag").getValue().toString();
                        if(tag.equals("ECE")){
                            Map updateECE = new HashMap();
                            String nameOfProject = dataSnapshot.child("name").getValue().toString();
                            updateECE.put("name", nameOfProject);
                            if(dataSnapshot.hasChild("probstatement")){
                                String probStatement = dataSnapshot.child("probstatement").getValue().toString();
                                updateECE.put("probstatement", probStatement);
                            }

                            if(dataSnapshot.hasChild("projectbyid")){
                                String projectById = dataSnapshot.child("projectbyid").getValue().toString();
                                updateECE.put("projectbyid", projectById);
                            }

                            if(dataSnapshot.hasChild("yos")){
                                String yob = dataSnapshot.child("yos").getValue().toString();
                                updateECE.put("yos",yob);
                            }

                            if(dataSnapshot.hasChild("description")){
                                String probSolution = dataSnapshot.child("description").getValue().toString();
                                updateECE.put("description", probSolution);
                            }

                            if(dataSnapshot.hasChild("reference")){
                                String reference = dataSnapshot.child("reference").getValue().toString();
                                updateECE.put("reference", reference);
                            }

                            if(dataSnapshot.hasChild("mentor")){
                                String mentor = dataSnapshot.child("mentor").getValue().toString();
                                updateECE.put("mentor", mentor);
                            }

                            if(dataSnapshot.hasChild("videourl") && dataSnapshot.hasChild("imageurl")){
                                updateECE.put("videourl", dataSnapshot.child("videourl").getValue().toString());
                                updateECE.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());
                            }

                            if(dataSnapshot.hasChild("reporturl") && dataSnapshot.hasChild("imageurl")){
                                updateECE.put("reporturl", dataSnapshot.child("reporturl").getValue().toString());
                                updateECE.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());

                            }
                            rootRef.child("ece").child("project"+i).updateChildren(updateECE, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                }
                            });
                            sortingFunction();
                        }if(tag.equals("MECH")){
                            Map updateMECH = new HashMap();
                            String nameOfProject = dataSnapshot.child("name").getValue().toString();
                            updateMECH.put("name", nameOfProject);
                            if(dataSnapshot.hasChild("probstatement")){
                                String probStatement = dataSnapshot.child("probstatement").getValue().toString();
                                updateMECH.put("probstatement", probStatement);
                            }

                            if(dataSnapshot.hasChild("projectbyid")){
                                String projectById = dataSnapshot.child("projectbyid").getValue().toString();
                                updateMECH.put("projectbyid", projectById);
                            }

                            if(dataSnapshot.hasChild("yos")){
                                String yob = dataSnapshot.child("yos").getValue().toString();
                                updateMECH.put("yos",yob);
                            }

                            if(dataSnapshot.hasChild("description")){
                                String probSolution = dataSnapshot.child("description").getValue().toString();
                                updateMECH.put("description", probSolution);
                            }

                            if(dataSnapshot.hasChild("reference")){
                                String reference = dataSnapshot.child("reference").getValue().toString();
                                updateMECH.put("reference", reference);
                            }

                            if(dataSnapshot.hasChild("mentor")){
                                String mentor = dataSnapshot.child("mentor").getValue().toString();
                                updateMECH.put("mentor", mentor);
                            }

                            if(dataSnapshot.hasChild("videourl") && dataSnapshot.hasChild("imageurl")){
                                updateMECH.put("videourl", dataSnapshot.child("videourl").getValue().toString());
                                updateMECH.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());
                            }

                            if(dataSnapshot.hasChild("reporturl") && dataSnapshot.hasChild("imageurl")){
                                updateMECH.put("reporturl", dataSnapshot.child("reporturl").getValue().toString());
                                updateMECH.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());

                            }
                            rootRef.child("mech").child("project"+i).updateChildren(updateMECH, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                }
                            });
                            sortingFunction();
                        }if(tag.equals("CSE")){
                            Map updateCSE = new HashMap();
                            String nameOfProject = dataSnapshot.child("name").getValue().toString();
                            updateCSE.put("name", nameOfProject);
                            if(dataSnapshot.hasChild("probstatement")){
                                String probStatement = dataSnapshot.child("probstatement").getValue().toString();
                                updateCSE.put("probstatement", probStatement);
                            }

                            if(dataSnapshot.hasChild("projectbyid")){
                                String projectById = dataSnapshot.child("projectbyid").getValue().toString();
                                updateCSE.put("projectbyid", projectById);
                            }

                            if(dataSnapshot.hasChild("yos")){
                                String yob = dataSnapshot.child("yos").getValue().toString();
                                updateCSE.put("yos",yob);
                            }

                            if(dataSnapshot.hasChild("description")){
                                String probSolution = dataSnapshot.child("description").getValue().toString();
                                updateCSE.put("description", probSolution);
                            }

                            if(dataSnapshot.hasChild("reference")){
                                String reference = dataSnapshot.child("reference").getValue().toString();
                                updateCSE.put("reference", reference);
                            }

                            if(dataSnapshot.hasChild("mentor")){
                                String mentor = dataSnapshot.child("mentor").getValue().toString();
                                updateCSE.put("mentor", mentor);
                            }

                            if(dataSnapshot.hasChild("videourl") && dataSnapshot.hasChild("imageurl")){
                                updateCSE.put("videourl", dataSnapshot.child("videourl").getValue().toString());
                                updateCSE.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());
                            }

                            if(dataSnapshot.hasChild("reporturl") && dataSnapshot.hasChild("imageurl")){
                                updateCSE.put("reporturl", dataSnapshot.child("reporturl").getValue().toString());
                                updateCSE.put("imageurl", dataSnapshot.child("imageurl").getValue().toString());

                            }
                            rootRef.child("cse").child("project"+i).updateChildren(updateCSE, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                }
                            });
                            sortingFunction();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        switch (tag) {
            case "All Projects": {
                getSupportActionBar().setTitle("All Projects");

                FirebaseRecyclerAdapter<projects, projectsViewHolder> projectsViewHolderAdapter = new FirebaseRecyclerAdapter<projects, projectsViewHolder>(
                        projects.class,
                        R.layout.single_project_holder,
                        projectsViewHolder.class,
                        projectsReference.orderByChild("yos")
                ) {
                    @Override
                    protected void populateViewHolder(projectsViewHolder viewHolder, projects model, int position) {
                        viewHolder.setImage(model.getImageurl());
                        viewHolder.setName(model.getName());
                        viewHolder.setYOB(model.getYos());
                        viewHolder.setStatement(model.getProbstatement());

                        final String clickedProject = getRef(position).getKey();

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent projectDetailsIntent = new Intent(AllProjectsActivity.this, ProjectDetailsActivity.class);
                                projectDetailsIntent.putExtra("clickedProject", clickedProject);
                                projectDetailsIntent.putExtra("userId", currentUserId);
                                startActivity(projectDetailsIntent);
                            }
                        });

                    }
                };

                allProjectsRecyclerView.setAdapter(projectsViewHolderAdapter);
                allProjectsRecyclerView.addItemDecoration(new DividerItemDecoration(AllProjectsActivity.this, LinearLayoutManager.VERTICAL));
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_projects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.AllProjectsPage_addNewProjectMenuIntem) {
            Intent newProject = new Intent(AllProjectsActivity.this, NewProjectActivity.class);
            newProject.putExtra("userID", currentUserId);
            startActivity(newProject);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_allProjects) {

            getSupportActionBar().setTitle("All Projects");

            FirebaseRecyclerAdapter<projects, projectsViewHolder> projectsViewHolderAdapter = new FirebaseRecyclerAdapter<projects, projectsViewHolder>(
                    projects.class,
                    R.layout.single_project_holder,
                    projectsViewHolder.class,
                    projectsReference.orderByChild("yos")
            ) {
                @Override
                protected void populateViewHolder(projectsViewHolder viewHolder, projects model, int position) {
                    viewHolder.setImage(model.getImageurl());
                    viewHolder.setName(model.getName());
                    viewHolder.setYOB(model.getYos());
                    viewHolder.setStatement(model.getProbstatement());

                    final String clickedProject = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent projectDetailsIntent = new Intent(AllProjectsActivity.this, ProjectDetailsActivity.class);
                            projectDetailsIntent.putExtra("clickedProject", clickedProject);
                            projectDetailsIntent.putExtra("userId", currentUserId);
                            startActivity(projectDetailsIntent);
                        }
                    });

                }
            };

            allProjectsRecyclerView.setAdapter(projectsViewHolderAdapter);
            allProjectsRecyclerView.addItemDecoration(new DividerItemDecoration(AllProjectsActivity.this, LinearLayoutManager.VERTICAL));
        } else if (id == R.id.nav_eceProjects) {

            getSupportActionBar().setTitle("ECE Projects");

            FirebaseRecyclerAdapter<projects, projectsViewHolder> projectsViewHolderAdapter = new FirebaseRecyclerAdapter<projects, projectsViewHolder>(
                    projects.class,
                    R.layout.single_project_holder,
                    projectsViewHolder.class,
                    eceProjects.orderByChild("yos")
            ) {
                @Override
                protected void populateViewHolder(projectsViewHolder viewHolder, projects model, int position) {
                    viewHolder.setImage(model.getImageurl());
                    viewHolder.setName(model.getName());
                    viewHolder.setYOB(model.getYos());
                    viewHolder.setStatement(model.getProbstatement());

                    final String clickedProject = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent projectDetailsIntent = new Intent(AllProjectsActivity.this, ProjectDetailsActivity.class);
                            projectDetailsIntent.putExtra("clickedProject", clickedProject);
                            projectDetailsIntent.putExtra("userId", currentUserId);
                            startActivity(projectDetailsIntent);
                        }
                    });

                }
            };

            allProjectsRecyclerView.setAdapter(projectsViewHolderAdapter);
            allProjectsRecyclerView.addItemDecoration(new DividerItemDecoration(AllProjectsActivity.this, LinearLayoutManager.VERTICAL));

        } else if (id == R.id.nav_cseProjects) {

            getSupportActionBar().setTitle("CSE Projects");

            FirebaseRecyclerAdapter<projects, projectsViewHolder> projectsViewHolderAdapter = new FirebaseRecyclerAdapter<projects, projectsViewHolder>(
                    projects.class,
                    R.layout.single_project_holder,
                    projectsViewHolder.class,
                    cseProjects.orderByChild("yos")
            ) {
                @Override
                protected void populateViewHolder(projectsViewHolder viewHolder, final projects model, int position) {
                    viewHolder.setImage(model.getImageurl());
                    viewHolder.setName(model.getName());
                    viewHolder.setYOB(model.getYos());
                    viewHolder.setStatement(model.getProbstatement());

                    final String clickedProject = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent projectDetailsIntent = new Intent(AllProjectsActivity.this, ProjectDetailsActivity.class);
                            projectDetailsIntent.putExtra("clickedProject", clickedProject);
                            projectDetailsIntent.putExtra("userId", currentUserId);
                            startActivity(projectDetailsIntent);
                        }
                    });

                }
            };

            allProjectsRecyclerView.setAdapter(projectsViewHolderAdapter);
            allProjectsRecyclerView.addItemDecoration(new DividerItemDecoration(AllProjectsActivity.this, LinearLayoutManager.VERTICAL));

        } else if (id == R.id.nav_mechProjects) {

            getSupportActionBar().setTitle("Mech Projects");

            FirebaseRecyclerAdapter<projects, projectsViewHolder> projectsViewHolderAdapter = new FirebaseRecyclerAdapter<projects, projectsViewHolder>(
                    projects.class,
                    R.layout.single_project_holder,
                    projectsViewHolder.class,
                    mechProjects.orderByChild("yos")
            ) {
                @Override
                protected void populateViewHolder(projectsViewHolder viewHolder, projects model, int position) {
                    viewHolder.setImage(model.getImageurl());
                    viewHolder.setName(model.getName());
                    viewHolder.setYOB(model.getYos());
                    viewHolder.setStatement(model.getProbstatement());

                    final String clickedProject = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent projectDetailsIntent = new Intent(AllProjectsActivity.this, ProjectDetailsActivity.class);
                            projectDetailsIntent.putExtra("clickedProject", clickedProject);
                            projectDetailsIntent.putExtra("userId", currentUserId);
                            startActivity(projectDetailsIntent);
                        }
                    });

                }
            };

            allProjectsRecyclerView.setAdapter(projectsViewHolderAdapter);
            allProjectsRecyclerView.addItemDecoration(new DividerItemDecoration(AllProjectsActivity.this, LinearLayoutManager.VERTICAL));

        } else if (id == R.id.MainMenu_LogoutMenuItem) {
            mainAuth.signOut();
            Intent startIntent = new Intent(AllProjectsActivity.this, startActivity.class);
            startActivity(startIntent);
            finish();
        }else if(id == R.id.MainMenu_favMenuItem){
            favReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("fav");
            getSupportActionBar().setTitle("Favourites");

            FirebaseRecyclerAdapter<projects, projectsViewHolder> projectsViewHolderAdapter = new FirebaseRecyclerAdapter<projects, projectsViewHolder>(
                    projects.class,
                    R.layout.single_project_holder,
                    projectsViewHolder.class,
                    favReference.orderByChild("yos")
            ) {
                @Override
                protected void populateViewHolder(projectsViewHolder viewHolder, projects model, int position) {
                    viewHolder.setImage(model.getImageurl());
                    viewHolder.setName(model.getName());
                    viewHolder.setYOB(model.getYos());
                    viewHolder.setStatement(model.getProbstatement());

                    final String clickedProject = getRef(position).getKey();

                    viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(AllProjectsActivity.this);
                            builder1.setMessage("Remove this project from your favourites ?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    favReference.child(clickedProject).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AllProjectsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialog.cancel();
                                }
                            });

                            builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                            return true;
                        }
                    });

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent projectDetailsIntent = new Intent(AllProjectsActivity.this, ProjectDetailsActivity.class);
                            projectDetailsIntent.putExtra("clickedProject", clickedProject);
                            projectDetailsIntent.putExtra("userId", currentUserId);
                            startActivity(projectDetailsIntent);
                            Toast.makeText(AllProjectsActivity.this, "clicked On " + clickedProject, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            };

            allProjectsRecyclerView.setAdapter(projectsViewHolderAdapter);
            allProjectsRecyclerView.addItemDecoration(new DividerItemDecoration(AllProjectsActivity.this, LinearLayoutManager.VERTICAL));
        }else if(id == R.id.nav_queryForum){
            Intent queryPageIntent = new Intent(AllProjectsActivity.this, PersonalQuery.class);
            startActivity(queryPageIntent);
        }else if(id == R.id.MainMenu_buyItems){
//            Intent buyIntent = new Intent(AllProjectsActivity.this, BuyItemsActivity.class);
//            startActivity(buyIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class projectsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public projectsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView projectName = (TextView)mView.findViewById(R.id.singleProjectHolder_projectName);
            projectName.setText(name);
        }

        public void setStatement(String statement){
            TextView problemStatement = (TextView)mView.findViewById(R.id.singleProjectHolder_problemStatement);
            problemStatement.setText(statement);
        }

        public void setImage(String url){
            CircleImageView projectImage = (CircleImageView)mView.findViewById(R.id.singleProjectHolder_productImage);
            Picasso.get().load(url).placeholder(R.drawable.icon).into(projectImage);
        }

        public void setYOB(String yearOfSub){
            TextView yob = (TextView)mView.findViewById(R.id.singleProjectHolder_yearOfSubmition);
            yob.setText("Year of Submission : " + yearOfSub);
        }
    }
}
