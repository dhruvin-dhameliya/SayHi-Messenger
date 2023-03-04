package com.group_project.chatapplication.groupChat.group_chat_messages;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.ipsec.ike.IkeIpv4AddrIdentification;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ShareCompat;
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

import java.io.File;
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
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
            return new HolderGroupChat(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
            return new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        modelGroupChat = modelGroupChats.get(position);
        String timestamp = modelGroupChat.getTimestamp();
        String message = modelGroupChat.getMessage();//text,image
        String senderUid = modelGroupChat.getSender();
        String messageType = modelGroupChat.getType();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if (messageType.equals("text")) {
            //text message
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messagefile.setVisibility(View.GONE);
            holder.messageTv.setText(message);
        } else if (messageType.equals("file")) {
            //document message
            holder.messagefile.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            //Document click event
            holder.messagefile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Doc_WebView_Activty.class);
                    intent.putExtra("pass_pdf_url", message);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });

            //document delete
            holder.messagefile.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to Detele this message?");
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
                                        ds.getRef().removeValue();
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
        } else {
            //image message
            if (message.equals("This message was deleted")) {
                holder.messageIv.setVisibility(View.GONE);
                holder.messageTv.setVisibility(View.VISIBLE);
                holder.messagefile.setVisibility(View.GONE);
            } else {
                holder.messageIv.setVisibility(View.VISIBLE);
                holder.messageTv.setVisibility(View.GONE);
                holder.messagefile.setVisibility(View.GONE);
            }

            try {
                Glide.with(holder.messageIv).load(message).into(holder.messageIv);
            } catch (Exception e) {
                holder.messageIv.setImageResource(R.drawable.default_image_for_chat);
            }

            holder.messageIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Full_screen_photo_Activity.class);
                    intent.putExtra("image", message);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
        }

        holder.messageIv.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("message", "This message was deleted");
                                    ds.getRef().updateChildren(hashMap);
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

        holder.timetv.setText(dateTime);

        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to Detele this message?");
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
                    holder.nameTv.setText(name);
                    Glide.with(holder.leftside).load(groupIcon).into(holder.leftside);
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
        TextView nameTv, messageTv, timetv;
        LinearLayout messageLayout;
        CircleImageView leftside;
        ImageView messageIv, messagefile;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timetv = itemView.findViewById(R.id.timeTv);
            messageLayout = itemView.findViewById(R.id.messagelayout);
            leftside = itemView.findViewById(R.id.message_image);
            messageIv = itemView.findViewById(R.id.messageIv);
            messagefile = itemView.findViewById(R.id.messagefile);
        }
    }
}
