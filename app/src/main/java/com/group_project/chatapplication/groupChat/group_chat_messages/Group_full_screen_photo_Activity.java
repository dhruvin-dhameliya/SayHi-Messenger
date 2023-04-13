package com.group_project.chatapplication.groupChat.group_chat_messages;

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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.group_project.chatapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Group_full_screen_photo_Activity extends AppCompatActivity {

    ImageView back_group_msg_page, get_image, img_download;
    String image, senderid;
    Bitmap bitmap;
    BitmapDrawable bitmapDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_full_screen_photo);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        back_group_msg_page = findViewById(R.id.back_group_msg_page);
        get_image = findViewById(R.id.full_screen_img);
        img_download = findViewById(R.id.download_group_receiver_img);
        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        senderid = intent.getStringExtra("sender");
        try {
            Glide.with(this).load(image).into(get_image);
        }catch (Exception e){
            e.printStackTrace();
        }


        String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (senderid.equals(myuid)) {
            img_download.setVisibility(View.GONE);
        } else {
            img_download.setVisibility(View.VISIBLE);
        }

        back_group_msg_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable = (BitmapDrawable) get_image.getDrawable();
                bitmap = bitmapDrawable.getBitmap();
                FileOutputStream fileOutputStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File Directory = new File(sdCard.getAbsolutePath() + "/Pictures/Say Hi");
                Directory.mkdir();

                String filename = String.format("IMG-" + System.currentTimeMillis() + ".jpg", System.currentTimeMillis());
                File outfile = new File(Directory, filename);
                Toast.makeText(Group_full_screen_photo_Activity.this, "Download Successfully", Toast.LENGTH_SHORT).show();
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