package com.group_project.chatapplication.userSettings;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.commonActivities.Image_Preview_Activity;
import com.group_project.chatapplication.registration.Registration_Activity;
import com.group_project.chatapplication.registration.User_Model;
import com.group_project.chatapplication.wallPaper.Cropper_Preview_Wallpaper_Activity;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    ImageView back_to_main_screen;
    CircleImageView user_profile_image;
    TextView user_name_txt, user_about_txt, jump_to_edit_profile_txt, jump_to_set_wallpaper_txt, txt_logout;
    MaterialButton btn1, btn2, btn3, btn4;
    RelativeLayout jump_to_edit_profile_layout, jump_to_set_wallpaper_layout;
    ActivityResultLauncher<String> mGetContent;
    String currentLoginUserId, name, phoneNumber, about, imageUri;
    User_Model usersModel;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back_to_main_screen = findViewById(R.id.back_to_main_screen);
        user_profile_image = findViewById(R.id.user_profile_image);
        user_name_txt = findViewById(R.id.user_name_txt);
        user_about_txt = findViewById(R.id.user_about_txt);
        jump_to_edit_profile_txt = findViewById(R.id.jump_to_edit_profile_txt);
        jump_to_set_wallpaper_txt = findViewById(R.id.jump_to_set_wallpaper_txt);
        txt_logout = findViewById(R.id.txt_logout);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        jump_to_edit_profile_layout = findViewById(R.id.jump_to_edit_profile_layout);
        jump_to_set_wallpaper_layout = findViewById(R.id.jump_to_set_wallpaper_layout);

        currentLoginUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        phoneNumber = user.getPhoneNumber();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Details").child(phoneNumber);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("phone")) {
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    about = Objects.requireNonNull(snapshot.child("about").getValue()).toString();
                    imageUri = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();

                    user_name_txt.setText(name);
                    user_about_txt.setText(about);
                    Glide.with(getApplicationContext()).load(imageUri).into(user_profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database error...", Toast.LENGTH_SHORT).show();
            }
        });

        user_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentId = new Intent(Profile_Activity.this, Image_Preview_Activity.class);
                intentId.putExtra("passSelectedImage", imageUri);
                intentId.putExtra("pass_current_name", user_name_txt.getText().toString().trim());
                startActivity(intentId);
            }
        });

        back_to_main_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        jump_to_edit_profile_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile_Activity.this, Edit_Profile_Activity.class));
            }
        });

        jump_to_edit_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        jump_to_set_wallpaper_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        jump_to_set_wallpaper_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile_Activity.this, Cropper_Preview_Wallpaper_Activity.class));
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(Profile_Activity.this, Cropper_Preview_Wallpaper_Activity.class);
                intent.putExtra("DATA", result.toString());
                startActivity(intent);

            }
        });

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Profile_Activity.this, Registration_Activity.class));
                finishAffinity();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about = "\uD83D\uDCBB At work";
                changeAbout(about);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about = "\uD83D\uDC68\u200D\uD83C\uDF93 At school";
                changeAbout(about);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about = "\uD83C\uDFAE Gaming";
                changeAbout(about);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about = "\uD83C\uDFA5 At the movies";
                changeAbout(about);
            }
        });

    }

    public void changeAbout(String newAbout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
        builder.setMessage("Do you want to change About?");
        builder.setCancelable(false);
        builder.setPositiveButton("CHANGE", (DialogInterface.OnClickListener) (dialog, which) -> {

            usersModel = new User_Model(auth.getUid(), name, phoneNumber, about, imageUri);
            databaseReference.setValue(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "About change!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
        builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}