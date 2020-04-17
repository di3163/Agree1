package com.example.agree;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TableHTMLActivity extends AppCompatActivity {

    WebView webViewHTML;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_html_table);
        webViewHTML = (WebView) findViewById(R.id.text_html);
        WebSettings webSettings = webViewHTML.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        String messageID = getIntent().getStringExtra("messageID");
        String htmlString = "";
        MessageAgree messageAgree = MainActivity.mailTask.getMessageAgreeForId(messageID);
        if (messageAgree != null){
            htmlString = messageAgree.getHtmlContent();
        }

        webViewHTML.loadDataWithBaseURL(null, htmlString, "text/html", "windows-1251", null);
        webViewHTML.setInitialScale(95);

    }
}
