package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FileChooseActivity extends AppCompatActivity {
    private List<String> currentMessageFilesList = new ArrayList<>();
    ListView listOfFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_choose);
        listOfFiles = findViewById(R.id.listFiles);
        String messageID = getIntent().getStringExtra("messageID");

        for (FilesFromMail currentFile : MainActivity.listFilesFromMail){
            if(currentFile.getiD().equals(messageID))
                currentMessageFilesList.add(currentFile.getFileName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_file, R.id.textFile, currentMessageFilesList);
        listOfFiles.setAdapter(adapter);

        listOfFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FileChooseActivity.this, PDFActivity.class);
                intent.putExtra("fileName", currentMessageFilesList.get(i));
//                intent.putExtra("keyName", list.get(i).getFileName());
//                intent.putExtra("fileName", list.get(i).getFilePath());
                startActivity(intent);
            }
        });
    }



}
