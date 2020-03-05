package com.example.agree;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

class MessageAgree implements Serializable, Comparable<MessageAgree>{
    private String id;
    private String subject;
    private String agrNumInMail;
    private String agrStat;
    private Calendar calendar;

    MessageAgree(String id, String subject, String agrStat) {
        this.id = id;
        this.subject = subject;
        this.agrStat = agrStat;
        this.agrNumInMail = agrStat;
        this.calendar = new GregorianCalendar();
    }

    String getId() {
        return id;
    }

    String getSubject() {
        return subject;
    }

    public String getAgrStat() {
        return agrStat;
    }

    public void setAgrStat(String agrStat) {
        this.agrStat = agrStat;
    }

    public String getAgrNumInMail() {
        return agrNumInMail;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public int compareTo(MessageAgree o) {
        return getCalendar().compareTo(o.getCalendar());
    }
}
