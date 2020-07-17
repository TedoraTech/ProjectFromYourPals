package com.niit.shreyasgs.collegeproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private DatabaseReference chatRef;
    private DatabaseReference projectReference;
    private DatabaseReference queriesRef;
    private FirebaseAuth mAuth;
    private DatabaseReference otherUserRef;

    private Button sendButton;
    private EditText inputText;

    private String Current_user;
    private String otherUserId;

    private RecyclerView chatRecyclerView;

    private Toolbar chatToolBar;
    private long i = 0;

    private String clickedProject;
    private String tokenId;
    private String OtherTokenId;
    private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRef = FirebaseDatabase.getInstance().getReference().child("chats");
        projectReference = FirebaseDatabase.getInstance().getReference().child("projects");
        queriesRef = FirebaseDatabase.getInstance().getReference();

        clickedProject = getIntent().getStringExtra("clickedProject");

        FirebaseDatabase.getInstance().getReference().child("projects").child(clickedProject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projectName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        Current_user = mAuth.getCurrentUser().getUid();

        otherUserId = getIntent().getStringExtra("otherUserId");
        otherUserRef = FirebaseDatabase.getInstance().getReference().child(otherUserId);

        FirebaseDatabase.getInstance().getReference().child("users").child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("token")){
                    OtherTokenId = dataSnapshot.child("token").getValue().toString();
                }else{
                    Toast.makeText(ChatActivity.this, "No token present", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chat1ref = FirebaseDatabase.getInstance().getReference().child("chats").child(Current_user);
        chat1ref.keepSynced(true);
        DatabaseReference chat2Ref = FirebaseDatabase.getInstance().getReference().child("chats").child(otherUserId);
        chat2Ref.keepSynced(true);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                tokenId = task.getResult().getToken();
            }
        });

        inputText = (EditText)findViewById(R.id.inputMessage);
        sendButton = (Button)findViewById(R.id.sendButton);

        chatToolBar = (Toolbar)findViewById(R.id.chatPage_toolBar);
        setSupportActionBar(chatToolBar);
        getSupportActionBar().setTitle("Queries about project");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatRecyclerView = (RecyclerView)findViewById(R.id.chatRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setHasFixedSize(true);

        final FirebaseRecyclerAdapter<messages, chatViewHolder> chats = new FirebaseRecyclerAdapter<messages, chatViewHolder>(
                messages.class,
                R.layout.message_holder,
                chatViewHolder.class,
                chatRef.child(Current_user).child(otherUserId + clickedProject)
        ) {
            @Override
            protected void populateViewHolder(chatViewHolder viewHolder, messages model, int position) {
                if(model.getFrom().equals(Current_user)){
                    viewHolder.setTextMsgFrom(model.getText());
                }else{
                    viewHolder.setTextMsgTo(model.getText());
                }
            }
        };

        chatRecyclerView.setAdapter(chats);
        chats.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyCount = chats.getItemCount();
                int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(last == -1 || (positionStart >= (friendlyCount - 1) && last == (positionStart-1))){
                    chatRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        chatRef.child(Current_user).child(otherUserId + clickedProject).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    i = dataSnapshot.getChildrenCount();
                }else{
                    i = 0;
                    final Map queryMap = new HashMap();
                    projectReference.child(clickedProject).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("name")){
                                String name = dataSnapshot.child("name").getValue().toString();
                                queryMap.put("name", name);
                            }
                            if(dataSnapshot.hasChild("imageurl")){
                                String imageUrl = dataSnapshot.child("imageurl").getValue().toString();
                                queryMap.put("imageurl", imageUrl);
                            }
                            queryMap.put("otherUserId", otherUserId);
                            queryMap.put("projectId", clickedProject);
                            queryMap.put("currentUserId", Current_user);
                            queriesRef.child("queries").child(Current_user + "Sent").child(otherUserId + clickedProject).updateChildren(queryMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });

                            queriesRef.child("queries").child(otherUserId + "Received").child(Current_user + clickedProject).updateChildren(queryMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                DatabaseReference sample = FirebaseDatabase.getInstance().getReference().child(Current_user).push();
                String PushId = sample.getKey();

                String message = inputText.getText().toString();

                chats.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyCount = chats.getItemCount();
                        int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        if(last == -1 || (positionStart >= (friendlyCount - 1) && last == (positionStart-1))){
                            chatRecyclerView.scrollToPosition(positionStart);
                        }
                    }
                });

                Map chatMap = new HashMap();
                chatMap.put("num", i);
                chatMap.put("text", message);
                chatMap.put("from", Current_user.toString());
                chatMap.put("to", otherUserId.toString());

                JSONObject requestObject = new JSONObject(chatMap);
                try {
                    requestObject.put("token", tokenId);
                    requestObject.put("OtherToken", OtherTokenId);
                    requestObject.put("ProjectName", projectName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ChatActivity.this, projectName, Toast.LENGTH_LONG).show();

                JsonObjectRequest notificationRequest = new JsonObjectRequest(Request.Method.POST, "https://sithack.serveo.net/sendnotification", requestObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, "There was an error processing the request", Toast.LENGTH_LONG).show();
                    }
                });

                Volley.newRequestQueue(ChatActivity.this).add(notificationRequest);

                chatRef.child(Current_user).child(otherUserId + clickedProject).child(PushId).updateChildren(chatMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            inputText.setText("");
                        }
                    }
                });

                chatRef.child(otherUserId).child(Current_user + clickedProject).child(PushId).updateChildren(chatMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                    }
                });

            }
        });
    }

    public static class chatViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public chatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTextMsgFrom(String msg){
            TextView msgTextViewFrom = (TextView) mView.findViewById(R.id.MessageHolder_chatMessageFrom);
            TextView msgTextViewTo = (TextView)mView.findViewById(R.id.MessageHolder_chatMessageTo);
            msgTextViewFrom.setVisibility(View.INVISIBLE);
            msgTextViewTo.setText("Me : " +msg);

        }

        public void setTextMsgTo(String msg){
            TextView msgTextViewFrom = (TextView) mView.findViewById(R.id.MessageHolder_chatMessageFrom);
            TextView msgTextViewTo = (TextView)mView.findViewById(R.id.MessageHolder_chatMessageTo);
            msgTextViewFrom.setVisibility(View.INVISIBLE);
            msgTextViewTo.setText("Pal : " +msg);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatRef.child(Current_user).child(otherUserId + clickedProject).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    queriesRef.child("queries").child(Current_user + "Sent").child(otherUserId + clickedProject).removeValue();

                    queriesRef.child("queries").child(otherUserId + "Received").child(Current_user + clickedProject).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
