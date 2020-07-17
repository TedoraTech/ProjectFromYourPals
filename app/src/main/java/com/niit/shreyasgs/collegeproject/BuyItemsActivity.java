package com.niit.shreyasgs.collegeproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyItemsActivity extends AppCompatActivity {

    private DatabaseReference itemsToBeBroughtRef;

    private AppCompatEditText itemsName;
    private Button sendButton;

    private String currentUserId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_items);

        itemsToBeBroughtRef = FirebaseDatabase.getInstance().getReference().child("items_to_bring");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait while we send the pic to the vendor");

        final RecyclerView itemsList = (RecyclerView)findViewById(R.id.buyItemsPage_RecyclerView);
        itemsList.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itemsList.setLayoutManager(linearLayoutManager);

        Toolbar buyItemsToolBar = (Toolbar) findViewById(R.id.buyItemsPage_toolbar);
        setSupportActionBar(buyItemsToolBar);
        getSupportActionBar().setTitle("Buy Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemsName = (AppCompatEditText)findViewById(R.id.buyItemsPage_itemsList);
        sendButton = (Button)findViewById(R.id.buyItemsPage_sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushID = itemsToBeBroughtRef.child(currentUserId).push().getKey();
                String nameOfItems = itemsName.getText().toString();
                Map itemsTextMap = new HashMap();
                itemsTextMap.put("type", "text");
                itemsTextMap.put("text", nameOfItems);
                itemsToBeBroughtRef.child(currentUserId).child(pushID).updateChildren(itemsTextMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        itemsName.setText("");
                    }
                });
            }
        });

        final FirebaseRecyclerAdapter<buyItemsModal, itemsPicHolder> itemsListPicAdapter = new FirebaseRecyclerAdapter<buyItemsModal, itemsPicHolder>(
                buyItemsModal.class,
                R.layout.single_image_holder,
                itemsPicHolder.class,
                itemsToBeBroughtRef.child(currentUserId)
        ) {
            @Override
            protected void populateViewHolder(itemsPicHolder viewHolder, buyItemsModal model, int position) {
                String type = model.getType();
                if(type.equals("text")){
                    viewHolder.setItemListText(model.getText());
                }else{
                    viewHolder.setItemListImage(model.getImage());
                }
            }
        };

        itemsList.setAdapter(itemsListPicAdapter);
        itemsListPicAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyCount = itemsListPicAdapter.getItemCount();
                int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(last == -1 || (positionStart >= (friendlyCount - 1) && last == (positionStart-1))){
                    itemsList.scrollToPosition(positionStart);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.buy_items_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        if(itemId == R.id.buyItemsMenu_takePic){
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(BuyItemsActivity.this);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri imageURI = data.getData();
            CropImage.activity(imageURI).setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.show();
                final String pushId = itemsToBeBroughtRef.child(currentUserId).push().getKey();
                Uri resultUri = result.getUri();
                final StorageReference buyItemsImagesReference = FirebaseStorage.getInstance().getReference();
                buyItemsImagesReference.child("listOfItems").child(currentUserId).child(pushId).putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        buyItemsImagesReference.child("listOfItems").child(currentUserId).child(pushId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                Map itemsPicsMap = new HashMap();
                                itemsPicsMap.put("image", downloadUrl);
                                itemsPicsMap.put("type", "image");

                                itemsToBeBroughtRef.child(currentUserId).child(pushId).updateChildren(itemsPicsMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static class itemsPicHolder extends RecyclerView.ViewHolder{

        View mView;

        public itemsPicHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setItemListImage(String url){
            ImageView ItemListImageView = (ImageView)mView.findViewById(R.id.singleImageHolder_imageView);
            Picasso.get().load(url).placeholder(R.drawable.icon).into(ItemListImageView);
        }

        public void setItemListText(String text){
            TextView ItemsListTextView = (TextView)mView.findViewById(R.id.singleImageHolder_textView);
            ImageView ItemListImageView = (ImageView)mView.findViewById(R.id.singleImageHolder_imageView);
            ItemListImageView.setVisibility(View.GONE);
            ItemsListTextView.setText("Me : " + text);
        }
    }
}
