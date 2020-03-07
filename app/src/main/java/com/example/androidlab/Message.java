package com.example.androidlab;

public class Message {
    public String msg;
    public int sendOrReceive;
    public long msgid;
    public static int TYPE_SEND = 1;
    public static int TYPE_RECEIVE = 2;
    public int position = -1;
    public Message(String msgContent, int sOrR, long id) {
        msg = msgContent;
        sendOrReceive = sOrR;
        msgid = id;
    }
    public void setPosition(int position){
        this.position = position;
    }
}
