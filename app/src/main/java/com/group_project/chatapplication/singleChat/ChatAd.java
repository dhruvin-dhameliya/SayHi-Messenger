package com.group_project.chatapplication.singleChat;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group_project.chatapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAd extends RecyclerView.Adapter {

    ArrayList<Chatmodel> chatmodels;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAd(ArrayList<Chatmodel> chatmodels, Context context) {
        this.chatmodels = chatmodels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new RecieverViewHolder(view);
        }
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String myMobileNo;

    {
        assert user != null;
        myMobileNo = user.getPhoneNumber().replace("+91", "");
    }

    @Override
    public int getItemViewType(int position) {
        if (chatmodels.get(position).getUid().equals(myMobileNo)) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chatmodel chatmodel = chatmodels.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setText(chatmodel.getMessage());
            String senderMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
            ((SenderViewHolder) holder).senderTime.setText(senderMsgTime);
        } else {
            ((RecieverViewHolder) holder).receiverMsg.setText(chatmodel.getMessage());
            String reciverMsgTime = longToDateString(Long.parseLong(chatmodel.getTimestamp()), "dd-MM-yyyy hh:mm");
            ((RecieverViewHolder) holder).receiverTime.setText(reciverMsgTime);
        }
    }

    @Override
    public int getItemCount() {
        return chatmodels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.opponent_message);
            receiverTime = itemView.findViewById(R.id.opponent_message_time);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.my_message);
            senderTime = itemView.findViewById(R.id.my_message_time);
        }
    }

    public static String longToDateString(long timestamp, String format) {
        return DateFormat.format(format, new Date(timestamp)).toString();
    }

}
