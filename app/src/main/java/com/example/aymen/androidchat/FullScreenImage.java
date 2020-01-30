package com.example.aymen.androidchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;

    private PhotoView photoView;
    private String thumb_url;
    private String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        toolbar = findViewById(R.id.fullimageview_activity_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressBar = findViewById(R.id.fullimageview_progress);
        photoView = (PhotoView) findViewById(R.id.fullphoto_view);
        Intent intent = getIntent();
        thumb_url = intent.getStringExtra("thumb_image_url");
        url = intent.getStringExtra("image_url");



        Picasso.get().load(thumb_url).placeholder(R.drawable.ic_male).into(photoView, new Callback() {
            @Override
            public void onSuccess() {

                Picasso.get().load(url).into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Exception e) {

                    }

                });

            }

            @Override
            public void onError(Exception e) {

            }

        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }
}
