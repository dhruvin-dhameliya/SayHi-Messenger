package com.group_project.chatapplication.groupChat.group_chat_messages;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.groupChat.group_chat_messages.Adapter_Group_Chat_Messages;
import com.group_project.chatapplication.groupChat.group_chat_messages.Group_Info_Activity;
import com.group_project.chatapplication.groupChat.group_chat_messages.Model_Group_Chat_Messages;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;

public class Group_Chat_Messages_Activity extends AppCompatActivity {

    String groupId, fetch_phone_number, mypost = "";
    ImageView groupIconTv, Back_press, img_group_chat_wallpaper;
    TextView groupTitleTv;
    ImageButton attachbtn;
    CardView senbtn;
    EditText messageET;
    FirebaseAuth firebaseAuth;
    RecyclerView chatRv;
    ArrayList<Model_Group_Chat_Messages> groupChatArrayList;
    Adapter_Group_Chat_Messages adapterGroupChat;
    Uri image_uri = null, video_uri = null;
    Uri uri;
    String picturePath;
    int IMAGE_PICK_CAMERA_CODE = 300;
    int IMAGE_PICK_GALLERY_CODE = 400;
    int DOCUMENT_PICK_CODE = 500;
    int VIDEO_PICK_GALLERY_CODE = 102;
    long length;
    int file_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_messages);

        groupIconTv = findViewById(R.id.groupIconTV);
        groupTitleTv = findViewById(R.id.grouptitle);
        attachbtn = findViewById(R.id.attachbtn);
        senbtn = findViewById(R.id.sendbtn);
        messageET = findViewById(R.id.messageEt);
        chatRv = findViewById(R.id.chatRv);
        Back_press = findViewById(R.id.back_press);
        img_group_chat_wallpaper = findViewById(R.id.img_group_chat_wallpaper);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();

        // Wallpaper display code...
        DatabaseReference dbwallpaper = FirebaseDatabase.getInstance().getReference().child("Chat Wallpaper").child(fetch_phone_number).child("wallpaper_image");
        dbwallpaper.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists())) {
                    String retrieveWallpaperImage = snapshot.getValue(String.class);
                    try {
                        Glide.with(img_group_chat_wallpaper).load(retrieveWallpaperImage).into(img_group_chat_wallpaper);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Wallpaper not set!", Toast.LENGTH_SHORT).show();
            }
        });

        loadgroupInfo();
        loadgroupmessage();
        loadGroupPost();

        senbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageET.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "Can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    byte[] data = message.getBytes(StandardCharsets.UTF_8);
                    String encode_txt_msg = Base64.encodeToString(data, Base64.DEFAULT);
                    sendmessage(encode_txt_msg);
                    messageET.setText("");
                }
            }
        });

        groupTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Group_Chat_Messages_Activity.this, Group_Info_Activity.class);
                intent1.putExtra("groupId", groupId);
                startActivity(intent1);
            }
        });

        attachbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        Back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView gallery = dialog.findViewById(R.id.gallery);
        ImageView video = dialog.findViewById(R.id.video);
        ImageView pdf = dialog.findViewById(R.id.pdf);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickFromCamera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickfromGallery();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickfromVideos();
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickfromDocument();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void pickfromDocument() {

        Intent intent1 = new Intent();
        intent1.setType("application/*");
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent1, "Select PDF File"), DOCUMENT_PICK_CODE);
    }


    public void pickfromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
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

    public void pickfromVideos() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK_GALLERY_CODE);
    }

    private void sendImagemessage() {
        //progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Sending Image..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //file path
        String filepath = "Group Messages/" + fetch_phone_number + "/" + "Images/" + System.currentTimeMillis() + ".jpg";
        //upload
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepath);
        storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> p_uriTasl = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTasl.isSuccessful()) ;
                Uri p_downloadUri = p_uriTasl.getResult();
                if (p_uriTasl.isSuccessful()) {
                    String timestamp = "" + System.currentTimeMillis();
                    String img_uri_normal = p_downloadUri.toString();
                    byte[] data = img_uri_normal.getBytes(StandardCharsets.UTF_8);
                    String encode_img_msg = Base64.encodeToString(data, Base64.DEFAULT);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", "" + firebaseAuth.getUid());
                    hashMap.put("message", "" + encode_img_msg);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("type", "" + "image");//text,image,file

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(groupId).child("Messages").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //send
                            //clear
                            messageET.setText("");
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }//the end

    private void loadGroupPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    mypost = "" + ds.child("post").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadgroupmessage() {
        groupChatArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Model_Group_Chat_Messages modelGroupChat = ds.getValue(Model_Group_Chat_Messages.class);
                    groupChatArrayList.add(modelGroupChat);
                }
                //adapter
                adapterGroupChat = new Adapter_Group_Chat_Messages(Group_Chat_Messages_Activity.this, groupChatArrayList, groupId);
                //recycleview
                chatRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendmessage(String message) {
        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", "" + firebaseAuth.getUid());
        hashMap.put("message", "" + message);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("type", "" + "text");//text,image,file

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Messages").child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //send
                //clear
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadgroupInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupTitle = "" + ds.child("groupTitle").getValue();
                    String groupIcon = "" + ds.child("groupIcon").getValue();

                    groupTitleTv.setText(groupTitle);
                    try {
                        Glide.with(groupIconTv).load(groupIcon).into(groupIconTv);
                    } catch (Exception e) {
                        groupIconTv.setImageResource(R.drawable.img_default_person);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();

                String[] filepathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(image_uri, filepathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filepathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                File img = new File(picturePath);
                length = img.length();
                file_size = Integer.parseInt(String.valueOf(length / 1024));
                if (file_size < 5000) {
                    sendImagemessage();
                } else {
                    Toast.makeText(this, "greater", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                sendImagemessage();
            } else if (requestCode == 200) {
                if (data != null) {
                    image_uri = data.getData();
                    sendImagemessage();
                }
            } else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                video_uri = data.getData();

                String[] filepathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(video_uri, filepathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filepathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                File video_len = new File(picturePath);
                length = video_len.length();
                file_size = Integer.parseInt(String.valueOf(length / 1024));
                if (file_size < 15000) {
                    sendVideoMessage();
                } else {
                    Toast.makeText(this, "Video is Greater!", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == DOCUMENT_PICK_CODE) {
                uri = data.getData();
                sendDocument();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendDocument() {
        //progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        // progressDialog.setMessage("Sending Image..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //file path
        String filepath = "Group Messages/" + fetch_phone_number + "/" + "Documents/" + System.currentTimeMillis();
        //upload
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepath);
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> p_uriTasl = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTasl.isSuccessful()) ;
                Uri p_downloadUri = p_uriTasl.getResult();
                if (p_uriTasl.isSuccessful()) {
                    String timestamp = "" + System.currentTimeMillis();
                    String doc_uri_normal = p_downloadUri.toString();
                    byte[] data = doc_uri_normal.getBytes(StandardCharsets.UTF_8);
                    String encode_doc_msg = Base64.encodeToString(data, Base64.DEFAULT);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", "" + firebaseAuth.getUid());
                    hashMap.put("message", "" + encode_doc_msg);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("type", "" + "file");//text,image,file

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(groupId).child("Messages").child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //send
                                    //clear
                                    messageET.setText("");
                                    progressDialog.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage((int) p + "% Uploading...");
            }
        });
    }

    private void sendVideoMessage() {
        //progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        // progressDialog.setMessage("Sending Image..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //file path
        String filepath = "Group Messages/" + fetch_phone_number + "/" + "Videos/" + System.currentTimeMillis() + ".mp4";
        //upload
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepath);
        storageReference.putFile(video_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> p_uriTasl = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTasl.isSuccessful()) ;
                Uri p_downloadUri = p_uriTasl.getResult();
                if (p_uriTasl.isSuccessful()) {
                    String timestamp = "" + System.currentTimeMillis();
                    String doc_uri_normal = p_downloadUri.toString();
                    byte[] data = doc_uri_normal.getBytes(StandardCharsets.UTF_8);
                    String encode_doc_msg = Base64.encodeToString(data, Base64.DEFAULT);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", "" + firebaseAuth.getUid());
                    hashMap.put("message", "" + encode_doc_msg);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("type", "" + "video");//text,image,file

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(groupId).child("Messages").child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //send
                                    //clear
                                    messageET.setText("");
                                    progressDialog.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Group_Chat_Messages_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage((int) p + "% Uploading...");
            }
        });
    }

}