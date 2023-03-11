package com.group_project.chatapplication.groupChat.group_chat_messages;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Group_Chat_Messages extends RecyclerView.Adapter<Adapter_Group_Chat_Messages.HolderGroupChat> {
    final int MSG_TYPE_LEFT = 0;
    final int MSG_TYPE_RIGHT = 1;
    Context context;
    ArrayList<Model_Group_Chat_Messages> modelGroupChats;
    FirebaseAuth auth;
    Model_Group_Chat_Messages modelGroupChat;
    String groupId;

    public Adapter_Group_Chat_Messages(Context context, ArrayList<Model_Group_Chat_Messages> modelGroupChats, String groupId) {
        this.context = context;
        this.modelGroupChats = modelGroupChats;
        this.groupId = groupId;
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
        }
        return new HolderGroupChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        modelGroupChat = modelGroupChats.get(position);
        String timestamp = modelGroupChat.getTimestamp();
        String message = modelGroupChat.getMessage().trim();//text,image
        String senderUid = modelGroupChat.getSender();
        String messageType = modelGroupChat.getType();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yy hh:mm aa", cal).toString();

        if (messageType.equals("text")) {
            holder.user_txt_msg.setVisibility(View.VISIBLE);
            holder.user_txt_msg.setText(message);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setVisibility(View.GONE);
            holder.user_txt_msg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }

        if (messageType.equals("image")) {
            holder.user_txt_msg.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setVisibility(View.GONE);
            holder.user_img_msg_layout.setVisibility(View.VISIBLE);
            Glide.with(holder.user_img_msg).load(message).into(holder.user_img_msg);
            holder.user_img_msg_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_full_screen_photo_Activity.class);
                    intent.putExtra("image", message);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
            holder.user_img_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_full_screen_photo_Activity.class);
                    intent.putExtra("image", message);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
        }

        if (messageType.equals("file")) {
            holder.user_doc_msg_layout.setVisibility(View.VISIBLE);
            holder.user_txt_msg.setVisibility(View.GONE);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Doc_WebView_Activty.class);
                    intent.putExtra("pass_pdf_url", message);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
            holder.user_doc_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Doc_WebView_Activty.class);
                    intent.putExtra("pass_pdf_url", message);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
        }

        if (message.equals("This message was deleted")) {
            holder.user_txt_msg.setVisibility(View.VISIBLE);
            holder.user_txt_msg.setText(modelGroupChat.getMessage());
            holder.user_img_msg.setVisibility(View.GONE);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_doc_msg.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setVisibility(View.GONE);
        }

        // Delete Text, Image & Documents messages...
        holder.user_img_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msgTimestamp = modelGroupChats.get(position).getTimestamp();
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                        Query query = dbref.child("Messages").orderByChild("timestamp").equalTo(msgTimestamp);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String msg = ds.child("message").getValue().toString();
                                    if (msg.equals("This message was deleted")) {
                                        ds.getRef().removeValue();
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("message", "This message was deleted");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
        holder.user_img_msg_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msgTimestamp = modelGroupChats.get(position).getTimestamp();
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                        Query query = dbref.child("Messages").orderByChild("timestamp").equalTo(msgTimestamp);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String msg = ds.child("message").getValue().toString();
                                    if (msg.equals("This message was deleted")) {
                                        ds.getRef().removeValue();
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("message", "This message was deleted");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
        holder.user_doc_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msgTimestamp = modelGroupChats.get(position).getTimestamp();
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                        Query query = dbref.child("Messages").orderByChild("timestamp").equalTo(msgTimestamp);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String msg = ds.child("message").getValue().toString();
                                    if (msg.equals("This message was deleted")) {
                                        ds.getRef().removeValue();
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("message", "This message was deleted");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
        holder.user_doc_msg_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msgTimestamp = modelGroupChats.get(position).getTimestamp();
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                        Query query = dbref.child("Messages").orderByChild("timestamp").equalTo(msgTimestamp);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String msg = ds.child("message").getValue().toString();
                                    if (msg.equals("This message was deleted")) {
                                        ds.getRef().removeValue();
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("message", "This message was deleted");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
        holder.outer_message_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msgTimestamp = modelGroupChats.get(position).getTimestamp();
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                        Query query = dbref.child("Messages").orderByChild("timestamp").equalTo(msgTimestamp);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String msg = ds.child("message").getValue().toString();
                                    if (msg.equals("This message was deleted")) {
                                        ds.getRef().removeValue();
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("message", "This message was deleted");
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });

        setUsername(modelGroupChat, holder);
        holder.msg_time.setText(dateTime);
    }


    private void setUsername(Model_Group_Chat_Messages modelGroupChat, HolderGroupChat holder) {
        //get victim info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users Details");
        ref.orderByChild("id").equalTo(modelGroupChat.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String groupIcon = "" + ds.child("profile_image").getValue();
                    holder.user_name.setText(name);
                    Glide.with(holder.user_profile_img).load(groupIcon).into(holder.user_profile_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelGroupChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (modelGroupChats.get(position).getSender().equals(auth.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class HolderGroupChat extends RecyclerView.ViewHolder {

        CircleImageView user_profile_img;
        TextView msg_time;
        LinearLayout msg_layout, outer_message_layout;
        RelativeLayout user_img_msg_layout, user_doc_msg_layout;
        TextView user_name;
        TextView user_txt_msg;
        RoundedImageView user_img_msg, user_doc_msg;


        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
            user_profile_img = itemView.findViewById(R.id.user_profile_img);
            msg_time = itemView.findViewById(R.id.msg_time);
            msg_layout = itemView.findViewById(R.id.msg_layout);
            outer_message_layout = itemView.findViewById(R.id.outer_message_layout);
            user_img_msg_layout = itemView.findViewById(R.id.user_img_msg_layout);
            user_doc_msg_layout = itemView.findViewById(R.id.user_doc_msg_layout);
            user_name = itemView.findViewById(R.id.user_name);
            user_txt_msg = itemView.findViewById(R.id.user_txt_msg);
            user_img_msg = itemView.findViewById(R.id.user_img_msg);
            user_doc_msg = itemView.findViewById(R.id.user_doc_msg);
        }
    }
}
