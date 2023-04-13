package com.group_project.chatapplication.groupChat.group_list;

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
import com.group_project.chatapplication.groupChat.group_chat_messages.Group_Chat_Messages_Activity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Adapter_Group_List extends RecyclerView.Adapter<Adapter_Group_List.HolderGroupchatList> {

    Context context;
    ArrayList<Model_Group_List> groupArrayList;
    FirebaseAuth firebaseAuth;
    String fetch_number,encoded_deleted_already_msg = "VGhpcyBtZXNzYWdlIHdhcyBkZWxldGVk";

    public Adapter_Group_List(Context context, ArrayList<Model_Group_List> groupArrayList) {
        this.context = context;
        this.groupArrayList = groupArrayList;
    }

    @NonNull
    @Override
    public HolderGroupchatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat, parent, false);
        return new HolderGroupchatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupchatList holder, int position) {
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        fetch_number= user.getPhoneNumber();

        Model_Group_List modelGroup = groupArrayList.get(position);
        String groupId = modelGroup.getGroupId();
        String groupIcon = modelGroup.getGroupIcon();
        String grouptitle = modelGroup.getGroupTitle();

        holder.groupTv.setText("");
        holder.timeTv.setText("");
        holder.messageTv.setText("");
        //load last message
        loadLastMessage(modelGroup, holder);

        holder.grouptitle.setText(grouptitle);
        try {
            Glide.with(holder.groupIcon).load(groupIcon).into(holder.groupIcon);

        } catch (Exception e) {
            holder.groupIcon.setImageResource(R.drawable.img_default_person);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in future
                Intent intent = new Intent(context, Group_Chat_Messages_Activity.class);
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);
            }
        });
    }//end

    private void loadLastMessage(Model_Group_List modelGroup, HolderGroupchatList holder) {
        //get
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(modelGroup.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            String message = "" + ds.child("message").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String sender = "" + ds.child("sender").getValue();
                            String messageType = "" + ds.child("type").getValue();

                            //convert time
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            String dateTime = DateFormat.format("dd/MM/yy hh:mm aa", cal).toString();


                            if (messageType.equals("image")) {
                                if (message.equals(encoded_deleted_already_msg)){
                                    byte[] data = Base64.decode(message, Base64.DEFAULT);
                                    String text = new String(data, StandardCharsets.UTF_8);
                                    holder.messageTv.setText(text);
                                }else {
                                    holder.messageTv.setText("Sent Photo");
                                }

                            } else if (messageType.equals("file")) {
                                if (message.equals(encoded_deleted_already_msg)){
                                    byte[] data = Base64.decode(message, Base64.DEFAULT);
                                    String text = new String(data, StandardCharsets.UTF_8);
                                    holder.messageTv.setText(text);
                                }else {
                                    holder.messageTv.setText("Sent Document");
                                }

                            }
                            else if (messageType.equals("video")){
                                if (message.equals(encoded_deleted_already_msg)){
                                    byte[] data = Base64.decode(message, Base64.DEFAULT);
                                    String text = new String(data, StandardCharsets.UTF_8);
                                    holder.messageTv.setText(text);
                                }else {
                                    holder.messageTv.setText("Sent Video");
                                }
                            }
                            else  {
                                byte[] data = Base64.decode(message, Base64.DEFAULT);
                                String text = new String(data, StandardCharsets.UTF_8);
                                holder.messageTv.setText(text);
                            }

                            holder.timeTv.setText(dateTime);

                            //get info
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Details");
                            reference.orderByChild("id").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                String name = "" + ds.child("name").getValue().toString().trim();
                                                String phone=""+ds.child("phone").getValue().toString().trim();

                                                if (phone.equals(fetch_number)){
                                                    holder.groupTv.setText("You:");
                                                }else {
                                                    holder.groupTv.setText(name + ":");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return groupArrayList.size();
    }

    class HolderGroupchatList extends RecyclerView.ViewHolder {

        ImageView groupIcon;
        TextView grouptitle, groupTv, messageTv, timeTv;

        public HolderGroupchatList(@NonNull View itemView) {
            super(itemView);
            groupIcon = itemView.findViewById(R.id.groupicon);
            groupTv = itemView.findViewById(R.id.groupTv);
            grouptitle = itemView.findViewById(R.id.grouptitle);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
