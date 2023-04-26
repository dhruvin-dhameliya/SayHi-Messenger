package com.group_project.chatapplication.groupChat.group_chat_messages;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.singleChat.single_chat_messages.Chat_Adapter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Group_Chat_Messages extends RecyclerView.Adapter<Adapter_Group_Chat_Messages.HolderGroupChat> {
    final int MSG_TYPE_LEFT = 0;
    final int MSG_TYPE_RIGHT = 1;
    Context context;
    ArrayList<Model_Group_Chat_Messages> modelGroupChats;
    FirebaseAuth auth;
    Model_Group_Chat_Messages modelGroupChat;
    String groupId, encoded_deleted_already_msg = "VGhpcyBtZXNzYWdlIHdhcyBkZWxldGVk"; // This message was deleted

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


        int[] reactions = {
                R.drawable.emoji_1_thumbs,
                R.drawable.emoji_2_heart,
                R.drawable.emoji_3_joy,
                R.drawable.emoji_4_open_mouth,
                R.drawable.emoji_5_crying,
                R.drawable.emoji_6_hands,
        };
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups");
        dbref.child(groupId).child("Messages").orderByChild("timestamp").equalTo(timestamp)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            try {
                                String feel = "" + dataSnapshot.child("feeling").getValue();
                                if (Integer.parseInt(feel) >= 0) {
                                    holder.feeling.setImageResource(reactions[Integer.parseInt(feel)]);
                                    holder.feeling.setVisibility(View.VISIBLE);
                                    holder.emoji_card.setVisibility(View.VISIBLE);
                                } else {
                                    holder.feeling.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.emoji_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.remove_reaction_dialogue);
                ImageView show_reaction = dialog.findViewById(R.id.remove_reaction_img);

                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups");
                dbref.child(groupId).child("Messages").orderByKey().equalTo(timestamp)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    try {
                                        String feel = "" + dataSnapshot.child("feeling").getValue();
                                        if (Integer.parseInt(feel) >= 0) {
                                            show_reaction.setImageResource(reactions[Integer.parseInt(feel)]);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                RelativeLayout tap_to_remove = dialog.findViewById(R.id.tap_to_remove);

                tap_to_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Map<String, Object> map = new HashMap<>();
                        map.put("feeling", -1);
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups");
                        dbref.child(groupId).child("Messages").child(timestamp).updateChildren(map);
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

        if (messageType.equals("text")) {
            holder.user_txt_msg.setVisibility(View.VISIBLE);
            byte[] data = Base64.decode(message, Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            holder.user_txt_msg.setText(text);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setVisibility(View.GONE);
            holder.user_video_msg_layout.setVisibility(View.GONE);
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
            holder.user_video_msg_layout.setVisibility(View.GONE);
            byte[] data = Base64.decode(message, Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            try {
                Glide.with(holder.user_img_msg).load(text).placeholder(R.drawable.default_image_for_chat).into(holder.user_img_msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.user_img_msg_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_full_screen_photo_Activity.class);
                    intent.putExtra("image", text);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
            holder.user_img_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_full_screen_photo_Activity.class);
                    intent.putExtra("image", text);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
        }

        //for video message
        if (messageType.equals("video")) {
            holder.user_txt_msg.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setVisibility(View.GONE);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_video_msg_layout.setVisibility(View.VISIBLE);
            byte[] data = Base64.decode(message, Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            try {
                Glide.with(holder.user_video_msg).load(text).placeholder(R.drawable.default_image_for_chat).into(holder.user_video_msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.user_video_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Full_Screen_Video_Activity.class);
                    intent.putExtra("video", text);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
            holder.user_video_msg_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Full_Screen_Video_Activity.class);
                    intent.putExtra("video", text);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });

        }

        if (messageType.equals("file")) {
            holder.user_doc_msg_layout.setVisibility(View.VISIBLE);
            holder.user_txt_msg.setVisibility(View.GONE);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_video_msg_layout.setVisibility(View.GONE);
            byte[] data = Base64.decode(message, Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            holder.user_doc_msg_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Doc_WebView_Activty.class);
                    intent.putExtra("pass_pdf_url", text);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
            holder.user_doc_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Group_Doc_WebView_Activty.class);
                    intent.putExtra("pass_pdf_url", text);
                    intent.putExtra("sender", senderUid);
                    context.startActivity(intent);
                }
            });
        }

        // This message was deleted
        if (message.equals(encoded_deleted_already_msg)) {
            byte[] data = Base64.decode(modelGroupChat.getMessage().trim(), Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            holder.user_txt_msg.setVisibility(View.VISIBLE);
            holder.user_txt_msg.setText(text);
            holder.user_img_msg.setVisibility(View.GONE);
            holder.user_img_msg_layout.setVisibility(View.GONE);
            holder.user_doc_msg.setVisibility(View.GONE);
            holder.user_doc_msg_layout.setVisibility(View.GONE);
            holder.user_video_msg_layout.setVisibility(View.GONE);
            holder.feeling.setVisibility(View.GONE);
            holder.emoji_card.setVisibility(View.GONE);
        }

        // Delete Text, Image & Documents messages...
        holder.user_img_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.msg_delete_reaction_dialog);

                MaterialCardView emoji_1 = dialog.findViewById(R.id.emoji_1);
                MaterialCardView emoji_2 = dialog.findViewById(R.id.emoji_2);
                MaterialCardView emoji_3 = dialog.findViewById(R.id.emoji_3);
                MaterialCardView emoji_4 = dialog.findViewById(R.id.emoji_4);
                MaterialCardView emoji_5 = dialog.findViewById(R.id.emoji_5);
                MaterialCardView emoji_6 = dialog.findViewById(R.id.emoji_6);

                RelativeLayout delete = dialog.findViewById(R.id.delete);
                LinearLayout reaction_layout = dialog.findViewById(R.id.reaction_layout);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
                                            if (msg.equals(encoded_deleted_already_msg)) { // This message was deleted
                                                ds.getRef().removeValue();
                                            } else {
                                                //image delete from the firebase storageno
                                                byte[] data = Base64.decode(msg, Base64.DEFAULT);
                                                String text = new String(data, StandardCharsets.UTF_8);
                                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(text);
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //image deleted from the firebase storage
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("message", encoded_deleted_already_msg); // This message was deleted
                                                hashMap.put("feeling", -1);
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
                    }
                });

                if (message.equals(encoded_deleted_already_msg)) {
                    reaction_layout.setVisibility(View.GONE);
                } else {
                    emoji_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 0);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 1);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);

                        }
                    });
                    emoji_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 2);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 3);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 4);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 5);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                }

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


                return false;
            }
        });
        holder.user_img_msg_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.msg_delete_reaction_dialog);

                MaterialCardView emoji_1 = dialog.findViewById(R.id.emoji_1);
                MaterialCardView emoji_2 = dialog.findViewById(R.id.emoji_2);
                MaterialCardView emoji_3 = dialog.findViewById(R.id.emoji_3);
                MaterialCardView emoji_4 = dialog.findViewById(R.id.emoji_4);
                MaterialCardView emoji_5 = dialog.findViewById(R.id.emoji_5);
                MaterialCardView emoji_6 = dialog.findViewById(R.id.emoji_6);

                RelativeLayout delete = dialog.findViewById(R.id.delete);
                LinearLayout reaction_layout = dialog.findViewById(R.id.reaction_layout);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
                                            if (msg.equals(encoded_deleted_already_msg)) {  // This message was deleted
                                                ds.getRef().removeValue();
                                            } else {
                                                //image delete from the firebase storage
                                                byte[] data = Base64.decode(msg, Base64.DEFAULT);
                                                String text = new String(data, StandardCharsets.UTF_8);
                                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(text);
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //image deleted from the firebase storage
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("message", encoded_deleted_already_msg);  // This message was deleted
                                                hashMap.put("feeling", -1);
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
                    }
                });

                if (message.equals(encoded_deleted_already_msg)) {
                    reaction_layout.setVisibility(View.GONE);
                } else {
                    emoji_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 0);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 1);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);

                        }
                    });
                    emoji_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 2);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 3);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 4);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 5);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                }

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


                return false;
            }
        });
        holder.user_doc_msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.msg_delete_reaction_dialog);

                MaterialCardView emoji_1 = dialog.findViewById(R.id.emoji_1);
                MaterialCardView emoji_2 = dialog.findViewById(R.id.emoji_2);
                MaterialCardView emoji_3 = dialog.findViewById(R.id.emoji_3);
                MaterialCardView emoji_4 = dialog.findViewById(R.id.emoji_4);
                MaterialCardView emoji_5 = dialog.findViewById(R.id.emoji_5);
                MaterialCardView emoji_6 = dialog.findViewById(R.id.emoji_6);

                RelativeLayout delete = dialog.findViewById(R.id.delete);
                LinearLayout reaction_layout = dialog.findViewById(R.id.reaction_layout);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
                                            if (msg.equals(encoded_deleted_already_msg)) {  // This message was deleted
                                                ds.getRef().removeValue();
                                            } else {
                                                //document delete from the firebase storage
                                                byte[] data = Base64.decode(msg, Base64.DEFAULT);
                                                String text = new String(data, StandardCharsets.UTF_8);
                                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(text);
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //document deleted from the firebase storage
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("message", encoded_deleted_already_msg);  // This message was deleted
                                                hashMap.put("feeling", -1);
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
                    }
                });

                if (message.equals(encoded_deleted_already_msg)) {
                    reaction_layout.setVisibility(View.GONE);
                } else {
                    emoji_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 0);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 1);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);

                        }
                    });
                    emoji_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 2);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 3);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 4);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 5);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                }

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


                return false;
            }
        });
        holder.user_doc_msg_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.msg_delete_reaction_dialog);

                MaterialCardView emoji_1 = dialog.findViewById(R.id.emoji_1);
                MaterialCardView emoji_2 = dialog.findViewById(R.id.emoji_2);
                MaterialCardView emoji_3 = dialog.findViewById(R.id.emoji_3);
                MaterialCardView emoji_4 = dialog.findViewById(R.id.emoji_4);
                MaterialCardView emoji_5 = dialog.findViewById(R.id.emoji_5);
                MaterialCardView emoji_6 = dialog.findViewById(R.id.emoji_6);

                RelativeLayout delete = dialog.findViewById(R.id.delete);
                LinearLayout reaction_layout = dialog.findViewById(R.id.reaction_layout);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
                                            if (msg.equals(encoded_deleted_already_msg)) {  // This message was deleted
                                                ds.getRef().removeValue();
                                            } else {
                                                //document delete from the firebase storage
                                                byte[] data = Base64.decode(msg, Base64.DEFAULT);
                                                String text = new String(data, StandardCharsets.UTF_8);
                                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(text);
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //document deleted from the firebase storage
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("message", encoded_deleted_already_msg);  // This message was deleted
                                                hashMap.put("feeling", -1);
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
                    }
                });

                if (message.equals(encoded_deleted_already_msg)) {
                    reaction_layout.setVisibility(View.GONE);
                } else {
                    emoji_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 0);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 1);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);

                        }
                    });
                    emoji_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 2);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 3);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 4);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 5);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                }

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


                return false;
            }
        });
        holder.outer_message_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.msg_delete_reaction_dialog);

                MaterialCardView emoji_1 = dialog.findViewById(R.id.emoji_1);
                MaterialCardView emoji_2 = dialog.findViewById(R.id.emoji_2);
                MaterialCardView emoji_3 = dialog.findViewById(R.id.emoji_3);
                MaterialCardView emoji_4 = dialog.findViewById(R.id.emoji_4);
                MaterialCardView emoji_5 = dialog.findViewById(R.id.emoji_5);
                MaterialCardView emoji_6 = dialog.findViewById(R.id.emoji_6);

                RelativeLayout delete = dialog.findViewById(R.id.delete);
                LinearLayout reaction_layout = dialog.findViewById(R.id.reaction_layout);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

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
                                            if (msg.equals(encoded_deleted_already_msg)) {  // This message was deleted
                                                ds.getRef().removeValue();
                                            } else {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("message", encoded_deleted_already_msg);
                                                hashMap.put("feeling", -1);
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
                    }
                });

                if (message.equals(encoded_deleted_already_msg)) {
                    reaction_layout.setVisibility(View.GONE);
                } else {
                    emoji_1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 0);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 1);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);

                        }
                    });
                    emoji_3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 2);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 3);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 4);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                    emoji_6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Map<String, Object> map = new HashMap<>();
                            map.put("feeling", 5);
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                            dbref.child("Messages").child(timestamp).updateChildren(map);
                        }
                    });
                }

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


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
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String name = "" + ds.child("name").getValue();
                        String groupIcon = "" + ds.child("profile_image").getValue();
                        holder.user_name.setText(name);
                        try {
                            Glide.with(holder.user_profile_img).load(groupIcon).into(holder.user_profile_img);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    holder.user_name.setVisibility(View.GONE);
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
        RelativeLayout user_img_msg_layout, user_doc_msg_layout, user_video_msg_layout;
        TextView user_name;
        TextView user_txt_msg;
        RoundedImageView user_img_msg, user_doc_msg, user_video_msg;
        MaterialCardView emoji_card;
        ImageView feeling;

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
            user_video_msg = itemView.findViewById(R.id.single_user_video_msg);
            user_video_msg_layout = itemView.findViewById(R.id.single_user_video_msg_layout);
            emoji_card = itemView.findViewById(R.id.emoji_reaction_card);
            feeling = itemView.findViewById(R.id.feeling);
        }
    }
}
