package com.example.agree;

import java.io.Serializable;
import java.util.Date;

public class FilesFromMail implements Serializable {
    String fileName;
    String iD;
    Date dateFile;

    public FilesFromMail(String fileName, String iD) {
        this.fileName = fileName;
        this.iD = iD;
        dateFile = new Date();
    }

    public String getFileName() {
        return fileName;
    }

    public String getiD() {
        return iD;
    }

    public Date getDateFile() {
        return dateFile;
    }
}
