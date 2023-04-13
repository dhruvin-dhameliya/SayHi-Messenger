package com.group_project.chatapplication.commonActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.group_project.chatapplication.R;

public class Image_Preview_Activity extends AppCompatActivity {

    ImageView selected_img, back_to_home;
    TextView current_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        selected_img = findViewById(R.id.selected_img);
        back_to_home = findViewById(R.id.back_to_home);
        current_name = findViewById(R.id.current_name);

        String imgUrl = getIntent().getExtras().get("passSelectedImage").toString();
        String currentName = getIntent().getExtras().get("pass_current_name").toString();
        try {
            Glide.with(selected_img.getContext()).load(imgUrl).into(selected_img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        current_name.setText(currentName);

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}