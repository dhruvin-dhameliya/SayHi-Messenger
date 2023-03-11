package com.group_project.chatapplication.singleChat.single_chat_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.singleChat.single_chat_messages.Single_Chat_Messages_Activity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_List_Adapter extends RecyclerView.Adapter<Chat_List_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Chat_List_Model> list;
    String room__id;

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
        holder.info.setText(listmodel.getReceiver_info());
        String chatIcon = listmodel.getReceiver_profileImage();
//        loadLastMessage(listmodel, holder);

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
        TextView chatName, info;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.single_chat_profile_img);
            chatName = itemView.findViewById(R.id.chat_title);
            info = itemView.findViewById(R.id.info_txt);
        }
    }
/*
    private void loadLastMessage(Chat_List_Model chat_list_model, MyViewHolder holder) {
        //get
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chat");
//        String sender_no = chat_list_model.getSender_no().replace("+91","");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ;
        FirebaseUser user = auth.getCurrentUser();
        String fetch_phone_without_91 = user.getPhoneNumber().replace("+91", "");

        ref.child(fetch_phone_without_91)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            room__id = dataSnapshot.getKey();
                            Log.d("ROOM_ID:", "" + room__id);

                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Chat");
                            ref2.child(fetch_phone_without_91).child(room__id).child("Messages").limitToLast(1)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                String timestamp = "" + ds.child("timestamp").getValue();
                                                String lm = "" + ds.child("message").getValue();

                                                Log.d("timestamp:", "" + timestamp);

                                                //convert time
                                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                                cal.setTimeInMillis(Long.parseLong(timestamp));
                                                Log.d("hello:", "onDataChange: ");
                                                String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                                                holder.timestamp.setText(dateTime);
                                                holder.lastMessage.setText(lm);
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
*/

}
