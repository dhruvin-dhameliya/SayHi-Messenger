package com.group_project.chatapplication.singleChat.single_chat_messages;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

public class Single_Chat_Doc_WebView_Activity extends AppCompatActivity {

    WebView webview_document;
    String document, senderid;
    ImageView document_download, back_to_single_chat;
    ProgressDialog progDailog;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_doc_web_view);

        webview_document = findViewById(R.id.webview_document);
        back_to_single_chat = findViewById(R.id.back_to_single_chat);
        document_download = findViewById(R.id.document_download);

        back_to_single_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        document = intent.getStringExtra("pass_pdf_url");
        senderid = intent.getStringExtra("sender");

        webview_document.setWebViewClient(new WebViewClient());
        webview_document.getSettings().setBuiltInZoomControls(true);
        webview_document.getSettings().setJavaScriptEnabled(true);
        webview_document.getSettings().setLoadWithOverviewMode(true);
        webview_document.getSettings().setUseWideViewPort(true);

        progDailog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        progDailog.setCancelable(false);

        webview_document.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });

        try {
            url = ("https://docs.google.com/gview?embedded=true&url=") + URLEncoder.encode(document, "ISO-8859-1");
            webview_document.loadUrl(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String myuid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+91", "");
        if (senderid.equals(myuid)) {
            document_download.setVisibility(GONE);
        } else {
            document_download.setVisibility(VISIBLE);
        }

        document_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(document));
                startActivity(intent);
            }
        });
    }
}