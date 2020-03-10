package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
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
    //List<MessageAgree> messageAgreeList;
    static List<FilesFromMail> listFilesFromMail = new ArrayList<>();
    private ImageView stop, ok, wait, ab;
    static String infoString;
    Timer mTimer = new Timer();
    final Handler uiHandler = new Handler();

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
        infoString = "";

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mailTask.refreshListMessages();
                infoString = "Список сообщений обновлен " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewInfo.setText(infoString);
                        displayAllMessages();
                    }
                });
            }
        }, 0L, 60L * 5000);

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
                MessageAgree messageAgree = (MessageAgree)listOfMessages.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
                intent.putExtra("messageID", messageAgree.getId());
                startActivity(intent);
            }
        });
    }

    public void onButtonClic(View v){
        asTask = new AsTask();
        asTask.execute();
    }

    public void onButtonFilesClic(View v){
        Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
        intent.putExtra("messageID", "ServiseTasks");
        startActivity(intent);
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
        //messageAgreeList = mailTask.getMessages();
        if(mailTask.getMessages().size() >0 ) {
            listOfMessages.setAdapter(new AgArrayAdapter(this, mailTask.getMessages()));
        }
        mailTask.delOldFiles();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        mailTask.saveSettings();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    protected void onResume() {
        textViewInfo.setText(infoString);
        displayAllMessages();
        super.onResume();
    }
}
