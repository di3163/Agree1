package com.example.agree;

import java.io.File;

public class FilesFromMail {
    String fileName;
    String iD;

    public FilesFromMail(String fileName, String iD) {
        this.fileName = fileName;
        this.iD = iD;
    }

    public String getFileName() {
        return fileName;
    }

    public String getiD() {
        return iD;
    }
}
