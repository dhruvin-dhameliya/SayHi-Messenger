package com.group_project.chatapplication.singleChat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
    String currentContactName, myMobileNo, receiverMobileNo, getName, senderRoom, receiverRoom;
    RecyclerView chattingRecycleView;
    ChatAd chatAd;
    User_Model user_model;
    Msg_Model msg_model = new Msg_Model();
    ArrayList<Chatmodel> chatModel = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);

        currentContactName = getIntent().getExtras().get("contact_name_pass").toString().trim();
        receiverMobileNo = getIntent().getExtras().get("contact_number_pass").toString().replace(" ", "").replace("-", "").replace("+91", "");

        mToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(currentContactName);
        getSupportActionBar().setSubtitle(receiverMobileNo);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        myMobileNo = Objects.requireNonNull(user.getPhoneNumber()).replace("+91", "");
        getName = user.getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat");

        chattingRecycleView = findViewById(R.id.chat_recyclearview);

        chatAd = new ChatAd(chatModel, this);
        chattingRecycleView.setAdapter(chatAd);

        senderRoom = myMobileNo + receiverMobileNo;
        receiverRoom = receiverMobileNo + myMobileNo;

        check_number_exist_or_not();

        do_chat_messages();

        display_chat_messages();
    }

    //send message
    public void do_chat_messages() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTxtMessage = userMessageInput.getText().toString();
                final Chatmodel chatmodel = new Chatmodel(myMobileNo, getTxtMessage);

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
                    firebaseDatabase.getReference().child("Users Details").child("+91" + receiverMobileNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user_model = snapshot.getValue(User_Model.class);
                            msg_model.setReceiverNo(user_model.getPhone());
                            msg_model.setReceiverName(user_model.getName());
                            msg_model.setReceiverProfileImg(user_model.getProfile_image());
                            msg_model.setReceiverInfo(user_model.getAbout());

                            HashMap<String, Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("receiver_no", msg_model.getReceiverNo());
                            objectsHashMap.put("receiver_name", msg_model.getReceiverName());
                            objectsHashMap.put("receiver_profileImage", msg_model.getReceiverProfileImg());
                            objectsHashMap.put("receiver_info", msg_model.getReceiverInfo());
                            firebaseDatabase.getReference().child("Chat").child(myMobileNo).child(senderRoom).updateChildren(objectsHashMap);

                            Toast.makeText(getApplicationContext(), "Transfer completed !!!", Toast.LENGTH_SHORT).show();
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

}