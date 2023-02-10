package com.group_project.chatapplication.All_Activities;

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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.group_project.chatapplication.Model_Class.Wallpaper_Model;
import com.group_project.chatapplication.R;

import java.util.Objects;

public class WallPaperChatActivity extends AppCompatActivity {

    ImageView img_choose_wallpaper;
    Button btn_set_wallpaper;
    ActivityResultLauncher<String> mGetContent;
    Uri uri;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String fetch_phone_number, wallpaper;
    FirebaseStorage firebaseStorage;
    ProgressDialog progressDialog;

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
                Intent intent = new Intent(WallPaperChatActivity.this, CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);

            }
        });

        btn_set_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                uploadimage();
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Chat Wallpaper").child(fetch_phone_number);
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
                Toast.makeText(WallPaperChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadimage() {
        StorageReference storageReference = firebaseStorage.getReference().child("Wallpaper Image").child(Objects.requireNonNull(fetch_phone_number));
        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            wallpaper = uri.toString();
                            Wallpaper_Model wallModel = new Wallpaper_Model(auth.getUid(), wallpaper);
                            databaseReference.setValue(wallModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(WallPaperChatActivity.this, MainActivity.class));
                                        finish();
                                        Toast.makeText(WallPaperChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(WallPaperChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(WallPaperChatActivity.this, "નિષ્ફળ", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WallPaperChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 200) {
//            if (data != null) {
//                uri = data.getData();
//                btnmage.setImageURI(uri);
//            }
//        }
        if (resultCode == -1 && requestCode == 101) {
            String result = data.getStringExtra("RESULT");
            uri = null;
            if (result != null) {
                uri = Uri.parse(result);
            }
            img_choose_wallpaper.setImageURI(uri);
        }
    }

}