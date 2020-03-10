package com.example.agree;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class LogActivity extends AppCompatActivity {

    private String path;
    private String fileDir;
    //private TextView textV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_file_cont);
        TextView textV = findViewById(R.id.text_log);
        fileDir =  getFilesDir().toString();
        path = fileDir +"/"+ getIntent().getStringExtra("fileName");
        StringBuilder strFile = new StringBuilder();
        try {
            Scanner scan = new Scanner(new File(path));
            scan.useLocale(Locale.ENGLISH);
            scan.useDelimiter("\n");
            while(scan.hasNextLine()) {
                strFile.append(scan.nextLine());
                strFile.append("\n");
            }
            scan.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        textV.setText(strFile.toString());
    }
}
