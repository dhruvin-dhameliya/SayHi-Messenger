package com.group_project.chatapplication.stories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.registration.User_Model;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

public class Text_Story_Activity extends AppCompatActivity {

    TextView btn_choose_font_family;
    MaterialButton btn_choose_color;
    EditText write_story_txt;
    LinearLayout txt_story_layout;
    FloatingActionButton btn_upload_txt_story;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    String fetch_phone_number;
    ProgressDialog progressDialog;
    UserStories_Model userStories_model = new UserStories_Model();
    User_Model user_model;
    int clickBtnCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_story);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.myGreenColor));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Story Uploading...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        fetch_phone_number = user.getPhoneNumber();

        write_story_txt = findViewById(R.id.write_story_txt);
        btn_upload_txt_story = findViewById(R.id.btn_upload_txt_story);
        txt_story_layout = findViewById(R.id.txt_story_layout);
        btn_choose_color = findViewById(R.id.btn_choose_color);
        btn_choose_font_family = findViewById(R.id.btn_choose_font_family);

        btn_choose_font_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnCount += 1;
                if (clickBtnCount > 11) {
                    clickBtnCount = 1;
                }
                switch (clickBtnCount) {
                    case 1:
                        Typeface font1 = Typeface.createFromAsset(getAssets(), "f1_roboto_mono.ttf");
                        btn_choose_font_family.setTypeface(font1);
                        write_story_txt.setTypeface(font1);
                        break;

                    case 2:
                        Typeface font2 = Typeface.createFromAsset(getAssets(), "f2_playfair_display.ttf");
                        btn_choose_font_family.setTypeface(font2);
                        write_story_txt.setTypeface(font2);
                        break;

                    case 3:
                        Typeface font3 = Typeface.createFromAsset(getAssets(), "f3_comfortaa.ttf");
                        btn_choose_font_family.setTypeface(font3);
                        write_story_txt.setTypeface(font3);
                        break;

                    case 4:
                        Typeface font4 = Typeface.createFromAsset(getAssets(), "f4_aclonica.ttf");
                        btn_choose_font_family.setTypeface(font4);
                        write_story_txt.setTypeface(font4);
                        break;

                    case 5:
                        Typeface font5 = Typeface.createFromAsset(getAssets(), "f5_fuzzy_bubbles.ttf");
                        btn_choose_font_family.setTypeface(font5);
                        write_story_txt.setTypeface(font5);
                        break;

                    case 6:
                        Typeface font6 = Typeface.createFromAsset(getAssets(), "f6_sofia.ttf");
                        btn_choose_font_family.setTypeface(font6);
                        write_story_txt.setTypeface(font6);
                        break;

                    case 7:
                        Typeface font7 = Typeface.createFromAsset(getAssets(), "f7_aboreto.ttf");
                        btn_choose_font_family.setTypeface(font7);
                        write_story_txt.setTypeface(font7);
                        break;

                    case 8:
                        Typeface font8 = Typeface.createFromAsset(getAssets(), "f8_dancing_script.ttf");
                        btn_choose_font_family.setTypeface(font8);
                        write_story_txt.setTypeface(font8);
                        break;

                    case 9:
                        Typeface font9 = Typeface.createFromAsset(getAssets(), "f9_audiowide.ttf");
                        btn_choose_font_family.setTypeface(font9);
                        write_story_txt.setTypeface(font9);
                        break;

                    case 10:
                        Typeface font10 = Typeface.createFromAsset(getAssets(), "f10_limelight.ttf");
                        btn_choose_font_family.setTypeface(font10);
                        write_story_txt.setTypeface(font10);
                        break;

                    case 11:
                        Typeface font11 = Typeface.createFromAsset(getAssets(), "f11_amita.ttf");
                        btn_choose_font_family.setTypeface(font11);
                        write_story_txt.setTypeface(font11);
                        break;
                }
            }
        });

        btn_choose_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnCount += 1;
                if (clickBtnCount > 12) {
                    clickBtnCount = 1;
                }
                switch (clickBtnCount) {
                    case 1:
                        Window window1 = getWindow();
                        window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window1.setStatusBarColor(getResources().getColor(R.color.c1));
                        txt_story_layout.setBackgroundResource(R.color.c1);
                        break;
                    case 2:
                        Window window2 = getWindow();
                        window2.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window2.setStatusBarColor(getResources().getColor(R.color.c2));
                        txt_story_layout.setBackgroundResource(R.color.c2);
                        break;

                    case 3:
                        Window window3 = getWindow();
                        window3.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window3.setStatusBarColor(getResources().getColor(R.color.c3));
                        txt_story_layout.setBackgroundResource(R.color.c3);
                        break;

                    case 4:
                        Window window4 = getWindow();
                        window4.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window4.setStatusBarColor(getResources().getColor(R.color.c4));
                        txt_story_layout.setBackgroundResource(R.color.c4);
                        break;

                    case 5:
                        Window window5 = getWindow();
                        window5.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window5.setStatusBarColor(getResources().getColor(R.color.c5));
                        txt_story_layout.setBackgroundResource(R.color.c5);
                        break;

                    case 6:
                        Window window6 = getWindow();
                        window6.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window6.setStatusBarColor(getResources().getColor(R.color.c6));
                        txt_story_layout.setBackgroundResource(R.color.c6);
                        break;

                    case 7:
                        Window window7 = getWindow();
                        window7.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window7.setStatusBarColor(getResources().getColor(R.color.c7));
                        txt_story_layout.setBackgroundResource(R.color.c7);
                        break;

                    case 8:
                        Window window8 = getWindow();
                        window8.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window8.setStatusBarColor(getResources().getColor(R.color.c8));
                        txt_story_layout.setBackgroundResource(R.color.c8);
                        break;

                    case 9:
                        Window window9 = getWindow();
                        window9.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window9.setStatusBarColor(getResources().getColor(R.color.c9));
                        txt_story_layout.setBackgroundResource(R.color.c9);
                        break;

                    case 10:
                        Window window10 = getWindow();
                        window10.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window10.setStatusBarColor(getResources().getColor(R.color.c10));
                        txt_story_layout.setBackgroundResource(R.color.c10);
                        break;

                    case 11:
                        Window window11 = getWindow();
                        window11.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window11.setStatusBarColor(getResources().getColor(R.color.c11));
                        txt_story_layout.setBackgroundResource(R.color.c11);
                        break;

                    case 12:
                        Window window12 = getWindow();
                        window12.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window12.setStatusBarColor(getResources().getColor(R.color.c12));
                        txt_story_layout.setBackgroundResource(R.color.c12);
                        break;
                }
            }
        });

        // fetch values from User Details table
        firebaseDatabase.getReference().child("Users Details").child(fetch_phone_number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_model = snapshot.getValue(User_Model.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_upload_txt_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

    }

    private void saveImage() {
        String storyTxtMsg = write_story_txt.getText().toString().trim();
        if (TextUtils.isEmpty(storyTxtMsg)) {
            Toast.makeText(getApplicationContext(), "Type a status!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            write_story_txt.setFocusable(false);
            // preserve layout as image
            txt_story_layout.setDrawingCacheEnabled(true);
            txt_story_layout.buildDrawingCache();
            txt_story_layout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            // Bitmap convert into image URI
            Bitmap bitmapImg = txt_story_layout.getDrawingCache();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmapImg, "picture", null);
            Uri myImgURI = Uri.parse(path);

            // Text-Story upload on firebase Storage code....
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date date = new Date();
            StorageReference reference = storage.getReference().child("Stories").child(fetch_phone_number).child(date.getTime() + "");
            reference.putFile(myImgURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                long d1 = date.getTime();
                                userStories_model.setName(user_model.getName());
                                userStories_model.setProfileImage(user_model.getProfile_image());
                                userStories_model.setLastupdated(d1);
                                HashMap<String, Object> objectsHashMap = new HashMap<>();
                                objectsHashMap.put("name", userStories_model.getName());
                                objectsHashMap.put("profileImage", userStories_model.getProfileImage());
                                objectsHashMap.put("lastUpdate", userStories_model.getLastupdated());
                                String imgURL = uri.toString();
                                byte[] data = imgURL.getBytes(StandardCharsets.UTF_8);
                                String encode_story = Base64.encodeToString(data, Base64.DEFAULT);
                                Stories_Model stories_model = new Stories_Model(encode_story, userStories_model.getLastupdated());
                                firebaseDatabase.getReference().child("Stories").child(fetch_phone_number).updateChildren(objectsHashMap);
                                firebaseDatabase.getReference().child("Stories").child(fetch_phone_number).child("Status").child(String.valueOf(d1)).setValue(stories_model);
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(), "Story Uploaded!.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Text_Story_Activity.this, MainActivity.class));
                                finishAffinity();
                            }
                        });
                    }
                }
            });
        }
    }
}