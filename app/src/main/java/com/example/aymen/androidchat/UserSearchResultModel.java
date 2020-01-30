package com.example.aymen.androidchat;

public class UserSearchResultModel {

    String Fullname, Username, Email, Profile_picurl,Profile_Thumb_picurl;

    public String getFullname() {
        return Fullname;
    }

    public String getUsername() {
        return Username;
    }

    public String getProfile_picurl() {
        return Profile_picurl;
    }

    public String getEmail() {
        return Email;
    }

    public String getProfile_Thumb_picurl() {
        return Profile_Thumb_picurl;
    }


    public UserSearchResultModel(String fullname, String username, String profile_picurl, String email, String profile_Thumb_picurl) {
        Fullname = fullname;
        Username = username;
        Profile_picurl = profile_picurl;
        Email = email;
        Profile_Thumb_picurl = profile_Thumb_picurl;
    }
}
