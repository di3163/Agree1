package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MailTask mailTask;
    private RelativeLayout activitiMain;
    private ListView listMess;
    private TextView textViewInfo;
    private Button buttonGetMList;
    AsTask asTask;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activitiMain = findViewById(R.id.activityMain);
        textViewInfo = findViewById(R.id.text_info);
        mailTask = new MailTask();
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
        List<MessageAgree> values = mailTask.getMessages();
        if(values.size() >0 ) {
            ListView listOfMessages = findViewById(R.id.listMess);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.str_mess, R.id.textView, values);
//            listOfMessages.setAdapter(adapter);
            listOfMessages.setAdapter(new AgArrayAdapter(this, values));
        }
    }
}
