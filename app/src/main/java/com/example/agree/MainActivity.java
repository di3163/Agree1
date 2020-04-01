package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static MailTask mailTask;
    private static boolean showOldMess;
    private RelativeLayout activitiMain;
    private RecyclerView listOfMessages;
    private AgArrayAdapterRW agArrayAdapterRW;
    private TextView textViewInfo;
    AsTask asTask;
    static List<FilesFromMail> listFilesFromMail = new ArrayList<>();
    private ImageView stop, ok, wait, ab;
    static String infoString;
    Timer mTimer = new Timer();
    final Handler uiHandler = new Handler();
    private FloatingNavigationView mFloatingNavigationView;

    public static boolean isShowOldMess() {
        return showOldMess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acttivity_main_fr);
        textViewInfo = findViewById(R.id.text_info);
        listOfMessages = findViewById(R.id.rw_mess);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listOfMessages.setLayoutManager(layoutManager);
        mailTask = new MailTask(this);
        ServiceTasks.loadSettings(this);
        mailTask.loadMailsetting();
        //mailTask.loadSettings();
        infoString = "";
        showOldMess = false;

        mFloatingNavigationView = (FloatingNavigationView) findViewById(R.id.floating_navigation_view);
        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });

        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Snackbar.make((View) mFloatingNavigationView.getParent(), item.getTitle() + " Selected!", Snackbar.LENGTH_SHORT).show();
                switch ( item.getItemId()){
                    case R.id.nav_files:
                        onButtonFilesClic();
                        break;
                    case R.id.nav_download_mess:
                        onMessagesDownloadClick();
                        break;
                    case R.id.nav_mail_setting_list:
                        onMailSettingClick();
                        break;
                    case R.id.app_bar_switch:
                        if (showOldMess) {
                            showOldMess = false;
                            item.setTitle("Show history");
                        } else {
                            showOldMess = true;
                            item.setTitle("Hide history");
                        }
                        displayAllMessages();

                }
                mFloatingNavigationView.close();
                return true;
            }
        });

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
    }

    @Override
    public void onBackPressed() {
        if (mFloatingNavigationView.isOpened()) {
            mFloatingNavigationView.close();
        } else {
            super.onBackPressed();
        }
    }

    public void onMessagesDownloadClick(){
        asTask = new AsTask();
        asTask.execute();
    }

    public void onButtonFilesClic(){
        Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
        intent.putExtra("messageID", "ServiseTasks");
        startActivity(intent);
    }

    public void onMailSettingClick(){
        Intent intent = new Intent(MainActivity.this, MailSettinsActivity.class);
        intent.putExtra("mailSetting", "MaillSetting");
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

    public void openFileChoose(MessageAgree messageAg){
        Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
        intent.putExtra("messageID", messageAg.getId());
        startActivity(intent);
    }

    public void displayAllMessages(){
        List<MessageAgree> fullMessList= mailTask.getMessages();

        if(fullMessList.size() >0 ) {
            if (isShowOldMess()) {
                agArrayAdapterRW = new AgArrayAdapterRW(fullMessList);
                listOfMessages.setAdapter(agArrayAdapterRW);
            }else {
                List<MessageAgree> todayMessages = new ArrayList<>();
                for (MessageAgree currentMess : fullMessList){
                    if(removeTime(currentMess.getDateAgr()).equals(removeTime(new Date()))){
                        todayMessages.add(currentMess);
                    }
                }
                agArrayAdapterRW = new AgArrayAdapterRW(todayMessages);
                listOfMessages.setAdapter(agArrayAdapterRW);
            }
        }
//        mailTask.delOldFiles();
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
        ServiceTasks.saveSettings(this, mailTask.getMessages());
        //mailTask.saveSettings();
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

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
