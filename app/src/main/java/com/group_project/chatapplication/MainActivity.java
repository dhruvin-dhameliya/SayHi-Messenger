package com.group_project.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Toolbar mTollbar;
    ViewPager myViewPager;
    TabLayout myTabLayout;
    TabAccessorAdapter myTabAccessorAdapter;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String fetch_phone_number;

    ArrayList<Model_Contact> arrayList = new ArrayList<Model_Contact>();
    FirebaseDatabase database;


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
        database = FirebaseDatabase.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        fetch_phone_number = user.getPhoneNumber();
        checkPermission();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC";
        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        HashMap map = new HashMap();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);

                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Model_Contact model_contact = new Model_Contact();
                    model_contact.setContact_name(name);
                    model_contact.setContact_number(number);
                    arrayList.add(model_contact);

                    map.put(
                            phoneCursor.getString(phoneCursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.NUMBER))),
                            phoneCursor.getString(phoneCursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
                    );

                    phoneCursor.close();
                }
            }
            cursor.close();
        }

        database.getReference().child("Contacts").child(fetch_phone_number).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fail!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied.!", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }

}