package com.group_project.chatapplication.All_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.group_project.chatapplication.Adapter_Class.Chat_Adapter;
import com.group_project.chatapplication.Model_Class.Chat_Model;
import com.group_project.chatapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    Toolbar mToolbar;
    FloatingActionButton sendMessageButton;
    EditText userMessageInput;
    ScrollView mScrollView;
    RecyclerView chat_recyclearview;
    String currentContactName, fetch_phone_number, fetch_name;
    ImageView img_chat_wallpaper, send_file;
    FirebaseAuth auth;
    DatabaseReference databaseReference, databaseReferenceChat;
    FirebaseStorage firebaseStorage;

    String receiverMobileNo;
    private final List<Chat_Model> chat_models = new ArrayList<>();
    private Chat_Adapter chat_adapter;
    private Boolean loadingFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        fetch_phone_number = user.getPhoneNumber();
        fetch_name = user.getDisplayName();

        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        img_chat_wallpaper = findViewById(R.id.img_chat_wallpaper);
        mScrollView = findViewById(R.id.my_scroll_view);
        chat_recyclearview = findViewById(R.id.chat_recyclearview);
        send_file = findViewById(R.id.send_file);

        chat_recyclearview.setHasFixedSize(true);
        chat_recyclearview.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chat_adapter = new Chat_Adapter(chat_models, ChatActivity.this);
        chat_recyclearview.setAdapter(chat_adapter);

        currentContactName = getIntent().getExtras().get("contact_name_pass").toString();
        receiverMobileNo = getIntent().getExtras().get("contact_number_pass").toString().replace(" ", "").replace("-", "");

        Toast.makeText(getApplicationContext(), currentContactName, Toast.LENGTH_SHORT).show();
        mToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(currentContactName);
        getSupportActionBar().setSubtitle(receiverMobileNo);

        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        doChat();
        displayChat();

        // Display Wallpaper code...
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat Wallpaper").child(fetch_phone_number).child("wallpaper_image");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                        String retrieveWallpaperImage = snapshot.getValue(String.class);
                        Glide.with(img_chat_wallpaper.getContext()).load(retrieveWallpaperImage).into(img_chat_wallpaper);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Wallpaper not set!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void doChat() {
        //send message
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentTimestamp = String.valueOf(new java.util.Date().getTime());
                String getTxtMessage = userMessageInput.getText().toString();

                databaseReferenceChat.child(fetch_phone_number).child("sender_no").setValue(fetch_phone_number);
                databaseReferenceChat.child(fetch_phone_number).child("receiver_no").setValue(receiverMobileNo);
                databaseReferenceChat.child(fetch_phone_number).child("messages").child(currentTimestamp).child("msg").setValue(getTxtMessage);
                databaseReferenceChat.child(fetch_phone_number).child("messages").child(currentTimestamp).child("sender_mobile").setValue(fetch_phone_number);
                userMessageInput.setText("");
            }
        });
    }

    public void displayChat() {

        // Chat display code here...
        databaseReferenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(fetch_phone_number).hasChild("messages")) {

                    chat_models.clear();

                    for (DataSnapshot messagesSnapshot : snapshot.child(fetch_phone_number).child("messages").getChildren()) {

                        if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("sender_mobile")) {

                            final String messagesTimestamps = messagesSnapshot.getKey();
                            final String getMobile = messagesSnapshot.child("sender_mobile").getValue(String.class);
                            final String getMsg = messagesSnapshot.child("msg").getValue(String.class);

                            String myDate = longToDateString(Long.parseLong(messagesTimestamps), "dd-MM-yyyy hh:mm");
                            Log.d("", "=== MYDate: " + myDate);

                            Chat_Model chat_model = new Chat_Model(getMobile, fetch_name, getMsg, myDate);
                            chat_models.add(chat_model);

                            loadingFirstTime = false;
                            chat_adapter.updateChatList(chat_models);

                            chat_recyclearview.scrollToPosition(chat_models.size() - 1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static String longToDateString(long timestamp, String format) {
        return DateFormat.format(format, new Date(timestamp)).toString();
    }

}