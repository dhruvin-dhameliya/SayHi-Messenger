package com.group_project.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Toolbar mTollbar;
    ViewPager myViewPager;
    TabLayout myTabLayout;
    TabAccessorAdapter myTabAccessorAdapter;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();
        }

        mTollbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mTollbar);
        getSupportActionBar().setTitle("Say Hi");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
            RequestNewGroup();
        }
        if (item.getItemId() == R.id.option_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        }
        if (item.getItemId() == R.id.option_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();
        }
        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, com.google.android.material.R.style.Base_ThemeOverlay_Material3_Dialog);
        builder.setTitle("New group");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Type group subject here...");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(getApplicationContext(), "Please write Group name", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String groupName) {
        databaseReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), groupName + " is created..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}