package com.example.agree;

import java.io.Serializable;
import java.util.Date;

class MessageAgree implements Serializable{
    private String id;
    private String subject;
    private String agrNumInMail;
    private String agrStat;
    private Date dateAgr;

    MessageAgree(String id, String subject, String agrStat) {
        this.id = id;
        this.subject = subject;
        this.agrStat = agrStat;
        this.agrNumInMail = agrStat;
        dateAgr = new Date();
    }

    String getId() {
        return id;
    }

    String getSubject() {
        return subject;
    }

    String getAgrStat() {
        return agrStat;
    }

    void setAgrStat(String agrStat) {
        this.agrStat = agrStat;
    }

    String getAgrNumInMail() {
        return agrNumInMail;
    }

    Date getDateAgr() {
        return dateAgr;
    }

}
