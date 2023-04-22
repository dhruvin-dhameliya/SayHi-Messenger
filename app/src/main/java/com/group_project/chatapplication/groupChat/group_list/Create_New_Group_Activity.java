package com.group_project.chatapplication.groupChat.group_list;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.registration.User_Model;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Create_New_Group_Activity extends AppCompatActivity {

    ActionBar actionBar;
    FirebaseAuth auth;
    CircleImageView groupimage;
    EditText editname, editdesc;
    MaterialButton btncreate;
    int IMAGE_PICK_CAMERA_CODE = 300;
    int IMAGE_PICK_GALLERY_CODE = 400;
    String[] cameraPermission;
    String[] storagePermission;
    Uri image_uri = null;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    String fetch_phone_number;
    User_Model user_model;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        toolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Create Group");

        groupimage = findViewById(R.id.groupimage);
        editname = findViewById(R.id.group_name);
        editdesc = findViewById(R.id.group_desc);
        btncreate = findViewById(R.id.btn_group);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();

        checkUser();

        groupimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickDialog();
            }
        });

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatGroup();
            }
        });

    }

    private void creatGroup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please, wait for creating a group...");
        progressDialog.setCancelable(false);

        String grouptitle = editname.getText().toString().trim();
        String groupDescription = editdesc.getText().toString().trim();
        if (TextUtils.isEmpty(grouptitle)) {
            editname.setError("Please enter group title.");
            return;
        }
        if (TextUtils.isEmpty(groupDescription)) {
            editdesc.setError("Please enter group description.");
            return;
        }
        progressDialog.show();
        String g_timestamp = "" + System.currentTimeMillis();
        if (image_uri == null) {
            //without image
            String default_image_url = "https://firebasestorage.googleapis.com/v0/b/say-hi-chat-application-7393e.appspot.com/o/Default%20Images%2Fdefault_group_img.png?alt=media&token=37ed7780-4ea7-459d-aa9b-9e42a3e6b391";
            createGroup1("" + g_timestamp, "" + grouptitle, "" + groupDescription, default_image_url);
        } else {
            //with image
            String fileNameandPath = "Group Profile Images/" + "image" + g_timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameandPath);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!p_uriTask.isSuccessful()) ;
                    Uri p_downloaduri = p_uriTask.getResult();
                    if (p_uriTask.isSuccessful()) {
                        createGroup1("" + g_timestamp, "" + grouptitle, "" + groupDescription, "" + p_downloaduri);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Create_New_Group_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createGroup1(String g_timestamp, String grouptitle, String groupDescription, String groupIcon) {
        //setup
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", "" + g_timestamp);
        hashMap.put("groupTitle", "" + grouptitle);
        hashMap.put("groupDescription", "" + groupDescription);
        hashMap.put("groupIcon", "" + groupIcon);
        hashMap.put("timestamp", "" + g_timestamp);
        hashMap.put("createBy", "" + fetch_phone_number);

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users Details");
        firebaseDatabase.child(fetch_phone_number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_model = snapshot.getValue(User_Model.class);
                name = user_model.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //create
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(g_timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("uid", fetch_phone_number);
                hashMap1.put("post", "creator");
                hashMap1.put("timestamp", g_timestamp);
                hashMap1.put("name", name);
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                reference1.child(g_timestamp).child("Participants").child(fetch_phone_number).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(Create_New_Group_Activity.this, "Group is created successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Create_New_Group_Activity.this, MainActivity.class));
                        finishAffinity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Create_New_Group_Activity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Create_New_Group_Activity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //camera
                    pickFromCamera();
                } else {
                    //gallery
                    pickfromGallery();
                }
            }
        }).show();
    }

    public void pickfromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    public void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }


    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            actionBar.setSubtitle(user.getPhoneNumber());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                groupimage.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                groupimage.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}