package com.example.agree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
}
