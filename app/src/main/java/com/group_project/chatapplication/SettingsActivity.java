package com.group_project.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    CircleImageView display_profile_img, update_profile_img;
    EditText profile_user_name, profile_user_about;
    TextView profile_user_phone;
    MaterialButton btn_profile_update;
    String currentLoginUserId, name, phoneNumber, about, imageUri;
    Uri updateImageUri;
    ProgressDialog progressDialog;
    User_Model usersModel;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Profile Updating...");
        progressDialog.setCancelable(false);

        display_profile_img = findViewById(R.id.display_profile_img);
        update_profile_img = findViewById(R.id.update_profile_img);
        profile_user_name = findViewById(R.id.profile_user_name);
        profile_user_about = findViewById(R.id.profile_user_about);
        profile_user_phone = findViewById(R.id.profile_user_phone);
        btn_profile_update = findViewById(R.id.btn_profile_update);

        currentLoginUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        phoneNumber = user.getPhoneNumber();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Details").child(phoneNumber);
        StorageReference storageReference = firebaseStorage.getReference().child("Users Profile Image").child(Objects.requireNonNull(phoneNumber));

        update_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("phone")) {
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    about = Objects.requireNonNull(snapshot.child("about").getValue()).toString();
                    phoneNumber = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                    imageUri = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();

                    profile_user_name.setText(name);
                    profile_user_about.setText(about);
                    profile_user_phone.setText(phoneNumber);
                    Glide.with(display_profile_img.getContext()).load(imageUri).into(display_profile_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database error...", Toast.LENGTH_SHORT).show();
            }
        });

        btn_profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileName = profile_user_name.getText().toString().trim();
                String profileAbout = profile_user_about.getText().toString().trim();

                if (TextUtils.isEmpty(profileName)) {
                    profile_user_name.setError("Please enter your name.");
                } else {
                    progressDialog.show();
                    if (updateImageUri != null) {
                        storageReference.putFile(updateImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String finalUpdate_ImageUri = uri.toString();
                                        usersModel = new User_Model(auth.getUid(), profileName, phoneNumber, profileAbout, finalUpdate_ImageUri);
                                        databaseReference.setValue(usersModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Profile Update.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String finalUpdate_ImageUri = uri.toString();
                                usersModel = new User_Model(auth.getUid(), profileName, phoneNumber, profileAbout, finalUpdate_ImageUri);
                                databaseReference.setValue(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Profile Update.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (data != null) {
                updateImageUri = data.getData();
                display_profile_img.setImageURI(updateImageUri);
            }
        }
    }

}