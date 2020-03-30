package com.example.agree;

import java.io.Serializable;
import java.util.Date;


//class MessageAgree implements Serializable, Comparable<MessageAgree>{
class MessageAgree implements Serializable{
    private String id;
    private String subject;
    private String agrNumInMail;
    private String agrStat;
    private Date dateAgr;
    //private Calendar calendar;

    MessageAgree(String id, String subject, String agrStat) {
        this.id = id;
        this.subject = subject;
        this.agrStat = agrStat;
        this.agrNumInMail = agrStat;
        dateAgr = new Date();
        //this.calendar = new GregorianCalendar();
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

    public Date getDateAgr() {
        return dateAgr;
    }

    //    public Calendar getCalendar() {
//        return calendar;
//    }
//
//    @Override
//    public int compareTo(MessageAgree o) {
//        return getCalendar().compareTo(o.getCalendar());
//    }
}
