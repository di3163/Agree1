package com.example.agree;


import android.app.Activity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

class MailTask {
    private List<MessageAgree> messages;
    private Properties properties;
    private Properties propertiesSMTP;
    private final Activity context;
    private String iD;
    private String logFileName;
    private String serverName;
    private String portNumber;
    private String account;
    private String pass;
    private String htmlString;

    MailTask(Activity context) {
        this.context = context;
        messages = new ArrayList<>();
        properties = new Properties();
        propertiesSMTP = new Properties();
        iD = null;
        logFileName = context.getFilesDir() + "/log.dat";
    }

    List<MessageAgree> getMessages() {
        return messages;
    }

    String getServerName() {
        return serverName;
    }

    String getPortNumber() {
        return portNumber;
    }

    String getAccount() {
        return account;
    }

    String getPass() {
        return pass;
    }

    void setServerName(String serverName) {
        this.serverName = serverName;
    }

    void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    void setAccount(String account) {
        this.account = account;
    }

    void setPass(String pass) {
        this.pass = pass;
    }

    void setMessages(List<MessageAgree> messages) {
        this.messages = messages;
    }

    void loadMailsetting(){
        properties.put("mail.imap.host", getServerName());
        properties.put("mail.imap.port", getPortNumber());
        properties.put("mail.imap.starttls.enable", "true");

//        properties.put("mail.transport.protocol", "smtp");
        propertiesSMTP.put("mail.smtp.host", "smtp.yandex.ru");
        propertiesSMTP.put("mail.smtp.auth", "true");
        propertiesSMTP.put("mail.smtp.socketFactory.port", "465");
        propertiesSMTP.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    }

