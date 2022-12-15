package com.group_project.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class GroupChatActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ImageButton sendMessageButton;
    EditText userMessageInput;
    ScrollView mScrollView;
    TextView displayTextMessage;
    String currentGroupName, currentUserId, currentUserName;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        auth = FirebaseAuth.getInstance();
//        currentUserId = auth.getCurrentUser().getUid();

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(getApplicationContext(), currentGroupName, Toast.LENGTH_SHORT).show();
        allInitializationFields();
        getUserInformation();
    }

    private void allInitializationFields() {
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        displayTextMessage = findViewById(R.id.group_chat_text_display);
        mScrollView = findViewById(R.id.my_scroll_view);

    }

    private void getUserInformation() {

    }

}