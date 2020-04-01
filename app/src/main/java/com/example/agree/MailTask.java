package com.example.agree;


import android.app.Activity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

class MailTask {
    private List<MessageAgree> messages;
    private Properties properties;
    private final Activity context;
    private String iD;
    private String logFileName;
    private String serverName;
    private String portNumber;
    private String account;
    private String pass;

    MailTask(Activity context) {
        this.context = context;
        messages = new ArrayList<>();
        properties = new Properties();
//        properties.put("mail.imap.host", getServerName());
//        properties.put("mail.imap.port", getPortNumber());
//        properties.put("mail.imap.starttls.enable", "true");
        iD = null;
        logFileName = context.getFilesDir() + "/log.dat";
    }

    List<MessageAgree> getMessages() {
        return messages;
    }

    public String getServerName() {
        return serverName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public String getAccount() {
        return account;
    }

    public String getPass() {
        return pass;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setMessages(List<MessageAgree> messages) {
        this.messages = messages;
    }

    void loadMailsetting(){
        properties.put("mail.imap.host", getServerName());
        properties.put("mail.imap.port", getPortNumber());
        properties.put("mail.imap.starttls.enable", "true");
    }

    private void purgeMessagesList(){
        if (messages.size() > 0)
        messages.clear();
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
        //purgeMessagesList();

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
                    if (subject.contains("На согласование") && subject.contains("ID")) {
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
                            //MainActivity.infoString = e.toString();
                            //e.printStackTrace();
                        }

                        for (String contractor : listOfContractors) {
                            String[] contractorParam = contractor.split("#");
                            if (contractorParam.length == 2) {
                                messages.add(new MessageAgree(iD, contractorParam[1], contractorParam[0]));
                            } else {
                                MainActivity.infoString = "incorrect mail format " + iD;
                                //error message incorrect mail format
                            }
                        }
                    }
                }
                inbox.close(false);
                store.close();
                //Collections.sort(messages);

            } catch (MessagingException e) {
                ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
                //MainActivity.infoString = e.toString();
                //e.printStackTrace();
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
                    strParse.append("\n");
                    strParse.append(org.jsoup.Jsoup.parse(html).text());
                } else if (part.getContent() instanceof MimeMultipart) {
                    strParse.append(parseMail((MimeMultipart) part.getContent()));
                }
                else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    MimeBodyPart mPart = (MimeBodyPart) multipart.getBodyPart(i);
                    String decodedFilename = MimeUtility.decodeText(mPart.getFileName());
                    //File fileAttach = new File(context.getFilesDir() + decodedFilename);
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

    void delOldFiles(){
        List<FilesFromMail> listF = MainActivity.listFilesFromMail;
        for (FilesFromMail currentF : listF){
            File file = new File(context.getFilesDir() + currentF.getFileName());
            Path path = file.toPath();
            if (file.exists()) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    if((new Date().getTime() - attrs.creationTime().toMillis())/(48*60*60*1000) > 1){
                        file.delete();
                    }
                } catch (IOException e) {
                    ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
                }
            }
        }
    }


//    void saveSettings(){
//        try {
//            ObjectOutputStream objectOS = new ObjectOutputStream(new FileOutputStream(context.getFilesDir() + "/ds2.dat"));
//            objectOS.writeObject(new ArrayList<MessageAgree>(getMessages()));
//            objectOS.writeObject(new ArrayList<FilesFromMail>(MainActivity.listFilesFromMail));
//            objectOS.close();
//        }catch (Exception e){
//            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
//            //MainActivity.infoString = e.toString();
//            //e.printStackTrace();
//        }
//    }

//    void loadSettings(){
//        File fileSer = new File(context.getFilesDir() + "/ds2.dat");
//        if (fileSer.exists()){
//            try {
//                ObjectInputStream objectIS = new ObjectInputStream(new FileInputStream(fileSer));
//                List<MessageAgree> list = (List<MessageAgree>) objectIS.readObject();
////                synchronized (messages) {
////                    for (MessageAgree currentM : list) {
////                        if (!messages.contains(currentM)) {
////                            messages.add(currentM);
////                        }
////                    }
////                }
//                setMessages(list);
//                List<FilesFromMail> listF = (List<FilesFromMail>) objectIS.readObject();
////                    for(FilesFromMail currentF : listF){
////                        if (!MainActivity.listFilesFromMail.contains(currentF)){
////                            if (new File(context.getFilesDir() + currentF.getFileName()).exists())
////                            MainActivity.listFilesFromMail.add(currentF);
////                        }
////                    }
//                MainActivity.listFilesFromMail = listF;
//                objectIS.close();
//            } catch (Exception e){
//                ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
//                //MainActivity.infoString = e.toString();
//                //e.printStackTrace();
//            }
//        }
//    }

}
