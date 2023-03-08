package com.group_project.chatapplication.singleChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.registration.User_Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Chat_Activity extends AppCompatActivity {

    Toolbar mToolbar;
    ImageButton sendMessageButton;
    EditText userMessageInput;
    ImageView back_press, profile_img;
    TextView user_name, user_online_or_not_txt;
    String currentContactName, myMobileNo, receiverMobileNo, getName, senderRoom, receiverRoom;
    RecyclerView chattingRecycleView;
    Chat_Adapter chatAd;
    User_Model user_model;
    Receiver_info_Model msg_model = new Receiver_info_Model();
    ArrayList<Chatmodel> chatModel = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back_press = findViewById(R.id.back_press);
        profile_img = findViewById(R.id.profile_img);
        user_name = findViewById(R.id.user_name);
        user_online_or_not_txt = findViewById(R.id.user_online_or_not_txt);
        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);

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

        //for typing on edit text
        userMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    checkTypingStatus("noOne");
                } else {
                    checkTypingStatus(myMobileNo);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        check_number_exist_or_not();
        loadChatInfo();

        do_chat_messages();
        display_chat_messages();
    }

    //Checking user is online & typing
    public void checkOnlineStatus(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Details").child("+91" + myMobileNo);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        reference.updateChildren(hashMap);
    }

    public void checkTypingStatus(String typing) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Details").child("+91" + myMobileNo);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingStatus", typing);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
        user_online_or_not_txt.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        user_online_or_not_txt.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
        user_online_or_not_txt.setVisibility(View.VISIBLE);
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
                            msg_model.setOnlineStatus(user_model.getOnlineStatus());
                            msg_model.setTypingStatus(user_model.getTypingStatus());

                            HashMap<String, Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("receiver_no", msg_model.getReceiverNo());
                            objectsHashMap.put("receiver_name", msg_model.getReceiverName());
                            objectsHashMap.put("receiver_profileImage", msg_model.getReceiverProfileImg());
                            objectsHashMap.put("receiver_info", msg_model.getReceiverInfo());
                            objectsHashMap.put("room_id", msg_model.getRoomId());
                            objectsHashMap.put("onlineStatus", msg_model.getOnlineStatus());
                            objectsHashMap.put("typingStatus", msg_model.getTypingStatus());

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
                            objectsHashMap.put("onlineStatus", msg_model.getOnlineStatus());
                            objectsHashMap.put("typingStatus", msg_model.getTypingStatus());
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

                        //get value of online & typing status
                        String online = "" + ds.child("onlineStatus").getValue();
                        String typing = "" + ds.child("typingStatus").getValue();
                        if (typing.equals(receiverMobileNo)) {
                            user_online_or_not_txt.setVisibility(View.VISIBLE);
                            user_online_or_not_txt.setText("typing...");
                        } else if (online.equals("online")) {
                            user_online_or_not_txt.setVisibility(View.VISIBLE);
                            user_online_or_not_txt.setText(online);
                        }

                        user_name.setText(title);
                        try {
                            Glide.with(profile_img).load(profileImage).into(profile_img);
                        } catch (Exception e) {
                            profile_img.setImageResource(R.drawable.img_default_person);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}