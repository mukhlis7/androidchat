package com.example.aymen.androidchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatBoxDBHelper extends SQLiteOpenHelper {


    //-------------ChatBox Database Name----------------------
    public static final String ChatBox_DBName = "ChatBoxDB.db";


    //-------------ChatBox Database Version----------------------
    static final int ChatBox_DBVersion = 1;



    //-------------Table to store loggedin User's Profile Data-----------
    public static final String Loggedin_User_Table = "Loggedin_User_Data";



    //-------------Columns in which loggedin user's data will be stored--------
    public static final String Loggedin_User_ID = "_ID";
    public static final String Loggedin_User_Fullname = "_Fullname";
    public static final String Loggedin_User_Username = "_Username";
    public static final String Loggedin_User_Email = "_Email";
    public static final String Loggedin_User_Token = "_Token";
    public static final String Loggedin_User_Profile_pic_url = "_Profile_pic_URL";
    public static final String Loggedin_User_Thumb_pic_url = "_Thumb_pic_URL";



    //--------------Table to store Loggedin User's Contacts----------------
    public static final String ChatContacts_Table = "User_Contacts";



    //---------------Columns to LoggedIn User's Contacts Data---------
    public static final String Loggedin_User_Contact_ID = "_CID";
    public static final String Loggedin_User_Contact_Username = "_CUsername";
    public static final String Loggedin_User_Contact_Fullname = "_CFullname";
    public static final String Loggedin_User_Contact_UniqueId = "_CUniqueId";
    public static final String Loggedin_User_Contact_Profile_pic_url = "_CProfile_pic_URL";
    public static final String Loggedin_User_Contact_Thumb_pic_url = "_CThumb_pic_URL";


    //---------------Table to Store LoggenIn User's Chat History----------
    public static final String Chat_History_Table = "Chat_History";

    //---------------Columns To Store LoggenIn User's Chat History--------
    public static final String ChatBox_ID = "ChatBoxDB.db";
    public static final String ChatBox_Sender = "ChatBoxDB.db";
    public static final String ChatBox_Recipient = "ChatBoxDB.db";
    public static final String ChatBox_Sender_UniqueId = "ChatBoxDB.db";
    public static final String ChatBox_MsgType = "ChatBoxDB.db";
    public static final String ChatBox_Message = "ChatBoxDB.db";
    public static final String ChatBox_UnixTimeStamp = "ChatBoxDB.db";


    private SQLiteDatabase chatBoxDatabase;

    private static final String CREATE_Loggedin_User_Table = "CREATE TABLE " + Loggedin_User_Table + "(" + Loggedin_User_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Loggedin_User_Fullname + " TEXT NOT NULL, " + Loggedin_User_Username + " TEXT NOT NULL, " + Loggedin_User_Email + " TEXT NOT NULL, "
            + Loggedin_User_Token + " TEXT NOT NULL, " + Loggedin_User_Profile_pic_url + " TEXT, " + Loggedin_User_Thumb_pic_url + " TEXT);";


    public ChatBoxDBHelper(@Nullable Context context) {
        super(context, ChatBox_DBName, null, ChatBox_DBVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_Loggedin_User_Table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Loggedin_User_Table);
        onCreate(db);
    }

    public void open() throws SQLException{

        chatBoxDatabase = this.getWritableDatabase();

    }

    public void close(){

        chatBoxDatabase.close();

    }

    public void addDataToLoggedin_User_Table(String fullname, String username, String email, String token, String profile_pic_url, String thumb_pic_url){


        ContentValues contentValues  = new ContentValues();
        contentValues.put(Loggedin_User_Fullname,fullname);
        contentValues.put(Loggedin_User_Username,username);
        contentValues.put(Loggedin_User_Email,email);
        contentValues.put(Loggedin_User_Token,token);
        contentValues.put(Loggedin_User_Profile_pic_url,profile_pic_url);
        contentValues.put(Loggedin_User_Thumb_pic_url,thumb_pic_url);

        chatBoxDatabase.insert(Loggedin_User_Table,null,contentValues);
    }

    public JSONArray getLoggedinUserDetails() {

        //final String TABLE_NAME = "name of table";

        String selectQuery = "SELECT  * FROM " + Loggedin_User_Table;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        //Log.d(TAG, e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }

    public void logoutdb(){

        chatBoxDatabase.execSQL("delete from "+ Loggedin_User_Table);

    }

}
