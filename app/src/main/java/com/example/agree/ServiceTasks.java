package com.example.agree;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ServiceTasks {

    static void addLogFile(String filesDir, String logString){
        try {
            File file = new File(filesDir);
            if (file.createNewFile()) {
            }else {
                Files.write(Paths.get(filesDir), logString.getBytes(), StandardOpenOption.APPEND);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void saveSettings(Context context, List<MessageAgree> messageAgreeList){
        String logFileName = context.getFilesDir() + "/log.dat";
        try {
            ObjectOutputStream objectOS = new ObjectOutputStream(new FileOutputStream(context.getFilesDir() + "/adat.dat"));
            objectOS.writeObject(new ArrayList<MessageAgree>(messageAgreeList));
            objectOS.writeObject(new ArrayList<FilesFromMail>(MainActivity.listFilesFromMail));
            objectOS.writeObject(MainActivity.mailTask.getServerName());
            objectOS.writeObject(MainActivity.mailTask.getPortNumber());
            objectOS.writeObject(MainActivity.mailTask.getAccount());
            objectOS.writeObject(MainActivity.mailTask.getPass());
            objectOS.close();
        }catch (Exception e){
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
        }
    }

    public static void loadSettings(Context context){
        String logFileName = context.getFilesDir() + "/log.dat";
        File fileSer = new File(context.getFilesDir() + "/adat.dat");
        if (fileSer.exists()){
            try {
                ObjectInputStream objectIS = new ObjectInputStream(new FileInputStream(fileSer));
                List<MessageAgree> list = (List<MessageAgree>) objectIS.readObject();
                MainActivity.mailTask.setMessages(list);

                List<FilesFromMail> listF = (List<FilesFromMail>) objectIS.readObject();
                MainActivity.listFilesFromMail = listF;

                String serverF = (String) objectIS.readObject();
                MainActivity.mailTask.setServerName(serverF);
                String portNumF = (String) objectIS.readObject();
                MainActivity.mailTask.setPortNumber(portNumF);
                String accountF = (String) objectIS.readObject();
                MainActivity.mailTask.setAccount(accountF);
                String passF = (String) objectIS.readObject();
                MainActivity.mailTask.setPass(passF);

                objectIS.close();
            } catch (Exception e){
                ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
            }
        }else {
            MainActivity.mailTask.setServerName("");
            MainActivity.mailTask.setPortNumber("");
            MainActivity.mailTask.setAccount("");
            MainActivity.mailTask.setPass("");
        }
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
