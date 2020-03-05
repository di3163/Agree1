package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static MailTask mailTask;
    private RelativeLayout activitiMain;
    ListView listOfMessages;
    private TextView textViewInfo;
    AsTask asTask;
    static List<MessageAgree> messageAgreeList;
    static List<FilesFromMail> listFilesFromMail = new ArrayList<>();
    private ImageView stop, ok, wait, ab;
    static String infoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activitiMain = findViewById(R.id.activityMain);
        textViewInfo = findViewById(R.id.text_info);
        listOfMessages = findViewById(R.id.listMess);
        listOfMessages.setClickable(true);
        mailTask = new MailTask(this);
        mailTask.loadSettings();
        displayAllMessages();
        infoString = "";
        startService(new Intent(this, MailService.class));
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                asTask = new AsTask();
//                asTask.execute();
//            }
//        },0, 300000);


        listOfMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.popup_choose_window, null);
                stop = (ImageView) popupView.findViewById(R.id.stop);
                ok = (ImageView) popupView.findViewById(R.id.ok);
                wait = (ImageView) popupView.findViewById(R.id.wait);
                ab = (ImageView) popupView.findViewById(R.id.ab);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final int pos = position;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                int x = (int)view.getX();
                int y = (int)view.getY();
                //popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
                popupWindow.showAsDropDown(view);
                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(pos);
                        messageAgree.setAgrStat(messageAgree.getAgrNumInMail());
                        popupWindow.dismiss();
                        displayAllMessages();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(pos);
                        messageAgree.setAgrStat("+");
                        popupWindow.dismiss();
                        displayAllMessages();
                    }
                });
                wait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(pos);
                        messageAgree.setAgrStat("*");
                        popupWindow.dismiss();
                        displayAllMessages();
                    }
                });
                ab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(pos);
                        messageAgree.setAgrStat("/");
                        popupWindow.dismiss();
                        displayAllMessages();
                    }
                });
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                return true;
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
            infoString = "Список сообщений обновлен " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            textViewInfo.setText(infoString);
            displayAllMessages();
        }
    }

    private void displayAllMessages(){
        messageAgreeList = mailTask.getMessages();
        if(messageAgreeList.size() >0 ) {
            listOfMessages.setAdapter(new AgArrayAdapter(this, messageAgreeList));
        }
    }

    @Override
    protected void onDestroy() {
        mailTask.saveSettings();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        textViewInfo.setText(infoString);
        super.onRestart();
    }

    protected void onResume() {
        textViewInfo.setText(infoString);
        super.onResume();
    }
}
