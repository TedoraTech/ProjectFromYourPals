package com.niit.shreyasgs.collegeproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalQuery extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference queryReference;

    private Toolbar personalPageToolbar;
    private RecyclerView chatListRecyclerView;

    private String projectNumber;
    private String currentUser;

    private int i = 1;
    private int j = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_query);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        queryReference = FirebaseDatabase.getInstance().getReference().child("queries");

        personalPageToolbar = (Toolbar) findViewById(R.id.personalQueryPage_Toolbar);
        setSupportActionBar(personalPageToolbar);
        getSupportActionBar().setTitle("Queries Forum");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatListRecyclerView = (RecyclerView)findViewById(R.id.personalQueryPage_ChatListView);
        chatListRecyclerView.setHasFixedSize(true);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<projects, queryHolder> queriesReceivedHolderAdapter = new FirebaseRecyclerAdapter<projects, queryHolder>(
                projects.class,
                R.layout.single_query_holder,
                queryHolder.class,
                queryReference.child(currentUser + "Received")
        ) {
            @Override
            protected void populateViewHolder(queryHolder viewHolder, projects model, int position) {
                viewHolder.setName(model.getName(), i++);
                viewHolder.setImage(model.getImageurl());

                final String otherUserId = model.getOtherUserId();
                final String currentUserId = model.getCurrentUserId();
                final String clickedProject = model.getProjectId();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent chatIntent = new Intent(PersonalQuery.this, ChatActivity.class);
                        if(currentUser.equals(currentUserId)){
                            chatIntent.putExtra("otherUserId", otherUserId);
                        }else{
                            chatIntent.putExtra("otherUserId", currentUserId);
                        }
                        chatIntent.putExtra("clickedProject", clickedProject);
                        startActivity(chatIntent);
                    }
                });
            }
        };

        chatListRecyclerView.setAdapter(queriesReceivedHolderAdapter);

    }

    public static class queryHolder extends RecyclerView.ViewHolder{

        View mView;

        public queryHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name, int i){
            TextView projectName = (TextView)mView.findViewById(R.id.singleQueryHolder_projectName);
            projectName.setText(name + " - " + i);
        }

        public void setImage(String imageUrl){
            CircleImageView imageView = (CircleImageView)mView.findViewById(R.id.singleQueryHolder_projectImage);
            Picasso.get().load(imageUrl).placeholder(R.drawable.icon).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sent_queries_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.sentQueriesMenuOption){
            Intent sentIntent = new Intent(PersonalQuery.this, SentQueries.class);
            startActivity(sentIntent);
        }
        return true;
    }
}
