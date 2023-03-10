package com.group_project.chatapplication.singleChat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.commonActivities.Image_Preview_Activity;
import com.group_project.chatapplication.groupChat.group_chat_messages.Group_Info_Activity;

import de.hdodenhof.circleimageview.CircleImageView;

public class Single_Chat_Info_Activity extends AppCompatActivity {

    CircleImageView contact_profile_img;
    TextView contact_name_txt, mobile_info, receiver_info_txt, jump_current_chat_iner_txt;
    String name, mobile, profileImage, about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_info);

        Intent intent = getIntent();
        name = intent.getStringExtra("title");
        mobile = intent.getStringExtra("mobile").replace("+91", "+91 ");
        profileImage = intent.getStringExtra("profileImage");
        about = intent.getStringExtra("about");

        contact_profile_img = findViewById(R.id.contact_profile_img);
        contact_name_txt = findViewById(R.id.contact_name_txt);
        mobile_info = findViewById(R.id.mobile_info);
        receiver_info_txt = findViewById(R.id.receiver_info_txt);
        jump_current_chat_iner_txt = findViewById(R.id.jump_current_chat_iner_txt);

        try {
            Glide.with(contact_profile_img).load(profileImage).into(contact_profile_img);
        } catch (Exception e) {
            contact_profile_img.setImageResource(R.drawable.img_default_person);
        }

        contact_name_txt.setText(name);
        mobile_info.setText(mobile);
        receiver_info_txt.setText(about);

        jump_current_chat_iner_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contact_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentId = new Intent(Single_Chat_Info_Activity.this, Image_Preview_Activity.class);
                intentId.putExtra("passSelectedImage", profileImage);
                intentId.putExtra("pass_current_name", name.trim());
                startActivity(intentId);
            }
        });

    }
}