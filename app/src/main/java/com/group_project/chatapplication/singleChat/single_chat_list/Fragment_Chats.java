package com.group_project.chatapplication.singleChat.single_chat_list;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.stories.Story_Preview_Activity;
import com.group_project.chatapplication.stories.TopStories_Adapter;
import com.group_project.chatapplication.stories.Stories_Model;
import com.group_project.chatapplication.stories.UserStories_Model;
import com.group_project.chatapplication.registration.User_Model;
import com.group_project.chatapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Chats extends Fragment {

    View chatFragmentView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, referenceForUserIMG, databaseReferenceStories, referenceForList;
    String fetch_phone_number, fetch_phone_without_91;
    ConstraintLayout layout_upload_stories;
    CircleImageView img_profile_show_stories;
    TopStories_Adapter topStories_adapter;
    ArrayList<UserStories_Model> userStories_models_list;
    RecyclerView stories_list, chatlistRv;
    ProgressDialog progressDialog;
    User_Model user_model;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ArrayList<Chat_List_Model> list;
    Chat_List_Adapter chat_list_adapter;
    String imageUri;
    RecyclerView groupRv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        chatFragmentView = inflater.inflate(R.layout.fragment__chats, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        groupRv = chatFragmentView.findViewById(R.id.groupRv);

        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();
        fetch_phone_without_91 = user.getPhoneNumber().replace("+91", "");

        userStories_models_list = new ArrayList<>();
        topStories_adapter = new TopStories_Adapter(getContext(), userStories_models_list);
        stories_list = chatFragmentView.findViewById(R.id.stories_list);
        layout_upload_stories = chatFragmentView.findViewById(R.id.layout_upload_stories);
        img_profile_show_stories = chatFragmentView.findViewById(R.id.img_profile_show_stories);
        chatlistRv = chatFragmentView.findViewById(R.id.chatlistRv);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Story Uploading...");
        progressDialog.setCancelable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        referenceForUserIMG = FirebaseDatabase.getInstance().getReference().child("Users Details").child(fetch_phone_number);

        databaseReferenceStories = FirebaseDatabase.getInstance().getReference().child("Stories");
        automaticDeleteStory();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        stories_list.setLayoutManager(linearLayoutManager);
        stories_list.setAdapter(topStories_adapter);

        //for display chat list in recyclerview
        referenceForList = FirebaseDatabase.getInstance().getReference().child("Chat");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chatlistRv.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        chat_list_adapter = new Chat_List_Adapter(getContext(), list); // wait
        chatlistRv.setAdapter(chat_list_adapter);

        displayChatUserList();

        // Fetch profile images for show/add Stories
        referenceForUserIMG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("phone")) {
                    imageUri = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();
                    try {
                        Glide.with(img_profile_show_stories).load(imageUri).into(img_profile_show_stories);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Profile Images can't fetch!", Toast.LENGTH_SHORT).show();
            }
        });

        // fetch values from User Details table
        firebaseDatabase.getReference().child("Users Details").child(fetch_phone_number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_model = snapshot.getValue(User_Model.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Normal stories uploading code ...
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    if (result.getData().getData() != null) {
                        assert result.getData() != null;
                        String img = result.getData().getData().toString();
                        Intent id = new Intent(getContext(), Story_Preview_Activity.class);
                        id.putExtra("pass_selected_img", img);
                        Log.d("", "IMG: " + img);
                        startActivity(id);
                    } else {
                        startActivity(new Intent(getContext(), MainActivity.class));
                        requireActivity().finishAffinity();
                    }
                }
            }
        });

        // Normal display stories code....
        firebaseDatabase.getReference().child("Stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userStories_models_list.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStories_Model userStoriesModel = new UserStories_Model();
                        userStoriesModel.setName(storySnapshot.child("name").getValue(String.class));
                        userStoriesModel.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        userStoriesModel.setLastupdated(storySnapshot.child("lastUpdate").getValue(Long.class));
                        ArrayList<Stories_Model> stories_models = new ArrayList<>();
                        for (DataSnapshot statusSnapshot : storySnapshot.child("Status").getChildren()) {
                            Stories_Model sample_status = statusSnapshot.getValue(Stories_Model.class);
                            stories_models.add(sample_status);
                        }
                        userStoriesModel.setStatuses(stories_models);
                        userStories_models_list.add(userStoriesModel);
                    }
                    topStories_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        layout_upload_stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_img = new Intent();
                intent_img.setType("image/*");
                intent_img.setAction(Intent.ACTION_PICK);
                activityResultLauncher.launch(intent_img);
            }
        });

        return chatFragmentView;
    }//end


    public void automaticDeleteStory() {
        databaseReferenceStories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fetch_phone_number).hasChild("Status")) {
                    for (DataSnapshot storySnapshot : snapshot.child(fetch_phone_number).child("Status").getChildren()) {
                        long storyKey = Long.parseLong(Objects.requireNonNull(storySnapshot.getKey()));
                        long currentDate = new Date().getTime();
                        long differenceDate = currentDate - storyKey;
                        // for 24 hours
                        if (differenceDate >= 86400000) {
                            FirebaseDatabase.getInstance().getReference().child("Stories").child(fetch_phone_number).getRef().setValue(null);
                            Log.d("", "DELETE STORY.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Automatic stories can't delete!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChatUserList() {
        referenceForList.child(fetch_phone_without_91).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    if (dataSnapshot1.hasChild("Messages")) {
                        Chat_List_Model listmodel = dataSnapshot1.getValue(Chat_List_Model.class);
                        list.add(listmodel);
                    }
                }
                chat_list_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        automaticDeleteStory();
    }
}