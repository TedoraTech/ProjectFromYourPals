package com.niit.shreyasgs.collegeproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class  NewProjectActivity extends AppCompatActivity {

    private DatabaseReference projectsReference;
    private StorageReference videoReference;

    private Toolbar newProjectPageToolBar;

    private ProgressDialog uploadDialog;

    private EditText projectName;
    private EditText problemStatement;
    private EditText problemSolution;
    private EditText yos;
    private EditText references;
    private EditText mentor;

    private Button videoUploadButton;
    private Button reportUploadButton;
    private Button submitNewProjectButton;

    private Button EceButton;
    private Button CseButton;
    private Button MechButton;

    private String tag;
    private String imageVideotag;
    private String downloadUrl;

    private long count;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        imageVideotag = "video";
        currentUserId = getIntent().getStringExtra("userID");

        projectsReference = FirebaseDatabase.getInstance().getReference().child("projects");
        projectsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = (dataSnapshot.getChildrenCount()) + 1;
                Toast.makeText(NewProjectActivity.this, "count = " + count, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        videoReference = FirebaseStorage.getInstance().getReference();

        projectName = (EditText)findViewById(R.id.NewProjectPage_ProjectName);
        problemStatement = (EditText)findViewById(R.id.NewProjectPage_ProblemStatement);
        problemSolution = (EditText)findViewById(R.id.NewProjectPage_ProposedSolution);
        yos = (EditText)findViewById(R.id.NewProjectPage_yos);
        references = (EditText)findViewById(R.id.NewProjectPage_References);
        mentor = (EditText)findViewById(R.id.NewProjectPage_Mentor);

        EceButton = (Button)findViewById(R.id.NewProjectPage_EceButton);
        EceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectsReference.child("project"+count).child("tag").setValue("ECE");
            }
        });

        CseButton = (Button)findViewById(R.id.NewProjectPage_CseButton);
        CseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectsReference.child("project"+count).child("tag").setValue("CSE");
            }
        });

        MechButton = (Button)findViewById(R.id.NewProjectPage_MechButton);
        MechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectsReference.child("project"+count).child("tag").setValue("MECH");
            }
        });

        videoUploadButton = (Button)findViewById(R.id.NewProjectPage_UploadVideo);
        videoUploadButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                if(imageVideotag.equals("video")){
                    tag = "video";
                    Intent uploadIntent = new Intent();
                    uploadIntent.setType("video/*");
                    uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(uploadIntent, "Select Video"), 1);
                }else if(imageVideotag.equals("image")){
                    tag = "image";
                    videoUploadButton.setClickable(false);
                    Intent uploadIntent = new Intent();
                    uploadIntent.setType("image/*");
                    uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(uploadIntent, "Select Image"), 1);
                }
            }
        });


        reportUploadButton = (Button)findViewById(R.id.NewProjectPage_UploadReport);
        reportUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = "pdf";
                reportUploadButton.setClickable(false);
                Intent uploadIntent = new Intent();
                uploadIntent.setType("application/pdf/*");
                uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(uploadIntent, "Select report"), 1);
            }
        });

        submitNewProjectButton = (Button)findViewById(R.id.NewProjectPage_submitProject);

        uploadDialog = new ProgressDialog(this);
        uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadDialog.setTitle("Uploading");

        newProjectPageToolBar = (Toolbar)findViewById(R.id.NewProjectPage_toolBar);
        setSupportActionBar(newProjectPageToolBar);
        getSupportActionBar().setTitle("Add a project");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitNewProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectname = projectName.getText().toString();
                String problemstatement = problemStatement.getText().toString();
                String problemsolution = problemSolution.getText().toString();
                String Year = yos.getText().toString();

                if(!projectname.isEmpty() && !problemsolution.isEmpty() && !problemstatement.isEmpty()){

                    Map addNewprojectMap = new HashMap();
                    addNewprojectMap.put("projectbyid", currentUserId);
                    addNewprojectMap.put("name", projectname);
                    addNewprojectMap.put("probstatement", problemstatement);
                    addNewprojectMap.put("description", problemsolution);
                    addNewprojectMap.put("yos", Year);
                    addNewprojectMap.put("reference", references.getText().toString());
                    addNewprojectMap.put("mentor", mentor.getText().toString());

                    projectsReference.child("project"+count).updateChildren(addNewprojectMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(NewProjectActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(NewProjectActivity.this, "Please fill the details of the project", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(tag.equals("video")){
            if(requestCode == 1 && resultCode == RESULT_OK){
                uploadDialog.setMessage("Please wait while we upload the video to our database...");
                uploadDialog.show();
                Uri videoUri = data.getData();
                final StorageReference uploadVideo = videoReference.child("projectvideos/").child("project"+count);
                uploadVideo.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        uploadVideo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadDialog.dismiss();
                                downloadUrl = uri.toString();
                                projectsReference.child("project"+count).child("videourl").setValue(downloadUrl);
                                videoUploadButton.setText("Upload Image");
                                imageVideotag = "image";
                            }
                        });
                    }
                });
            }else{
                uploadDialog.dismiss();
                Toast.makeText(NewProjectActivity.this, "Please select the file and try again", Toast.LENGTH_SHORT).show();
            }
        }else if(tag.equals("pdf")){
            if(requestCode == 1 && resultCode == RESULT_OK){
                uploadDialog.show();
                uploadDialog.setProgress(0);
                final Uri pdfUri = data.getData();
                Cursor cursor = getContentResolver().query(pdfUri, null, null, null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                String name = cursor.getString(nameIndex);
                AlertDialog alertDialog = new AlertDialog.Builder(NewProjectActivity.this).create();
                alertDialog.setMessage("Upload file " + name + " ?");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final StorageReference uploadPdf = FirebaseStorage.getInstance().getReference().child("projectreports/").child("project"+count+".pdf");
//                        uploadPdf.putFile(pdfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                uploadPdf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        uploadDialog.dismiss();
//                                        downloadUrl = uri.toString();
//                                        projectsReference.child("project"+count).child("reporturl").setValue(downloadUrl);
//                                    }
//                                });
//                            }
//                        });
                        uploadPdf.putFile(pdfUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int progess = (int) taskSnapshot.getBytesTransferred() * 100/ (int)taskSnapshot.getTotalByteCount();
                                uploadDialog.setProgress(progess);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                uploadPdf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploadDialog.dismiss();
                                        downloadUrl = uri.toString();
                                        projectsReference.child("project"+count).child("reporturl").setValue(downloadUrl);
                                    }
                                });
                            }
                        });
                    }
                });
                alertDialog.show();
            }else{
                uploadDialog.dismiss();
                Toast.makeText(NewProjectActivity.this, "Please select the file and try again", Toast.LENGTH_SHORT).show();
            }
        }else if(tag.equals("image")){
            if(requestCode == 1 && resultCode == RESULT_OK){
                uploadDialog.setMessage("Please wait while we upload the Image to our database...");
                uploadDialog.show();
                Uri imageUri = data.getData();
                final StorageReference uploadPdf = FirebaseStorage.getInstance().getReference().child("projectreports/").child("project"+count+".pdf");
                uploadPdf.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progess = (int) taskSnapshot.getBytesTransferred() * 100/ (int)taskSnapshot.getTotalByteCount();
                        uploadDialog.setProgress(progess);
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        uploadPdf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadDialog.dismiss();
                                downloadUrl = uri.toString();
                                projectsReference.child("project"+count).child("imageurl").setValue(downloadUrl);
                            }
                        });
                    }
                });

            }else{
                uploadDialog.dismiss();
                Toast.makeText(NewProjectActivity.this, "Please select the file and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
