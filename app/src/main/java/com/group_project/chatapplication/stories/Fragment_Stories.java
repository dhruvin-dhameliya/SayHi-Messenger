package com.group_project.chatapplication.stories;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.registration.User_Model;
import com.group_project.chatapplication.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Fragment_Stories extends Fragment {

    View storiesFragmentView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, referenceForUserIMG;
    String fetch_phone_number;
    RoundedImageView square_img_upload_story;
    TopStories_Adapter_2 topStories_adapter_2;
    MaterialButton btn_delete_your_stories;
    ArrayList<UserStories_Model> userStories_models_list;
    RecyclerView stories_list;
    ProgressDialog progressDialog;
    User_Model user_model;
    ActivityResultLauncher<Intent> activityResultLauncher;
    String imageUri;
    FloatingActionButton add_txt_story;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        storiesFragmentView = inflater.inflate(R.layout.fragment__stories, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();

        userStories_models_list = new ArrayList<>();
        topStories_adapter_2 = new TopStories_Adapter_2(getContext(), userStories_models_list);
        stories_list = storiesFragmentView.findViewById(R.id.stories_list_2);
        square_img_upload_story = storiesFragmentView.findViewById(R.id.square_img_upload_story);
        btn_delete_your_stories = storiesFragmentView.findViewById(R.id.btn_delete_your_stories);
        add_txt_story = storiesFragmentView.findViewById(R.id.add_txt_story);

        add_txt_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Text_Story_Activity.class));
            }
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Story Uploading...");
        progressDialog.setCancelable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        referenceForUserIMG = FirebaseDatabase.getInstance().getReference().child("Users Details").child(fetch_phone_number);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        stories_list.setLayoutManager(gridLayoutManager);
        stories_list.setAdapter(topStories_adapter_2);

        // Fetch profile images for show/add Stories
        referenceForUserIMG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("phone")) {
                    imageUri = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();
                    try {
                        Glide.with(square_img_upload_story.getContext()).load(imageUri).into(square_img_upload_story);
                    }catch (Exception e){
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
                    topStories_adapter_2.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        square_img_upload_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_img = new Intent();
                intent_img.setType("image/*");
                intent_img.setAction(Intent.ACTION_PICK);
                activityResultLauncher.launch(intent_img);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Stories").child(fetch_phone_number).child("Status");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btn_delete_your_stories.setVisibility(View.VISIBLE);
                } else {
                    btn_delete_your_stories.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Delete all uploaded Stories...
        btn_delete_your_stories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(storiesFragmentView.getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Delete your stories?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Stories").child(fetch_phone_number).getRef().setValue(null);
                        Toast.makeText(getContext(), "Stories Deleted.", Toast.LENGTH_SHORT).show();
                        btn_delete_your_stories.setVisibility(View.GONE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        return storiesFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Stories").child(fetch_phone_number).child("Status");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btn_delete_your_stories.setVisibility(View.VISIBLE);
                } else {
                    btn_delete_your_stories.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}