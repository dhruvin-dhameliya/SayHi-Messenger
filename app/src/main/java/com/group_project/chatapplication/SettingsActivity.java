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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    EditText edituser, editstatus;
    Button btnupdate;
    CircleImageView userimage;
    String currentid;
    FirebaseAuth auth;
    DatabaseReference reference;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    int Gallerypick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edituser = findViewById(R.id.user_name);
        editstatus = findViewById(R.id.profile_status);
        btnupdate = findViewById(R.id.update);
        userimage = findViewById(R.id.profile_image);
        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile");
        currentid = auth.getCurrentUser().getUid();


        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Update();
            }
        });
        Retriveuser();
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallerypick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallerypick && resultCode == RESULT_OK && data != null) {
            Uri imageuri = data.getData();
            progressDialog.setTitle("Upload");
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference filepath = storageReference.child(currentid + ".jpg");
            filepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();
                        String download = task.getResult().toString();
                        reference.child("Users").child(currentid).child("image")
                                .setValue(download)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Image save ", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void Retriveuser() {
        reference.child("Users").child(currentid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))) {
                    String retrivename = snapshot.child("name").getValue().toString();
                    String retrivestatus = snapshot.child("status").getValue().toString();
                    String retriveimage = snapshot.child("image").getValue().toString();

                    edituser.setText(retrivename);
                    editstatus.setText(retrivestatus);
//                    Picasso.get().load(retriveimage).into(userimage);       <<=======================================================
//                    Glide.with(userimage.getContext()).load(retriveimage).into(userimage);    <<=======================================================
                } else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                    String retrivename = snapshot.child("name").getValue().toString();
                    String retrivestatus = snapshot.child("status").getValue().toString();

                    edituser.setText(retrivename);
                    editstatus.setText(retrivestatus);
                } else {
                    // edituser.setVisibility(View.GONE);
                    Toast.makeText(SettingsActivity.this, "Please set & update your profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Update() {
        String setUsername = edituser.getText().toString();
        String setstatus = editstatus.getText().toString();
        if (TextUtils.isEmpty(setUsername)) {
            Toast.makeText(this, "Enter User name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setstatus)) {
            Toast.makeText(this, "Enter status", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profile = new HashMap<>();
            // profile.put("uid",currentid);
            profile.put("name", setUsername);
            profile.put("status", setstatus);
            reference.child("Users").child(currentid).setValue(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Profile Updated successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}