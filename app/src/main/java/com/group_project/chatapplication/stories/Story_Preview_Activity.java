package com.group_project.chatapplication.stories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.R;

public class Story_Preview_Activity extends AppCompatActivity {

    MaterialButton btn_screen_close, btn_crop_img, btn_choose_color;
    RelativeLayout final_story_img_layout;
    ImageView story_preview_img;
    CardView story_caption_card, edit_txt_caption_cardView;
    TextView txt_story_caption;
    EditText edit_txt_caption;
    FloatingActionButton btn_upload_story;
    String fetch_caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_preview);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.black));

        btn_screen_close = findViewById(R.id.btn_screen_close);
        btn_crop_img = findViewById(R.id.btn_crop_img);
        btn_choose_color = findViewById(R.id.btn_choose_color);
        final_story_img_layout = findViewById(R.id.final_story_img_layout);
        story_preview_img = findViewById(R.id.story_preview_img);
        story_caption_card = findViewById(R.id.story_caption_card);
        edit_txt_caption_cardView = findViewById(R.id.edit_txt_caption_cardView);
        txt_story_caption = findViewById(R.id.txt_story_caption);
        edit_txt_caption = findViewById(R.id.edit_txt_caption);
        btn_upload_story = findViewById(R.id.btn_upload_story);

        // close preview screen code...
        btn_screen_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Story_Preview_Activity.this, MainActivity.class));
                finish();
            }
        });

        // Close KeyBord when press DONE button of key-bord...
        edit_txt_caption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    fetch_caption = edit_txt_caption.getText().toString();
                    txt_story_caption.setText(fetch_caption);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    edit_txt_caption_cardView.setVisibility(View.GONE);
                    story_caption_card.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        // Close KeyBord when touch on screen...
        edit_txt_caption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    fetch_caption = edit_txt_caption.getText().toString();
                    txt_story_caption.setText(fetch_caption);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    edit_txt_caption_cardView.setVisibility(View.GONE);
                    story_caption_card.setVisibility(View.VISIBLE);
                }
            }
        });

        // Show KeyBord when touch on Text-Story-Caption...
        story_caption_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_story_caption.setText(fetch_caption);
                story_caption_card.setVisibility(View.GONE);
                edit_txt_caption_cardView.setVisibility(View.VISIBLE);

                edit_txt_caption.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edit_txt_caption, InputMethodManager.SHOW_IMPLICIT);
            }
        });

    }
}