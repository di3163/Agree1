package com.example.agree;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MailService extends Service {
    AsTask asTask;
    TimerT timerT;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        asTask = new AsTask();
        timerT = new TimerT();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerT, 100000, 100000);

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                AsTask asTask = new AsTask();
//                asTask.execute();
//            }
//        },0, 300000);
    }

    public void onDestroy(){
        timerT.cancel();
    }

    class TimerT extends TimerTask{

        @Override
        public void run() {
            asTask.execute();
        }

        @Override
        public boolean cancel() {
            asTask.cancel(true);
            return super.cancel();
        }
    }

    class AsTask extends AsyncTask<Void,  Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            MainActivity.mailTask.refreshListMessages();
            return null;
        }
        protected void onPostExecute(String result){
            MainActivity.infoString = "Список сообщений обновлен " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        }
    }
}
