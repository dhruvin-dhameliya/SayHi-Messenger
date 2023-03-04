package com.group_project.chatapplication.wallPaper;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.Objects;

public class Wallpaper_Chat_Activity extends AppCompatActivity {

    ImageView img_choose_wallpaper;
    Button btn_set_wallpaper;
    ActivityResultLauncher<String> mGetContent;
    Uri uri;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String fetch_phone_number, wallpaper;
    FirebaseStorage firebaseStorage;
    ProgressDialog progressDialog;
    Wallpaper_Model wallpaper_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_paper_chat);

        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();

        img_choose_wallpaper = findViewById(R.id.img_choose_wallpaper);
        btn_set_wallpaper = findViewById(R.id.btn_set_wallpaper);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set Wallpaper");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please, wait for set wallpaper...");

        img_choose_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(Wallpaper_Chat_Activity.this, Cropper_Activity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);

            }
        });

        btn_set_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                uploadWallpaper();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat Wallpaper").child(fetch_phone_number);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())) {
                    String retriveimage = snapshot.child("wallpaper_image").getValue().toString();
                    Glide.with(img_choose_wallpaper.getContext()).load(retriveimage).into(img_choose_wallpaper);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Wallpaper_Chat_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 101) {
            String result = data.getStringExtra("RESULT");
            uri = null;
            if (result != null) {
                uri = Uri.parse(result);
            }
            img_choose_wallpaper.setImageURI(uri);
        }
    }

    private void uploadWallpaper() {
        StorageReference storageReference = firebaseStorage.getReference().child("Wallpaper Image").child(Objects.requireNonNull(fetch_phone_number));
        progressDialog.show();
        if (uri == null) {
            String finalWallpaper_ImageUri = "https://firebasestorage.googleapis.com/v0/b/say-hi-chat-app.appspot.com/o/default_wallpaper_img.jpeg?alt=media&token=b6d3532b-ca12-4488-a18c-5a61b3540d63";
            wallpaper_model = new Wallpaper_Model(auth.getUid(), finalWallpaper_ImageUri);
            databaseReference.setValue(wallpaper_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Wallpaper Set!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Wallpaper_Chat_Activity.this, MainActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Wallpaper not set!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if (uri != null) {
                storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String finalWallpaper_ImageUri = uri.toString();
                                wallpaper_model = new Wallpaper_Model(auth.getUid(), finalWallpaper_ImageUri);
                                databaseReference.setValue(wallpaper_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Wallpaper is Set.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Wallpaper_Chat_Activity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Wallpaper not set!", Toast.LENGTH_SHORT).show();
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
                        String finalWallpaper_ImageUri = uri.toString();
                        wallpaper_model = new Wallpaper_Model(auth.getUid(), finalWallpaper_ImageUri);
                        databaseReference.setValue(wallpaper_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Wallpaper set.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Wallpaper_Chat_Activity.this, MainActivity.class));
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Wallpaper not set.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

}