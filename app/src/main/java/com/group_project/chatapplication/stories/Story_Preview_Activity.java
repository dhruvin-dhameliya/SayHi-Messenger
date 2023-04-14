package com.group_project.chatapplication.stories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

public class Story_Preview_Activity extends AppCompatActivity {

    Toolbar mTollbar;
    RelativeLayout final_story_img_layout;
    ImageView story_preview_img, btn_screen_close;
    CardView story_caption_card, edit_txt_caption_cardView;
    MaterialCardView btn_card_screen_close;
    TextView txt_story_caption;
    EditText edit_txt_caption;
    FloatingActionButton btn_upload_story;
    String fetch_caption, selected_img, fetch_phone_number;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;
    UserStories_Model userStories_model = new UserStories_Model();
    User_Model user_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_preview);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.black));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        fetch_phone_number = user.getPhoneNumber();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Story Uploading...");
        progressDialog.setCancelable(false);

        selected_img = getIntent().getExtras().get("pass_selected_img").toString();

        btn_screen_close = findViewById(R.id.btn_screen_close);
        btn_card_screen_close = findViewById(R.id.btn_card_screen_close);
        final_story_img_layout = findViewById(R.id.final_story_img_layout);
        story_preview_img = findViewById(R.id.story_preview_img);
        story_caption_card = findViewById(R.id.story_caption_card);
        edit_txt_caption_cardView = findViewById(R.id.edit_txt_caption_cardView);
        txt_story_caption = findViewById(R.id.txt_story_caption);
        edit_txt_caption = findViewById(R.id.edit_txt_caption);
        btn_upload_story = findViewById(R.id.btn_upload_story);

        btn_screen_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_card_screen_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            Glide.with(story_preview_img).load(selected_img).into(story_preview_img);
        }catch (Exception e){
            e.printStackTrace();
        }


        setSupportActionBar(mTollbar);

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

        // Close KeyBord when press DONE button of key-bord...
        edit_txt_caption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    fetch_caption = edit_txt_caption.getText().toString().trim();
                    if (fetch_caption.length() >= 75) {
                        edit_txt_caption.setError("Story caption maximum 75 character.");
                    } else {
                        txt_story_caption.setText(fetch_caption);

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        edit_txt_caption_cardView.setVisibility(View.GONE);
                        story_caption_card.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                return false;
            }
        });

        // Close KeyBord when touch on screen...
        edit_txt_caption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    fetch_caption = edit_txt_caption.getText().toString().trim();
                    if (fetch_caption.length() >= 75) {
                        edit_txt_caption.setError("Story caption maximum 75 character.");
                    } else {
                        txt_story_caption.setText(fetch_caption);

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        edit_txt_caption_cardView.setVisibility(View.GONE);
                        story_caption_card.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Show KeyBord when touch on Text-Story-Caption...
        story_caption_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_story_caption.setText(fetch_caption);
                story_caption_card.setVisibility(View.GONE);
                edit_txt_caption_cardView.setVisibility(View.VISIBLE);

                edit_txt_caption.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edit_txt_caption, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        btn_upload_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

    }

    private void saveImage() {
        fetch_caption = edit_txt_caption.getText().toString().trim();
        if (fetch_caption.length() >= 75) {
            edit_txt_caption.setError("Story caption maximum 75 character.");
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edit_txt_caption.getWindowToken(), 0);
            progressDialog.show();
            edit_txt_caption.setFocusable(false);
            // preserve layout as image
            final_story_img_layout.setDrawingCacheEnabled(true);
            final_story_img_layout.buildDrawingCache();
            final_story_img_layout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            // Bitmap convert into image URI
            Bitmap bitmapImg = final_story_img_layout.getDrawingCache();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmapImg, "SayHi Story-" + System.currentTimeMillis(), null);
            Uri myImgURI = Uri.parse(path);

            // Story upload on firebase Storage code....
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

                                Toast.makeText(getApplicationContext(), "Story Uploaded!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Story_Preview_Activity.this, MainActivity.class));
                                finishAffinity();
                            }
                        });
                    }
                }
            });
        }
    }

}