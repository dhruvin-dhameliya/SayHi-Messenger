package com.group_project.chatapplication.groupChat.group_chat_messages;

import static android.view.View.*;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.group_project.chatapplication.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Group_Doc_WebView_Activty extends AppCompatActivity {

    WebView webView;
    String document, senderid;
    ImageView doc_download, back_to_group_chat;
    ProgressDialog progressDialog;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_doc_web_view_activty);

        webView = findViewById(R.id.webview_pdf);
        doc_download = findViewById(R.id.doc_download);
        back_to_group_chat = findViewById(R.id.back_to_group_chat);

        back_to_group_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        document = intent.getStringExtra("pass_pdf_url");
        senderid = intent.getStringExtra("sender");

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        progressDialog.setCancelable(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progressDialog.dismiss();
            }
        });

        try {
            url = "https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(document, "ISO-8859-1");
            webView.loadUrl(url);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (senderid.equals(myuid)) {
            doc_download.setVisibility(GONE);
        } else {
            doc_download.setVisibility(VISIBLE);
        }

        doc_download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(document));
                startActivity(intent);
            }
        });
    }

}