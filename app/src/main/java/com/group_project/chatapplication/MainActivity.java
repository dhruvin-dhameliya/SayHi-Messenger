package com.group_project.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_project.chatapplication.userSettings.Profile_Activity;
import com.group_project.chatapplication.groupChat.group_list.Fragment_Groups;
import com.group_project.chatapplication.contacts.Contact_Show_Activity;
import com.group_project.chatapplication.groupChat.group_list.Create_New_Group_Activity;
import com.group_project.chatapplication.registration.Registration_Activity;
import com.group_project.chatapplication.singleChat.single_chat_list.Fragment_Chats;
import com.group_project.chatapplication.contacts.Fragment_Contacts;
import com.group_project.chatapplication.stories.Fragment_Stories;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Toolbar mTollbar;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String fetch_phone_number;
    BottomNavigationView bottomNavbar;
    FloatingActionButton jump_chat_screen;
    CircleImageView home_profile_image;
    Dialog dialog;
    Fragment_Chats fragment_chats = new Fragment_Chats();
    Fragment_Stories fragment_stories = new Fragment_Stories();
    Fragment_Contacts fragment_contacts = new Fragment_Contacts();
    Fragment_Groups fragment_groups = new Fragment_Groups();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(MainActivity.this, Registration_Activity.class));
            finish();
        }

        jump_chat_screen = findViewById(R.id.jump_chat_screen);
        jump_chat_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, Contact_Show_Activity.class));
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, Contact_Show_Activity.class));
                }
            }
        });

        home_profile_image = findViewById(R.id.home_profile_image);
        home_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile_Activity.class));
            }
        });

        bottomNavbar = findViewById(R.id.bottomNavbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_chats).commit();

        bottomNavbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_chats).commit();
                        jump_chat_screen.show();
                        break;
                    case R.id.stories:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_stories).commit();
                        jump_chat_screen.show();
                        break;

                    case R.id.groups:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_groups).commit();
                        jump_chat_screen.show();
                        break;

                    case R.id.contacts:
                       /* getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_contacts).commit();
                        jump_chat_screen.hide();
                        break;*/

                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            jump_chat_screen.hide();
                        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_contacts).commit();
                            jump_chat_screen.hide();
                        }
                        break;
                }
                return true;
            }
        });

        mTollbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mTollbar);
        getSupportActionBar().setTitle("Say Hi");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();
        checkPermission();

        dialog = new Dialog(this, R.style.NewDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);
        dialog.show();

        // Show profile image on home screen
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Details").child(fetch_phone_number);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("phone"))) {
                    String retrieveProfileImage = snapshot.child("profile_image").getValue().toString();
                    try {
                        Glide.with(getApplicationContext()).load(retrieveProfileImage).into(home_profile_image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Profile image not get!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.option_create_group) {
            startActivity(new Intent(MainActivity.this, Create_New_Group_Activity.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_chats).commit();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied...", Toast.LENGTH_SHORT).show();
        }
    }

}