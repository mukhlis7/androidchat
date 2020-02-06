package com.example.aymen.androidchat;

/**
 * Created by Aymen on 08/06/2018.
 */

public class ContactsModel {


    private String Username;
    private String Fullname;
    private String Time;
    private String Thumbimgurl;

    public  ContactsModel(){

    }
    public ContactsModel(String username, String fullname, String time, String thumbimgurl) {
        this.Username = username;
        this.Fullname = fullname;
        this.Thumbimgurl = thumbimgurl;
        this.Time = time;
    }



    public String getUsername() {
        return Username;
    }

    public String getFullname() {
        return Fullname;
    }

    public String getThumbimgurl() {
        return Thumbimgurl;
    }

    public String  getTime(){

        return Time;
    }



    public void setUsername(String username) {
        this.Username = username;
    }



    public void setFullname(String fullname) {
        this.Fullname = fullname;
    }


    public void setThumbimgurl(String thumbimgurl) {
        this.Thumbimgurl = thumbimgurl;
    }


    public void setTime(String time) {
        this.Time = time;
    }


}