    boolean sendMess(String s, String currentId) {
        boolean succesfully = false;
        Session sessionSMTP = Session.getInstance(propertiesSMTP,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("", "");
                    }
                });
        Message messageSMTP = new MimeMessage(sessionSMTP);
        try {
            messageSMTP.setFrom(new InternetAddress("merida-di@yandex.ru"));
            messageSMTP.setRecipient(Message.RecipientType.TO, new InternetAddress("merida-di@yandex.ru"));
            messageSMTP.setSubject(currentId);
            messageSMTP.setText(s);
            Transport.send(messageSMTP);
            succesfully = true;
        } catch (MessagingException e) {
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
        }
        return succesfully;
    }


    public void makeAg(){
        List<MessageAgree> listActual = selectActualAgree();
        List<String> listShipped = new ArrayList<>();
        String currentId;
        for (MessageAgree current : listActual) {
               currentId = current.getId();
            if (!listShipped.contains(currentId)){
                String messageString = addAgToMeesage(listActual, currentId);
                if (messageString.equals(""))messageString="+";
                if (sendMess(messageString, currentId)) {
                    listShipped.add(current.getId());
                }

            }
        }

        for (MessageAgree current : listActual) {
            if (listShipped.contains(current.getId())) current.setAgreed(true);
        }
    }

    public String addAgToMeesage (List<MessageAgree> listActual, String currentId){
        StringBuilder messageBuilder = new StringBuilder();
        for (MessageAgree current : listActual) {
            if (current.getId().equals(currentId)) {
                messageBuilder.append(addAgrStatInBody(current));
            }
        }
        return messageBuilder.toString();
    }

    List<MessageAgree> selectActualAgree(){
        List<MessageAgree> listActual = new ArrayList<>();
        for (MessageAgree currentMess : messages){
            if (!ServiceTasks.removeTime(currentMess.getDateAgr()).equals(ServiceTasks.removeTime(new Date())) || currentMess.isAgreed()) continue;
            listActual.add(currentMess);
        }
        return listActual;
    }

    String addAgrStatInBody(MessageAgree currM){
        StringBuilder sb = new StringBuilder();
        if (currM.getAgrStat().equals("+")){
            return "";
        }else if (currM.getAgrStat().equals("*")){
            sb.append(currM.getAgrNumInMail());
            sb.append("*");
            sb.append("\n");
        }else if (currM.getAgrStat().equals("/")){
            sb.append(currM.getAgrNumInMail());
            sb.append("/");
            sb.append("\n");
        }else {
            sb.append(currM.getAgrNumInMail());
            sb.append("\n");
        }
        return sb.toString();
    }


    void sendAgree(){
        List<MessageAgree> listActual = selectActualAgree();
        List<String> listPostedId = new ArrayList<>();
        List<String> listShipped = new ArrayList<>();
        for (MessageAgree current : listActual) {
            String currentId = null;
            StringBuilder stringBody = new StringBuilder();
            for (MessageAgree currentMess : listActual) {
                if (currentId == null) {
                    if (!listPostedId.contains(currentMess.getId())) {
                        listPostedId.add(currentMess.getId());
                        currentId = currentMess.getId();
                        stringBody.append(addAgrStatInBody(currentMess));
                    }
                } else if (currentId.equals(currentMess.getId())) {
                    stringBody.append(addAgrStatInBody(currentMess));
                }
            }
            if (!current.isAgreed()) {
                if (!listShipped.contains(current.getId())) {
                    String stringB = null;
                    if (stringBody.toString().equals("")){
                        stringB = "+";

                    }else {
                        stringB = stringBody.toString();
                    }
                    if (!sendMess(stringB, current.getId())) {
                        listPostedId.remove(current.getId());
                        listShipped.remove(current.getId());
                    } else {
                        listShipped.add(current.getId());
                    }
                }
            }
        }
        for (MessageAgree current : listActual) {
            if (listShipped.contains(current.getId())) current.setAgreed(true);
        }

    }


    void purgeMessagesList(){
        if (messages.size() > 0)
        messages.clear();
    }

    MessageAgree getMessageAgreeForId(String idForMess){
        MessageAgree findMess = null;
        for (MessageAgree current : messages){
            if (current.getId().equals(idForMess)){
                findMess = current;
            }
        }
        return findMess;
    }

    private List<String> getArrId(){
        List<String> listId = new ArrayList<>();
        for (MessageAgree mid : messages){
            listId.add(mid.getId());
        }
        return listId;
    }

    void refreshListMessages(){
        List<String> listOfContractors = new ArrayList<>();

        List<String> listId = new ArrayList<>();
        synchronized (messages) {
            if (messages.size() != 0) {
                listId = getArrId();
            }

            Session session = Session.getDefaultInstance(properties);
            try {
                Store store = session.getStore("imaps");
                store.connect(getServerName(),getAccount(), getPass());
                Folder inbox = store.getFolder("vintegra");
                inbox.open(Folder.READ_ONLY);
                int count = inbox.getMessageCount();
                Message[] messagesArr = inbox.getMessages(1, count);
                for (Message message : messagesArr) {
                    String subject = message.getSubject();
                    if (subject.contains("На согласование") && subject.contains("ID") &&
                            ServiceTasks.removeTime(message.getReceivedDate()).equals(ServiceTasks.removeTime(new Date()))) {
                        iD = subject.substring(subject.lastIndexOf("ID:"));
                        if (listId.size() != 0) {
                            if (listId.contains(iD))
                                continue;
                        }
                        listOfContractors.clear();
                        try {
                            Object content = message.getContent();
                            if (content instanceof Multipart) {
                                MimeMultipart multipart = (MimeMultipart) content;
                                listOfContractors = contNames(parseMail(multipart));
                            }
                        } catch (IOException e) {
                            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
                        }

                        for (String contractor : listOfContractors) {
                            String[] contractorParam = contractor.split("#");
                            if (contractorParam.length == 2) {
                                messages.add(new MessageAgree(iD, contractorParam[1], contractorParam[0], htmlString));
                                htmlString = "";
                            } else {
                                MainActivity.infoString = "incorrect mail format " + iD;
                            }
                        }
                    }
                }
                inbox.close(false);
                store.close();

            } catch (MessagingException e) {
                ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
            }
        }
    }

    private List<String> contNames(@NotNull String strParse){
        List<String> listContractors = new ArrayList<>();
        String[] stringsMess = strParse.replaceAll("[\r]","").split("\n");
        int index = Arrays.asList(stringsMess).indexOf("Коммент из карточки клиента");
        if (index != -1){
            int numOfClient = Integer.parseInt(stringsMess[index+2]);
            listContractors.add("1#"+stringsMess[index+4]);
            numOfClient++;
            for (int k = index+5; k < stringsMess.length; k++ ){

                String currentString = stringsMess[k];
                if (currentString.length() == 1 && Integer.parseInt(currentString) == numOfClient){
                    listContractors.add(currentString+"#"+stringsMess[k+2]);
                    numOfClient++;
                }
            }
        }
        return listContractors;
    }

    @NotNull
    private String parseMail(@NotNull MimeMultipart multipart) {
        StringBuilder strParse = new StringBuilder();
        try {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    strParse.append("\n");
                    strParse.append(part.getContent());
                } else if (part.isMimeType("text/html")) {
                    String html = (String) part.getContent();
                    htmlString = html;
                    strParse.append("\n");
                    strParse.append(org.jsoup.Jsoup.parse(html).text());
                } else if (part.getContent() instanceof MimeMultipart) {
                    strParse.append(parseMail((MimeMultipart) part.getContent()));
                }
                else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    MimeBodyPart mPart = (MimeBodyPart) multipart.getBodyPart(i);
                    String decodedFilename = MimeUtility.decodeText(mPart.getFileName());
                    //File fileAttach = new File(context.getFilesDir() + decodedFilename);
                    if(decodedFilename.contains("КК")){
                        decodedFilename = iD+decodedFilename;
                    }
                    if (!new File(context.getFilesDir() + decodedFilename).exists()) {
                        mPart.saveFile(new File(context.getFilesDir() + decodedFilename));

                    }
                    MainActivity.listFilesFromMail.add(new FilesFromMail(decodedFilename, iD));
                }
            }
        } catch (IOException e) {
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
        } catch (MessagingException e) {
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
        }
        return strParse.toString();
    }

    void delFiles(boolean all) {
        List<FilesFromMail> listFiles = MainActivity.listFilesFromMail;
        FilesFromMail[] filesArray = new FilesFromMail[listFiles.size()];
        listFiles.toArray(filesArray);
        for (FilesFromMail filesFromMail : filesArray) {
            File file = new File(context.getFilesDir() + filesFromMail.getFileName());
            Path path = file.toPath();
            if (file.exists()) {
                try {
                    if (all) {
                        file.delete();
                        listFiles.remove(filesFromMail);
                    } else {
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                        if ((new Date().getTime() - attrs.creationTime().toMillis()) / (48 * 60 * 60 * 1000) > 1) {
                            file.delete();
                            listFiles.remove(filesFromMail);
                        }
                    }
                } catch (IOException e) {
                    ServiceTasks.addLogFile(logFileName, new Date() + ":" + e.toString() + "\n");

                }
            }
        }
    }
}
