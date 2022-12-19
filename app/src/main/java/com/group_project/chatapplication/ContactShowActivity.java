package com.group_project.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactShowActivity extends AppCompatActivity {

    RecyclerView recycler_view;
    ArrayList<Model_Contact> arrayList = new ArrayList<Model_Contact>();
    Adapter_Contact adapter;
//    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_show);


        recycler_view = findViewById(R.id.recycler_view);

//        database = FirebaseDatabase.getInstance();

        checkPermission();

    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ContactShowActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContactShowActivity.this, new String[]{
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
                            phoneCursor.getString(phoneCursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME )))
                    );

                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_Contact(this, arrayList);
        recycler_view.setAdapter(adapter);

/*
        database.getReference().updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
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
*/

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