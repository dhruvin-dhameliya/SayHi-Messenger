package com.group_project.chatapplication.wallPaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;

public class Default_Wallpaper_Preview_Activity extends AppCompatActivity {

    ImageView btn_default_screen_close, default_wallpaper_set_preaview;
    TextView btn_default_wallpaper;
    String fetch_phone_number, default_wallpaper_URI;
    ProgressDialog progressDialog;
    Wallpaper_Model wallpaper_model;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_wallpaper_preview);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        btn_default_screen_close = findViewById(R.id.btn_default_screen_close);
        default_wallpaper_set_preaview = findViewById(R.id.default_wallpaper_set_preaview);
        btn_default_wallpaper = findViewById(R.id.btn_default_wallpaper);

        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat Wallpaper").child(fetch_phone_number);

        default_wallpaper_URI = "https://firebasestorage.googleapis.com/v0/b/say-hi-chat-application-7393e.appspot.com/o/Default%20Images%2Fdefault_wallpaper_img.jpeg?alt=media&token=3984b91e-d978-4690-80a5-da7ca662c288";

        try {
            Glide.with(default_wallpaper_set_preaview).load(default_wallpaper_URI).into(default_wallpaper_set_preaview);
        }catch (Exception e){
            e.printStackTrace();
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set Wallpaper");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please, wait for set wallpaper...");

        btn_default_screen_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_default_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();
                upload_wallpaper();
            }
        });
    }

    private void upload_wallpaper() {
        progressDialog.show();
        wallpaper_model = new Wallpaper_Model(auth.getUid(), default_wallpaper_URI);
        databaseReference.setValue(wallpaper_model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Wallpaper Set!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Default_Wallpaper_Preview_Activity.this, MainActivity.class));
                    finishAffinity();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Wallpaper not set!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
