package com.example.ogulcantoker.anniversarychat;


import java.util.Date;

/**
 * Created by ogulcan.toker on 22.03.2018.
 */

public class AnniversaryChat {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public AnniversaryChat(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }

    public AnniversaryChat(){
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
       return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
