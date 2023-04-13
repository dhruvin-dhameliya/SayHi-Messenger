package com.group_project.chatapplication.singleChat.single_chat_messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.commonActivities.Image_Preview_Activity;

import de.hdodenhof.circleimageview.CircleImageView;

public class Single_Chat_Info_Activity extends AppCompatActivity {

    CircleImageView contact_profile_img;
    TextView contact_name_txt, mobile_info, receiver_info_txt, jump_current_chat_iner_txt;
    String name, mobile, profileImage, about, contactName, contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_info);

        Intent intent = getIntent();
        name = intent.getStringExtra("title");
        mobile = intent.getStringExtra("mobile");
        profileImage = intent.getStringExtra("profileImage");
        about = intent.getStringExtra("about");

        contactName = intent.getStringExtra("contactName");
        contactNumber = intent.getStringExtra("contactNumber");

        contact_profile_img = findViewById(R.id.contact_profile_img);
        contact_name_txt = findViewById(R.id.contact_name_txt);
        mobile_info = findViewById(R.id.mobile_info);
        receiver_info_txt = findViewById(R.id.receiver_info_txt);
        jump_current_chat_iner_txt = findViewById(R.id.jump_current_chat_iner_txt);

        contact_name_txt.setText(name);
        mobile_info.setText(mobile.replace("+91", "+91 "));
        receiver_info_txt.setText(about);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users Details").child(mobile);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    contact_name_txt.setText(name);
                    mobile_info.setText(mobile.replace("+91", "+91 "));
                    receiver_info_txt.setText(about);
                    try {
                        Glide.with(contact_profile_img).load(profileImage).into(contact_profile_img);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    contact_profile_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentId = new Intent(Single_Chat_Info_Activity.this, Image_Preview_Activity.class);
                            intentId.putExtra("passSelectedImage", profileImage);
                            intentId.putExtra("pass_current_name", name.trim());
                            startActivity(intentId);
                        }
                    });

                } else {
                    contact_name_txt.setText(contactName);
                    try {
                        Glide.with(contact_profile_img).load(R.drawable.img_contact_user).into(contact_profile_img);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    mobile_info.setText("+91 " + contactNumber.replace("+91", ""));
                    receiver_info_txt.setText("Hey there! I am Not using Say Hi.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        jump_current_chat_iner_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}