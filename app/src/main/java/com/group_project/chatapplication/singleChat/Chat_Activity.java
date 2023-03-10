package com.group_project.chatapplication.singleChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.registration.User_Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Chat_Activity extends AppCompatActivity {

    Toolbar mToolbar;
    ImageButton sendMessageButton;
    EditText userMessageInput;
    ImageView back_press, profile_img, attachbtn;
    TextView user_name;
    String currentContactName, myMobileNo, receiverMobileNo, getName, senderRoom, receiverRoom;
    RecyclerView chattingRecycleView;
    Chat_Adapter chatAd;
    User_Model user_model;
    Receiver_info_Model msg_model = new Receiver_info_Model();
    ArrayList<Chatmodel> chatModel = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Uri image_uri = null;
    Uri uri;
    String picturePath;
    int IMAGE_PICK_CAMERA_CODE = 300;
    int IMAGE_PICK_GALLERY_CODE = 400;
    int DOCUMENT_PICK_CODE = 500;
    long length;
    int file_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back_press = findViewById(R.id.back_to_screen);
        profile_img = findViewById(R.id.profile_img);
        user_name = findViewById(R.id.user_name);
        attachbtn = findViewById(R.id.send_file);
        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_message);

        currentContactName = getIntent().getExtras().get("pass_receiver_name").toString().trim();
        receiverMobileNo = getIntent().getExtras().get("pass_receiver_number").toString().replace(" ", "").replace("-", "").replace("+91", "");

        mToolbar = findViewById(R.id.chat_bar_layout);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        myMobileNo = Objects.requireNonNull(user.getPhoneNumber()).replace("+91", "");
        getName = user.getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat");

        chattingRecycleView = findViewById(R.id.chat_recyclearview);

        chatAd = new Chat_Adapter(chatModel, this);
        chattingRecycleView.setAdapter(chatAd);

        senderRoom = myMobileNo + receiverMobileNo;
        receiverRoom = receiverMobileNo + myMobileNo;

        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        attachbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        check_number_exist_or_not();
        loadChatInfo();

        do_chat_messages();
        display_chat_messages();
    }

    //send message
    public void do_chat_messages() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTxtMessage = userMessageInput.getText().toString().trim();
                final Chatmodel chatmodel = new Chatmodel(myMobileNo, getTxtMessage, "text");

                if (TextUtils.isEmpty(getTxtMessage)) {
                    Toast.makeText(getApplicationContext(), "Can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    String currentTime = String.valueOf(new Date().getTime());
                    chatmodel.setTimestamp(currentTime);
                    databaseReference.child(myMobileNo)
                            .child(senderRoom)
                            .child("Messages")
                            .child(currentTime)
                            .setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference.child(receiverMobileNo)
                                            .child(receiverRoom)
                                            .child("Messages")
                                            .child(currentTime)
                                            .setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                }
                            });
                    userMessageInput.setText("");
                }
            }
        });

    }

    // display user messages
    public void display_chat_messages() {
        databaseReference.child(myMobileNo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatModel.clear();
                        for (DataSnapshot snapshot1 : snapshot.child(senderRoom).child("Messages").getChildren()) {
                            Chatmodel chatmodel = snapshot1.getValue(Chatmodel.class);
                            chatModel.add(chatmodel);
                        }
                        chatAd.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    // check user can use this app or not
    public void check_number_exist_or_not() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users Details").child("+91" + receiverMobileNo);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // fetch values from User Details table

                    // Upload receiver details in our room id
                    firebaseDatabase.getReference().child("Users Details").child("+91" + receiverMobileNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user_model = snapshot.getValue(User_Model.class);
                            msg_model.setReceiverNo(user_model.getPhone());
                            msg_model.setReceiverName(user_model.getName());
                            msg_model.setReceiverProfileImg(user_model.getProfile_image());
                            msg_model.setReceiverInfo(user_model.getAbout());
                            msg_model.setRoomId(senderRoom);

                            HashMap<String, Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("receiver_no", msg_model.getReceiverNo());
                            objectsHashMap.put("receiver_name", msg_model.getReceiverName());
                            objectsHashMap.put("receiver_profileImage", msg_model.getReceiverProfileImg());
                            objectsHashMap.put("receiver_info", msg_model.getReceiverInfo());
                            objectsHashMap.put("room_id", msg_model.getRoomId());

                            firebaseDatabase.getReference().child("Chat").child(myMobileNo).child(senderRoom).updateChildren(objectsHashMap);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // Upload sender details in receiver room id
                    firebaseDatabase.getReference().child("Users Details").child("+91" + myMobileNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user_model = snapshot.getValue(User_Model.class);
                            msg_model.setReceiverNo(user_model.getPhone());
                            msg_model.setReceiverName(user_model.getName());
                            msg_model.setReceiverProfileImg(user_model.getProfile_image());
                            msg_model.setReceiverInfo(user_model.getAbout());
                            msg_model.setRoomId(receiverRoom);

                            HashMap<String, Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("receiver_no", msg_model.getReceiverNo());
                            objectsHashMap.put("receiver_name", msg_model.getReceiverName());
                            objectsHashMap.put("receiver_profileImage", msg_model.getReceiverProfileImg());
                            objectsHashMap.put("receiver_info", msg_model.getReceiverInfo());
                            objectsHashMap.put("room_id", msg_model.getRoomId());
                            firebaseDatabase.getReference().child("Chat").child(receiverMobileNo).child(receiverRoom).updateChildren(objectsHashMap);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Not Exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadChatInfo() {
        databaseReference.child(myMobileNo).child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        String title = "" + ds.child("receiver_name").getValue();
                        String profileImage = "" + ds.child("receiver_profileImage").getValue();
                        String mobile = "" + ds.child("receiver_no").getValue();
                        String about = "" + ds.child("receiver_info").getValue();

                        user_name.setText(title);
                        try {
                            Glide.with(profile_img).load(profileImage).into(profile_img);
                        } catch (Exception e) {
                            profile_img.setImageResource(R.drawable.img_default_person);
                        }

                        user_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), Single_Chat_Info_Activity.class);
                                intent.putExtra("title", title);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("profileImage", profileImage);
                                intent.putExtra("about", about);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView gallery = dialog.findViewById(R.id.gallery);
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
        startActivityForResult(Intent.createChooser(intent1, "Select Document"), DOCUMENT_PICK_CODE);
    }

    public void pickfromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    public void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Image Icon Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void sendImagemessage() {
        //progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Sending Image..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //file path
        String filepath = "Single User Messages/" + myMobileNo + "/" + "Images/" + System.currentTimeMillis() + ".jpg";
        //upload
        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference(filepath);
        storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> p_uriTasl = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTasl.isSuccessful()) ;
                Uri p_downloadUri = p_uriTasl.getResult();
                if (p_uriTasl.isSuccessful()) {
                    String timestamp = "" + System.currentTimeMillis();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", "" + myMobileNo);
                    hashMap.put("message", "" + p_downloadUri);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("type", "" + "image");//text,image,file

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
                    reference.child(myMobileNo)
                            .child(senderRoom)
                            .child("Messages")
                            .child(timestamp)
                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    reference.child(receiverMobileNo)
                                            .child(receiverRoom)
                                            .child("Messages")
                                            .child(timestamp)
                                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });
                                    userMessageInput.setText("");
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        String filepath = "Single User Messages/" + myMobileNo + "/" + "Documents/" + System.currentTimeMillis();
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

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", "" + myMobileNo);
                    hashMap.put("message", "" + p_downloadUri);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("type", "" + "file");//text,image,file

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
                    reference.child(myMobileNo)
                            .child(senderRoom)
                            .child("Messages")
                            .child(timestamp)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //send
                                    //clear
                                    reference.child(receiverMobileNo)
                                            .child(receiverRoom)
                                            .child("Messages")
                                            .child(timestamp)
                                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });

                                    userMessageInput.setText("");
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Document sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to send", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to send", Toast.LENGTH_SHORT).show();
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