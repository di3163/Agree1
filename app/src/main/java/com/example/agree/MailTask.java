package com.example.agree;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

class MailTask {
    private List<MessageAgree> messages;
    private Properties properties;

    MailTask() {
        messages = new ArrayList<>();
        properties = new Properties();
        properties.put("mail.imap.host", "imap.yandex.ru");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
    }

    List<MessageAgree> getMessages() {
        return messages;
    }

    private void purgeMessagesList(){
        if (messages.size() > 0)
        messages.clear();
    }

    void refreshListMessages(){
        List<String> listOfContractors = new ArrayList<>();
        purgeMessagesList();
        Session session = Session.getDefaultInstance(properties);
        try {
            Store store = session.getStore("imaps");
            store.connect("imap.yandex.ru", "", "");
            Folder inbox = store.getFolder("vintegra");
            inbox.open(Folder.READ_ONLY);
            int count = inbox.getMessageCount();
            Message[] messagesArr = inbox.getMessages(1, count);
            for (Message message : messagesArr) {
                String subject = message.getSubject();
                if (subject.contains("На согласование") && subject.contains("ID")) {
                    listOfContractors.clear();
                    try {
                        Object content = message.getContent();
                        if (content instanceof Multipart){
                            MimeMultipart multipart = (MimeMultipart) content;
                            listOfContractors = contNames(parseMail(multipart));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String iD = subject.substring(subject.lastIndexOf("ID:"));
                    for (String contractor : listOfContractors){
                        messages.add(new MessageAgree(iD, contractor));
                    }
                }
            }
            inbox.close(false);
            store.close();
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

    private List<String> contNames(@NotNull String strParse){
        List<String> listContractors = new ArrayList<>();
        String[] stringsMess = strParse.replaceAll("[\r]","").split("\n");
        int index = Arrays.asList(stringsMess).indexOf("Коммент из карточки клиента");
        if (index != -1){
            int numOfClient = Integer.parseInt(stringsMess[index+2]);
            listContractors.add(stringsMess[index+4]);
            numOfClient++;
            for (int k = index+5; k < stringsMess.length; k++ ){

                String currentString = stringsMess[k];
                if (currentString.length() == 1 && Integer.parseInt(currentString) == numOfClient){
                    listContractors.add(stringsMess[k+2]);
                    numOfClient++;
                }
            }
        }
        return listContractors;
    }

    private String parseMail(@NotNull MimeMultipart multipart) {
        //String strParse = "";
        StringBuilder strParse = new StringBuilder();
        try {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    //strParse = strParse + "\n" + part.getContent();
                    strParse.append("\n");
                    strParse.append(part.getContent());
                } else if (part.isMimeType("text/html")) {
                    String html = (String) part.getContent();
                    //strParse = strParse + "\n" + org.jsoup.Jsoup.parse(html).text();
                    strParse.append("\n");
                    strParse.append(org.jsoup.Jsoup.parse(html).text());
                } else if (part.getContent() instanceof MimeMultipart) {
                    //strParse = strParse + parseMail((MimeMultipart) part.getContent());
                    strParse.append(parseMail((MimeMultipart) part.getContent()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return strParse.toString();
    }
}
