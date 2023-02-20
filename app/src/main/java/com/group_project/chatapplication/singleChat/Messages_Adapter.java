package com.group_project.chatapplication.singleChat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group_project.chatapplication.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages_Adapter extends RecyclerView.Adapter<Messages_Adapter.MyViewHolder> {

    private List<Messages_Model> messages_models;
    private final Context context;

    public Messages_Adapter(List<Messages_Model> messagesLists, Context context) {
        this.messages_models = messagesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public Messages_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Messages_Model list2 = messages_models.get(position);

        holder.name.setText(list2.getName());
        holder.lastMessage.setText(list2.getLastMessage());

        if (list2.getUnseenMessages() == 0) {
            holder.unseenMessages.setVisibility(View.GONE);
        } else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", list2.getName());
                intent.putExtra("mobile", list2.getMobile());
                context.startActivity(intent);
            }
        });

    }

    public void updateData(List<Messages_Model> messages_models) {
        this.messages_models = messages_models; //final
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages_models.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView dp_img;
        private TextView name;
        private TextView lastMessage;
        private TextView unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dp_img = itemView.findViewById(R.id.dp_img);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);

        }
    }
}
