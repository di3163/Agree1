package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MailTask mailTask;
    private RelativeLayout activitiMain;
    ListView listOfMessages;
    private TextView textViewInfo;
    AsTask asTask;
    static List<MessageAgree> messageAgreeList;
    static List<FilesFromMail> listFilesFromMail = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activitiMain = findViewById(R.id.activityMain);
        textViewInfo = findViewById(R.id.text_info);
        listOfMessages = findViewById(R.id.listMess);
        listOfMessages.setClickable(true);
        mailTask = new MailTask(this);


        listOfMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(position);
                messageAgree.setAgr(true);
                displayAllMessages();
                return messageAgree.isAgr();
            }
        });

        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object o = listOfMessages.getItemAtPosition(position);
                MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
                intent.putExtra("messageID", messageAgree.getId());
                //intent.putExtra("keyName", list.get(i).getFileName());
                startActivity(intent);
            }
        });

        displayAllMessages();
    }



    public void onButtonClic(View v){
        asTask = new AsTask();
        asTask.execute();
    }


    class AsTask extends AsyncTask<Void,  Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            mailTask.refreshListMessages();
            return null;
        }
        protected void onPostExecute(String result){
            textViewInfo.setText("Список сообщений обновлен");
            displayAllMessages();
        }
    }

    private void displayAllMessages(){
        messageAgreeList = mailTask.getMessages();
        if(messageAgreeList.size() >0 ) {
            listOfMessages.setAdapter(new AgArrayAdapter(this, messageAgreeList));
        }
    }

}
