package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileChooseActivity extends AppCompatActivity {
    private List<String> currentFilesList;
    ListView listOfFiles;
    //WebView webViewHTML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_choose);
        listOfFiles = findViewById(R.id.listFiles);
//        webViewHTML = (WebView) findViewById(R.id.text_html);
//        WebSettings webSettings = webViewHTML.getSettings();
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);

        String messageID = getIntent().getStringExtra("messageID");

        if(messageID.equals("ServiseTasks")){
            currentFilesList = getFilesFromDir(getFilesDir().toString());
        }else {
//            String htmlString = "";
//            MessageAgree messageAgree = MainActivity.mailTask.getMessageAgreeForId(messageID);
//            if (messageAgree != null){
//                htmlString = messageAgree.getHtmlContent();
//            }
//
//            webViewHTML.loadDataWithBaseURL(null, htmlString, "text/html", "windows-1251", null);
//            webViewHTML.setInitialScale(95);

            currentFilesList = new ArrayList<>();
            for (FilesFromMail currentFile : MainActivity.listFilesFromMail) {
                if (currentFile.getiD().equals(messageID))
                    currentFilesList.add(currentFile.getFileName());
            }
        }

        listOfFiles.setAdapter(new FileArrayAdapter(this, currentFilesList));


        listOfFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentFilesList.get(i).contains("log.dat")){
                    Intent intent = new Intent(FileChooseActivity.this, LogActivity.class);
                    intent.putExtra("fileName", currentFilesList.get(i));
                    startActivity(intent);
                }else if(currentFilesList.get(i).contains(".pdf")) {
                    Intent intent = new Intent(FileChooseActivity.this, PDFActivity.class);
                    intent.putExtra("fileName", currentFilesList.get(i));
//                intent.putExtra("keyName", list.get(i).getFileName());
//                intent.putExtra("fileName", list.get(i).getFilePath());
                    startActivity(intent);
                }
            }
        });
    }

    private List<String> getFilesFromDir(String pathname){
        File folder = new File(pathname);
        List<String> listFiles = new ArrayList<>();
        ArrayList<File> listTemp = new ArrayList<File>(Arrays.asList(folder.listFiles()));
        for (File entryFile : listTemp){
            if (entryFile.isFile()) listFiles.add(entryFile.getName());
        }
        return listFiles;
    }
}
