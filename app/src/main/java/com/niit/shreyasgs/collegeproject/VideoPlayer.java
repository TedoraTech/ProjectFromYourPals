package com.niit.shreyasgs.collegeproject;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mVideoView = (VideoView)findViewById(R.id.VideoPlayerPage_videoView);

        try{
            String downloadURI = getIntent().getStringExtra("downloadurl");
            mVideoView.setVideoURI(Uri.parse(downloadURI));
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView.start();
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), "No Media found", Toast.LENGTH_LONG).show();
        }
    }
}
