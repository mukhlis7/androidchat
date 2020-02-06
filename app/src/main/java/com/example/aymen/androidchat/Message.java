package com.example.aymen.androidchat;

/**
 * Created by Aymen on 08/06/2018.
 */

public class Message {


    private String MyUsername;
    private String OtherUsername;
    private String Message;
    private String MyThumbimgurl;
    private String OtherThumbimgurl;
    private String MsgType;
    private int ID;
    private int Sorr;
    private long Timestamp;

    public  Message(){

    }



    public Message(String sender, String recipient, String message, String mythumbimgurl, String otherthumbimgurl, String msgType, long timestamp, int id, int sorr) {
        this.MyUsername = sender;
        this.OtherUsername = recipient;
        this.Message = message;
        this.MyThumbimgurl = mythumbimgurl;
        this.OtherThumbimgurl = otherthumbimgurl;
        this.Timestamp = timestamp;
        this.MsgType = msgType;
        this.ID = id;
        this.Sorr = sorr;
    }


    public String getMyUsername() {
        return MyUsername;
    }

    public void setMyUsername(String myUsername) {
        MyUsername = myUsername;
    }

    public String getOtherUsername() {
        return OtherUsername;
    }

    public void setOtherUsername(String otherUsername) {
        OtherUsername = otherUsername;
    }

    public String getMyThumbimgurl() {
        return MyThumbimgurl;
    }

    public void setMyThumbimgurl(String myThumbimgurl) {
        MyThumbimgurl = myThumbimgurl;
    }

    public String getOtherThumbimgurl() {
        return OtherThumbimgurl;
    }

    public void setOtherThumbimgurl(String otherThumbimgurl) {
        OtherThumbimgurl = otherThumbimgurl;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSorr() {
        return Sorr;
    }

    public void setSorr(int sorr) {
        Sorr = sorr;
    }

    public String getMessage() {
        return Message;
    }


    public void setMessage(String message) {
        this.Message = message;
    }


    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }


}


