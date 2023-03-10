package com.group_project.chatapplication.singleChat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.group_project.chatapplication.R;
import com.group_project.chatapplication.groupChat.group_chat_messages.Full_screen_photo_Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Single_Chat_full_screen_photo_Activity extends AppCompatActivity {

    ImageView download_receiver_img, full_img;
    String image, senderid;
    Bitmap bitmap;
    BitmapDrawable bitmapDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_full_screen_photo);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        download_receiver_img = findViewById(R.id.download_receiver_img);
        full_img = findViewById(R.id.full_img);

        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        senderid = intent.getStringExtra("sender");
        Glide.with(full_img.getContext()).load(image).into(full_img);

        String myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+91", "");
        if (senderid.equals(myuid)) {
            download_receiver_img.setVisibility(View.GONE);
        } else {
            download_receiver_img.setVisibility(View.VISIBLE);
        }

// henil
        download_receiver_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable) download_receiver_img.getDrawable();
                bitmap = bitmapDrawable.getBitmap();
                FileOutputStream fileOutputStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File Directory = new File(sdCard.getAbsolutePath() + "/Download");
                Directory.mkdir();

                String filename = String.format("Say_Hi.jpg", System.currentTimeMillis());
                File outfile = new File(Directory, filename);
                Toast.makeText(Single_Chat_full_screen_photo_Activity.this, "Download", Toast.LENGTH_SHORT).show();
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