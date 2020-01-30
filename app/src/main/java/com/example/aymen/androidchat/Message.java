package com.example.aymen.androidchat;

/**
 * Created by Aymen on 08/06/2018.
 */

public class Message {


    private String nickname;
    private String message;
    private String uniqueId;
    private String UserJoined;
    private String Thumbimgurl;
    private long Timestamp;

    public  Message(){

    }
    public Message(String nickname, String message, String uniqueId, String UserJoined, String thumbimgurl, long timestamp) {
        this.nickname = nickname;
        this.message = message;
        this.uniqueId = uniqueId;
        this.UserJoined = UserJoined;
        this.Thumbimgurl = thumbimgurl;
        this.Timestamp = timestamp;
    }



    public String getNickname() {
        return nickname;
    }

    public String getuniqueId() {
        return uniqueId;
    }

    public String getMessage() {
        return message;
    }


    public String getUserJoined() {
        return UserJoined;
    }

    public String getThumbimgurl() {
        return Thumbimgurl;
    }

    public long getTimestamp(){

        return Timestamp;
    }



    public void setNickname(String nickname) {
        this.nickname = nickname;
    }



    public void setMessage(String message) {
        this.message = message;
    }

    public void setuniqueId(String uniqueId) {
        this.nickname = uniqueId;
    }

    public void setThumbimgurl(String thumbimgurl) {
        this.Thumbimgurl = thumbimgurl;
    }

    public void setUserJoined(String UserJoined) {
        this.UserJoined = UserJoined;
    }

    public void setTimestamp(long time) {
        this.Timestamp = time;
    }


}


