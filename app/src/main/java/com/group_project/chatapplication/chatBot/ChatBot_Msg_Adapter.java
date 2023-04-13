package com.group_project.chatapplication.chatBot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.group_project.chatapplication.R;

import java.util.List;
import java.util.Objects;

public class ChatBot_Msg_Adapter extends RecyclerView.Adapter {

    List<ChatBot_Msg_Model> msg_modelList;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatBot_Msg_Adapter(List<ChatBot_Msg_Model> msg_modelList) {
        this.msg_modelList = msg_modelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_ui, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_ui, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Objects.equals(msg_modelList.get(position).getSentBy(), "me")) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatBot_Msg_Model chatBot_msg_model = msg_modelList.get(position);
        if (holder.getClass() == UserViewHolder.class) {
            ((UserViewHolder) holder).senderChatView.setVisibility(View.VISIBLE);
            ((UserViewHolder) holder).senderMsg.setText(chatBot_msg_model.getMessage());
        } else {
            ((BotViewHolder) holder).receiverChatView.setVisibility(View.VISIBLE);
            ((BotViewHolder) holder).receiverMsg.setText(chatBot_msg_model.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return msg_modelList.size();
    }

    public class BotViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout receiverChatView;
        TextView receiverMsg;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverChatView = itemView.findViewById(R.id.receiverChatView);
            receiverMsg = itemView.findViewById(R.id.receiverMsg);
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout senderChatView;
        TextView senderMsg;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            senderChatView = itemView.findViewById(R.id.senderChatView);
            senderMsg = itemView.findViewById(R.id.senderMsg);
        }
    }

}
