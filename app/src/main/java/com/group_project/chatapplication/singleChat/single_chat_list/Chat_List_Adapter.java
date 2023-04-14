package com.group_project.chatapplication.singleChat.single_chat_list;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.singleChat.single_chat_messages.Single_Chat_Messages_Activity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_List_Adapter extends RecyclerView.Adapter<Chat_List_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Chat_List_Model> list;
    String room_id;

    public Chat_List_Adapter(Context context, ArrayList<Chat_List_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_singlechat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat_List_Model listmodel = list.get(position);
        holder.chatName.setText(listmodel.getReceiver_name());
        String chatIcon = listmodel.getReceiver_profileImage();
        loadLastMessage(listmodel, holder);

        try {
            Glide.with(holder.img).load(chatIcon).into(holder.img);

        } catch (Exception e) {
            holder.img.setImageResource(R.drawable.img_contact_user);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in future
                Intent intent = new Intent(context, Single_Chat_Messages_Activity.class);
                intent.putExtra("pass_receiver_name", listmodel.getReceiver_name());
                intent.putExtra("pass_receiver_number", listmodel.getReceiver_no());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView chatName, lastMessage, lastTime;
        ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.single_chat_profile_img);
            chatName = itemView.findViewById(R.id.chat_title);
            lastMessage = itemView.findViewById(R.id.last_message);
            lastTime = itemView.findViewById(R.id.last_time);
            icon = itemView.findViewById(R.id.icon_img_file);
        }
    }


    public void loadLastMessage(Chat_List_Model chat_list_model, MyViewHolder holder) {
        //get
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String fetch_phone_without_91 = Objects.requireNonNull(user.getPhoneNumber()).replace("+91", "");
        String fetch_receiver_mobile_without_91 = chat_list_model.getReceiver_no().replace("+91", "");

        room_id = fetch_phone_without_91 + fetch_receiver_mobile_without_91;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chat");
        ref.child(fetch_phone_without_91).child(room_id).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String message = "" + ds.child("message").getValue();
                            String messageType = "" + ds.child("type").getValue();

                            //convert time
                            String lastMsgTime = longToDateString(Long.parseLong(timestamp), "hh:mm a");

                            if (messageType.equals("image")) {
                                holder.icon.setVisibility(View.VISIBLE);
                                holder.icon.setImageResource(R.drawable.icon_photo_msg);
                                holder.lastMessage.setText("Photo");
                            } else if (messageType.equals("video")) {
                                holder.icon.setVisibility(View.VISIBLE);
                                holder.icon.setImageResource(R.drawable.icon_video_msg);
                                holder.lastMessage.setText("Video");
                            } else if (messageType.equals("file")) {
                                holder.icon.setVisibility(View.VISIBLE);
                                holder.icon.setImageResource(R.drawable.icon_file_msg);
                                holder.lastMessage.setText("Document");
                            } else {
                                holder.icon.setVisibility(View.GONE);
                                byte[] data = Base64.decode(message, Base64.DEFAULT);
                                String text = new String(data, StandardCharsets.UTF_8).toString();
                                holder.lastMessage.setText(text);
                            }

                            holder.lastTime.setText(lastMsgTime);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static String longToDateString(long timestamp, String format) {
        return DateFormat.format(format, new Date(timestamp)).toString();
    }
}
