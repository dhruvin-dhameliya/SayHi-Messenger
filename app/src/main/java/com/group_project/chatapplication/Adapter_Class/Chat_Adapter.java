package com.group_project.chatapplication.Adapter_Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.group_project.chatapplication.Model_Class.Chat_Model;
import com.group_project.chatapplication.R;

import java.util.List;

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.MyViewHolder> {

    private List<Chat_Model> chat_models;
    private final Context context;
    private String userMobile;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    public Chat_Adapter(List<Chat_Model> chat_models, Context context) {
        this.chat_models = chat_models;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String myMobileNo = user.getPhoneNumber();

        this.userMobile = myMobileNo;

    }

    @NonNull
    @Override
    public Chat_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull Chat_Adapter.MyViewHolder holder, int position) {

        Chat_Model list2 = chat_models.get(position);

        if (list2.getMobile().equals(userMobile)) {
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.opponentLayout.setVisibility(View.VISIBLE);
            holder.myMessage.setText(list2.getMessages());
            holder.myTime.setText(list2.getDate());

        } else {
            holder.myLayout.setVisibility(View.GONE);
            holder.opponentLayout.setVisibility(View.GONE);
            holder.opponentMessage.setText(list2.getMessages());
            holder.opponentTime.setText(list2.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return chat_models.size();
    }

    public void updateChatList(List<Chat_Model> chatLists) {
        this.chat_models = chatLists;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout opponentLayout, myLayout;
        private TextView opponentMessage, myMessage;
        private TextView opponentTime, myTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            opponentLayout = itemView.findViewById(R.id.opponent_layout);
            myLayout = itemView.findViewById(R.id.my_layout);
            opponentMessage = itemView.findViewById(R.id.opponent_message);
            myMessage = itemView.findViewById(R.id.my_message);
            opponentTime = itemView.findViewById(R.id.opponent_message_time);
            myTime = itemView.findViewById(R.id.my_message_time);

        }
    }
}
