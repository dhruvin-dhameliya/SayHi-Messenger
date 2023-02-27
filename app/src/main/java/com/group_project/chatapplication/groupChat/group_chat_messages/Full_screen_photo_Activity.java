package com.group_project.chatapplication.groupChat.group_chat_messages;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.group_project.chatapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Full_screen_photo_Activity extends AppCompatActivity {
    ImageView get_image, img_download;
    String image, senderid;
    Bitmap bitmap;
    BitmapDrawable bitmapDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);

        get_image = findViewById(R.id.image3);
        img_download = findViewById(R.id.download_img);
        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        senderid = intent.getStringExtra("sender");
        Glide.with(this).load(image).into(get_image);

        String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (senderid.equals(myuid)) {
            img_download.setVisibility(View.GONE);
        } else {
            img_download.setVisibility(View.VISIBLE);
        }

        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable) get_image.getDrawable();
                bitmap = bitmapDrawable.getBitmap();
                FileOutputStream fileOutputStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File Directory = new File(sdCard.getAbsolutePath() + "/Download");
                Directory.mkdir();

                String filename = String.format("Say_Hi.jpg", System.currentTimeMillis());
                File outfile = new File(Directory, filename);
                Toast.makeText(Full_screen_photo_Activity.this, "Download", Toast.LENGTH_SHORT).show();
                try {
                    fileOutputStream = new FileOutputStream(outfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent2.setData(Uri.fromFile(outfile));
                    sendBroadcast(intent2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}