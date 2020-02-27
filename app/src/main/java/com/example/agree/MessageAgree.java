package com.example.agree;

public class MessageAgree {
    String id;
    String subject; //потом сюда поместим контрагента
    boolean agr;

    public MessageAgree(String id, String subject) {
        this.id = id;
        this.subject = subject;
        agr = false;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isAgr() {
        return agr;
    }

    public void setAgr(boolean agr) {
        this.agr = agr;
    }
}
