package com.group_project.chatapplication.wallPaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.userSettings.Profile_Activity;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class Cropper_Preview_Wallpaper_Activity extends AppCompatActivity {

    String result;
    Uri fileuri, resultUri;
    ImageView wallpaper_set_preaview, back_arrow;
    TextView btn_set_wallpaper;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    String fetch_phone_number;
    Wallpaper_Model wallpaper_model;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper_preview_wallpaper);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        wallpaper_set_preaview = findViewById(R.id.wallpaper_set_preaview);
        btn_set_wallpaper = findViewById(R.id.btn_set_wallpaper);
        back_arrow = findViewById(R.id.btn_screen_close);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set Wallpaper");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please, wait for set wallpaper...");

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat Wallpaper").child(fetch_phone_number);

        readIntent();

        String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        UCrop.Options options = new UCrop.Options();

        UCrop.of(fileuri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withAspectRatio(0, 0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000, 2000)
                .start(Cropper_Preview_Wallpaper_Activity.this);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btn_set_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();
                uploadWallpaper();
            }
        });
    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            result = intent.getStringExtra("DATA");
            fileuri = Uri.parse(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            assert data != null;
            resultUri = UCrop.getOutput(data);
            wallpaper_set_preaview.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            startActivity(new Intent(Cropper_Preview_Wallpaper_Activity.this, Profile_Activity.class));
            finish();
        } else if (data == null) {
            onBackPressed();
            finish();
        }
    }

    private void uploadWallpaper() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Wallpaper Image").child(Objects.requireNonNull(fetch_phone_number));
        progressDialog.show();
        if (resultUri == null) {
            String finalWallpaper_ImageUri = "https://firebasestorage.googleapis.com/v0/b/say-hi-chat-app.appspot.com/o/default_wallpaper_img.jpeg?alt=media&token=b6d3532b-ca12-4488-a18c-5a61b3540d63";
            wallpaper_model = new Wallpaper_Model(auth.getUid(), finalWallpaper_ImageUri);

            databaseReference.setValue(wallpaper_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Wallpaper Set!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Cropper_Preview_Wallpaper_Activity.this, MainActivity.class));
                        finishAffinity();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Wallpaper not set!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if (resultUri != null) {
                storageReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                            startActivity(new Intent(Cropper_Preview_Wallpaper_Activity.this, MainActivity.class));
                                            finishAffinity();
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
                                    startActivity(new Intent(Cropper_Preview_Wallpaper_Activity.this, MainActivity.class));
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