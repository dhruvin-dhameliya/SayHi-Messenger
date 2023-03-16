package com.group_project.chatapplication.groupChat.group_chat_messages;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.commonActivities.Image_Preview_Activity;
import com.group_project.chatapplication.contacts.ContactDTO;
import com.group_project.chatapplication.groupChat.add_participant.Adapter_Group_Contact;
import com.group_project.chatapplication.groupChat.add_participant.Group_Contacts_Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Group_Info_Activity extends AppCompatActivity {

    CircleImageView groupIconIv;
    ImageButton editGroupTv;
    TextView group_name, createdBy, descriptionTv, participantTv, txt_leave, jump_group_chat_iner_txt, jump_add_participent_iner_txt;
    LinearLayout participantaddTv, leaveGroupTv, jump_group_chat;
    RelativeLayout jump_add_participent_iner, jump_group_chat_iner;
    RecyclerView participantsRv;

    String groupId, fetch_phone_number, groupIcon, groupTitle;
    String myGroupPost = "";
    FirebaseAuth firebaseAuth;
    ArrayList<ContactDTO> userModelArrayList;
    Adapter_Group_Contact contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        groupIconIv = findViewById(R.id.groupIconIv);
        descriptionTv = findViewById(R.id.descriptionTv);
        group_name = findViewById(R.id.group_name);
        createdBy = findViewById(R.id.createdBy);
        editGroupTv = findViewById(R.id.editGroupTv);
        participantaddTv = findViewById(R.id.participantaddTv);
        jump_add_participent_iner = findViewById(R.id.jump_add_participent_iner);
        jump_group_chat_iner_txt = findViewById(R.id.jump_group_chat_iner_txt);
        jump_group_chat = findViewById(R.id.jump_group_chat);
        jump_add_participent_iner_txt = findViewById(R.id.jump_add_participent_iner_txt);
        jump_group_chat_iner = findViewById(R.id.jump_group_chat_iner);
        participantTv = findViewById(R.id.participantTv);
        leaveGroupTv = findViewById(R.id.leaveGroupTv);
        participantsRv = findViewById(R.id.participantsRv);
        txt_leave = findViewById(R.id.txt_leave);

        groupId = getIntent().getStringExtra("groupId");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();

        loadGroupInfo();
        loadGroupPost();

        groupIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentId = new Intent(Group_Info_Activity.this, Image_Preview_Activity.class);
                intentId.putExtra("passSelectedImage", groupIcon);
                intentId.putExtra("pass_current_name", groupTitle.trim());
                startActivity(intentId);
            }
        });

        jump_group_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        jump_group_chat_iner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        jump_group_chat_iner_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        jump_add_participent_iner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Group_Info_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else if (ContextCompat.checkSelfPermission(Group_Info_Activity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Group_Info_Activity.this, Group_Contacts_Activity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            }
        });

        jump_add_participent_iner_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Group_Info_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else if (ContextCompat.checkSelfPermission(Group_Info_Activity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Group_Info_Activity.this, Group_Contacts_Activity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            }
        });

        participantaddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Group_Info_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else if (ContextCompat.checkSelfPermission(Group_Info_Activity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Group_Info_Activity.this, Group_Contacts_Activity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            }
        });

        txt_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTitle = "";
                String dialogDescription = "";
                String positiveButtonTitle = "";
                if (myGroupPost.equals("creator")) {
                    dialogTitle = "Delete Group";
                    dialogDescription = "Are you sure you want to Delete group permanently";
                    positiveButtonTitle = "DELETE";
                } else {
                    dialogTitle = "Leave Group";
                    dialogDescription = "Are you sure you want to Leave group permanently";
                    positiveButtonTitle = "LEAVE";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Group_Info_Activity.this);
                builder.setTitle(dialogTitle).setMessage(dialogDescription).setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (myGroupPost.equals("creator")) {
                            //I am creator
                            deletegroup();
                        } else {
                            leaveGroup();
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        leaveGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTitle = "";
                String dialogDescription = "";
                String positiveButtonTitle = "";
                if (myGroupPost.equals("creator")) {
                    dialogTitle = "Delete Group";
                    dialogDescription = "Are you sure you want to Delete group permanently";
                    positiveButtonTitle = "DELETE";
                } else {
                    dialogTitle = "Leave Group";
                    dialogDescription = "Are you sure you want to Leave group permanently";
                    positiveButtonTitle = "LEAVE";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Group_Info_Activity.this);
                builder.setTitle(dialogTitle).setMessage(dialogDescription).setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (myGroupPost.equals("creator")) {
                            //I am creator
                            deletegroup();
                        } else {
                            leaveGroup();
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        editGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Group_Info_Activity.this, Group_Edit_Activity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

    }

    private void leaveGroup() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(fetch_phone_number).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //group left
                Toast.makeText(Group_Info_Activity.this, "You Left This Group", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Group_Info_Activity.this, MainActivity.class));
                finishAffinity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                Toast.makeText(Group_Info_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletegroup() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //group deleted
                Toast.makeText(Group_Info_Activity.this, "Group is Deleted successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Group_Info_Activity.this, MainActivity.class));
                finishAffinity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                Toast.makeText(Group_Info_Activity.this, "Filed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").orderByChild("uid").equalTo(fetch_phone_number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    myGroupPost = "" + ds.child("post").getValue();

                    if (myGroupPost.equals("participant")) {
                        editGroupTv.setVisibility(View.GONE);
                        participantaddTv.setVisibility(View.GONE);
                        txt_leave.setText("Leave Group");
                    } else if (myGroupPost.equals("creator")) {
                        editGroupTv.setVisibility(View.VISIBLE);
                        participantaddTv.setVisibility(View.VISIBLE);
                        txt_leave.setText("Delete Group");
                    }
                }
                loadparticipant();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupId = "" + ds.child("groupId").getValue();
                    groupTitle = "" + ds.child("groupTitle").getValue();
                    String groupDescription = "" + ds.child("groupDescription").getValue();
                    groupIcon = "" + ds.child("groupIcon").getValue();
                    String createdBy = "" + ds.child("createBy").getValue();
                    String timestamp = "" + ds.child("timestamp").getValue();

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(timestamp));
                    String dateTime = DateFormat.format("dd/MM/yyyy hh : mm aa", cal).toString();

                    loadCreatorInfo(dateTime, createdBy);
                    descriptionTv.setText(groupDescription);
                    group_name.setText(groupTitle);

                    try {
                        Glide.with(groupIconIv).load(groupIcon).into(groupIconIv);

                    } catch (Exception e) {
                        groupIconIv.setImageResource(R.drawable.img_default_person);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCreatorInfo(String dateTime, String createBy) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Details");
        reference.orderByChild("phone").equalTo(createBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    createdBy.setText("Created By " + name + " on " + dateTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadparticipant() {
        userModelArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModelArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get uid from group -> Participants

                    ContactDTO model = ds.getValue(ContactDTO.class);
                    userModelArrayList.add(model);

                    contactAdapter = new Adapter_Group_Contact(userModelArrayList, Group_Info_Activity.this, groupId);
                    participantsRv.setAdapter(contactAdapter);
                    participantTv.setText(userModelArrayList.size() + " PARTICIPANTS");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Denied...", Toast.LENGTH_SHORT).show();
        }
    }
}