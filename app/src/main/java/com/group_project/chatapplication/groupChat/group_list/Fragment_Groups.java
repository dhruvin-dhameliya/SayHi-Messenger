package com.group_project.chatapplication.groupChat.group_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.R;

import java.util.ArrayList;

public class Fragment_Groups extends Fragment {

    View groupFragment;
    String fetch_phone_number;
    RecyclerView groupRv;
    ArrayList<Model_Group_List> groupArrayList;
    Adapter_Group_List adapterGroupchatList;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragment = inflater.inflate(R.layout.fragment__groups, container, false);

        groupRv = groupFragment.findViewById(R.id.groupRv);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();

        loadGroupChatList();

        return groupFragment;
    }

    private void loadGroupChatList() {
        groupArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Participants").child(fetch_phone_number).exists()) {
                        Model_Group_List modelGroup = ds.getValue(Model_Group_List.class);
                        groupArrayList.add(modelGroup);
                    }
                }
                adapterGroupchatList = new Adapter_Group_List(getContext(), groupArrayList);
                groupRv.setAdapter(adapterGroupchatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}