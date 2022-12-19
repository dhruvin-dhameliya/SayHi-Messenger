package com.group_project.chatapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatsFragment extends Fragment {

    View chatFragmentView;
    FloatingActionButton jump_chat_screen;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatFragmentView =  inflater.inflate(R.layout.fragment_chats, container, false);

        jump_chat_screen = chatFragmentView.findViewById(R.id.jump_chat_screen);
        jump_chat_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ContactShowActivity.class));
            }
        });

        return chatFragmentView;
    }
}