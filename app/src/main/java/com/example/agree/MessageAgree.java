package com.example.agree;

class MessageAgree {
    private String id;
    private String subject;
    private String agr;
    private String agrStat;

    MessageAgree(String id, String subject, String agrStat) {
        this.id = id;
        this.subject = subject;
        this.agrStat = agrStat;
        this.agr = agrStat;
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

    public String getAgr() {
        return agr;
    }
}
