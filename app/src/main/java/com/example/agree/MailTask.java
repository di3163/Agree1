package com.example.agree;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class MailTask {

    private List<MessageAgree> messages;
    private Properties properties;

    public MailTask() {
        messages = new ArrayList<>();
        properties = new Properties();
        properties.put("mail.imap.host", "imap.yandex.ru");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
    }

    public List<MessageAgree> getMessages() {
        return messages;
    }

    public void purgeMessagesList(){
        if (messages.size() > 0)
        messages.clear();
    }

    void refreshListMessages(){
        purgeMessagesList();
        Session session = Session.getDefaultInstance(properties);
        try {
            Store store = session.getStore("imaps");
            store.connect("imap.yandex.ru", "", "");
            Folder inbox = store.getFolder("java");
            inbox.open(Folder.READ_ONLY);
            int count = inbox.getMessageCount();
            Message[] messagesArr = inbox.getMessages(1, count);
            for (Message message : messagesArr) {
                messages.add(new MessageAgree("", message.getSubject()));
            }
            inbox.close(false);
            store.close();
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
