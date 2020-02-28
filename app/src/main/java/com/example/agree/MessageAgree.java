package com.example.agree;

class MessageAgree {
    private String id;
    private String subject;
    private boolean agr;

    MessageAgree(String id, String subject) {
        this.id = id;
        this.subject = subject;
        agr = false;
    }

    String getId() {
        return id;
    }

    String getSubject() {
        return subject;
    }

    boolean isAgr() {
        return agr;
    }

    void setAgr(boolean agr) {
        this.agr = agr;
    }


}
