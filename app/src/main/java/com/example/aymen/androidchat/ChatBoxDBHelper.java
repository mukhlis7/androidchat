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

import java.util.Random;

public class ChatBoxDBHelper extends SQLiteOpenHelper {


    public static final String AlphaNumaricString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";


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
    public static final String Loggedin_User_Contact_ID = "_id";
    public static final String Loggedin_User_Contact_Username = "_CUsername";
    public static final String Loggedin_User_Contact_Fullname = "_CFullname";
    public static final String Loggedin_User_Contact_UniqueId = "_CUniqueId";
    public static final String Loggedin_User_Contact_Email = "_CEmail";
    public static final String Loggedin_User_Contact_ChatTable = "_CChatTable";
    public static final String Loggedin_User_Contact_Profile_pic_url = "_CProfile_pic_URL";
    public static final String Loggedin_User_Contact_Thumb_pic_url = "_CThumb_pic_URL";


    //---------------Table to Store LoggenIn User's Chat History----------
    public static final String Chat_History_Table = "Chat_History";

    //---------------Columns To Store LoggenIn User's Chat History--------
    public static final String ChatBox_ID = "_id";
    public static final String ChatBox_Sender = "ChatBox_Sender";
    public static final String ChatBox_My_Thumb_IMG = "Sender_Thumb_IMG";
    public static final String ChatBox_Recipient = "ChatBox_Recipient";
    public static final String ChatBox_Other_Thumb_IMG = "Recipient_Thumb_IMG";
    public static final String ChatBox_WHICH = "ChatBox_SORR";
    public static final String ChatBox_MsgType = "ChatBox_MsgType";
    public static final String ChatBox_Message = "ChatBox_Message";
    public static final String ChatBox_UnixTimeStamp = "ChatBox_UnixTimeStamp";


    private String ContactChatTableName = "";

    private SQLiteDatabase chatBoxDatabase;

    private static final String CREATE_Loggedin_User_Table = "CREATE TABLE " + Loggedin_User_Table + "( " + Loggedin_User_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Loggedin_User_Fullname + " TEXT NOT NULL, " + Loggedin_User_Username + " TEXT NOT NULL, " + Loggedin_User_Email + " TEXT NOT NULL, "
            + Loggedin_User_Token + " TEXT NOT NULL, " + Loggedin_User_Profile_pic_url + " TEXT, " + Loggedin_User_Thumb_pic_url + " TEXT);";


    private static final String CREATE_ChatContacts_Table = "CREATE TABLE " + ChatContacts_Table + "( " + Loggedin_User_Contact_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Loggedin_User_Contact_Fullname + " TEXT, " + Loggedin_User_Contact_Username + " TEXT, " + Loggedin_User_Contact_UniqueId + " TEXT, "
            + Loggedin_User_Contact_Profile_pic_url + " TEXT, " + Loggedin_User_Contact_ChatTable + " TEXT, " + Loggedin_User_Contact_Thumb_pic_url + " TEXT, " + Loggedin_User_Contact_Email + " TEXT);";


    private static final String CREATE_Chat_History_Table = "CREATE TABLE " + Chat_History_Table + "(" + ChatBox_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ChatBox_Sender + " TEXT NOT NULL, " + ChatBox_Recipient + " TEXT NOT NULL, " + ChatBox_Other_Thumb_IMG + " TEXT, " + ChatBox_My_Thumb_IMG + " TEXT, " + ChatBox_WHICH + " INTEGER, "
            + ChatBox_MsgType + " TEXT NOT NULL, " + ChatBox_Message + " TEXT NOT NULL, " + ChatBox_UnixTimeStamp + " TEXT NOT NULL);";



    public ChatBoxDBHelper(@Nullable Context context) {
        super(context, ChatBox_DBName, null, ChatBox_DBVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_Loggedin_User_Table);
        db.execSQL(CREATE_ChatContacts_Table);
       // db.execSQL(CREATE_Chat_History_Table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Loggedin_User_Table);
        db.execSQL("DROP TABLE IF EXISTS " + ChatContacts_Table);
        //db.execSQL("DROP TABLE IF EXISTS " + Chat_History_Table);
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




    public Cursor getLoggedinUserDetails() {

        //final String TABLE_NAME = "name of table";

        String selectQuery = "SELECT  * FROM " + Loggedin_User_Table;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        return cursor;

    }

    public void logoutdb(){

        chatBoxDatabase.execSQL("delete from "+ Loggedin_User_Table);

    }

    public String addDataToChatContacts_Table(String fullname, String username, String email, String UniqueId, String thumb_pic_url, String profile_pic_url){



        if (CheckIsDataAlreadyInDBorNot(ChatContacts_Table,Loggedin_User_Contact_Username,username)){
            Log.i("ChatBoxDBHelper","Contact Already Exsits");


        }else {

           String ContactChatTableName1 = genRandomString(10,AlphaNumaricString);

            ContactChatTableName = ContactChatTableName1;


            final String CREATE_User_Chat_Contacts_Table = "CREATE TABLE " + ContactChatTableName1 + " ( " + ChatBox_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ChatBox_Sender + " TEXT NOT NULL, " + ChatBox_Recipient + " TEXT NOT NULL, " + ChatBox_Other_Thumb_IMG + " TEXT, " + ChatBox_My_Thumb_IMG + " TEXT, " + ChatBox_WHICH + " INTEGER, "
                    + ChatBox_MsgType + " TEXT NOT NULL, " + ChatBox_Message + " TEXT NOT NULL, " + ChatBox_UnixTimeStamp + " TEXT NOT NULL);";


            SQLiteDatabase chatBoxDatabase = this.getWritableDatabase();

            chatBoxDatabase.execSQL(CREATE_User_Chat_Contacts_Table);


            ContentValues contentValues  = new ContentValues();
            contentValues.put(Loggedin_User_Contact_Fullname,fullname);
            contentValues.put(Loggedin_User_Contact_Username,username);
            contentValues.put(Loggedin_User_Contact_Email,email);
            contentValues.put(Loggedin_User_Contact_UniqueId,UniqueId);
            contentValues.put(Loggedin_User_Contact_ChatTable,ContactChatTableName);
            contentValues.put(Loggedin_User_Contact_Profile_pic_url,profile_pic_url);
            contentValues.put(Loggedin_User_Contact_Thumb_pic_url,thumb_pic_url);

            chatBoxDatabase.insert(ChatContacts_Table,null,contentValues);

        }


        return ContactChatTableName;
    }

    public static String genRandomString(final int sizeofstring,String AllowedChar){


        final Random random= new Random();
        final StringBuilder sb = new StringBuilder(sizeofstring);
        for(int i=1; i<=sizeofstring;i++){

            sb.append(AllowedChar.charAt(random.nextInt(AllowedChar.length())));

        }

        return sb.toString();
    }


    public Cursor getUserContactsDetails() {

        String selectQuery = "SELECT * FROM " + ChatContacts_Table;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);

        return cursor;

    }

    public void SaveSentRecivedMSG(String Tablename, String sender, String recipient, String msg, String MsgType, String timeStamp, int sorr, String sender_thumb,String recipient_thumb){

        ContentValues contentValues  = new ContentValues();
        contentValues.put(ChatBox_Sender,sender);
        contentValues.put(ChatBox_Recipient,recipient);
        contentValues.put(ChatBox_WHICH,sorr);
        contentValues.put(ChatBox_Message,msg);
        contentValues.put(ChatBox_Other_Thumb_IMG,recipient_thumb);
        contentValues.put(ChatBox_My_Thumb_IMG,sender_thumb);
        contentValues.put(ChatBox_MsgType,MsgType);
        contentValues.put(ChatBox_UnixTimeStamp,timeStamp);

        chatBoxDatabase.insert(Tablename,null,contentValues);
    }


    public Cursor getUserChatHistory(String Recipient_UserChatTable) {

        String selectQuery = "SELECT * FROM " + Recipient_UserChatTable;// + " WHERE " + ChatBox_Recipient + "= \"" + recipient + "\"";
       // String selectwQuery = "SELECT * FROM " + Chat_History_Table + " WHERE " + ChatBox_Recipient + " = \"" + recipient + "\" AND " + ChatBox_Sender + " = \"" + sender + "\"" + " OR "+ ChatBox_Recipient + " = \"" + sender + "\" AND " + ChatBox_Sender + " = \"" + recipient + "\"";

        Log.i("Query String to msg: ",selectQuery);

        SQLiteDatabase db  = this.getReadableDatabase();

       // String[] columnNames = new String[] {ChatBox_ID,ChatBox_Sender, ChatBox_Recipient, ChatBox_WHICH, ChatBox_Message, ChatBox_Other_Thumb_IMG, ChatBox_My_Thumb_IMG,ChatBox_MsgType,ChatBox_UnixTimeStamp};
        //String whereClause = ChatBox_Recipient + "= \"" + recipient + "\"";

        return db.rawQuery(selectQuery, null);  //db.query(Chat_History_Table,columnNames,whereClause,null,null,null,null);

    }


    public void deleteContactfromdb(int CID, String CTable) {

        String selectQuery = "DELETE FROM " + ChatContacts_Table + " WHERE " + Loggedin_User_Contact_ID + " = " + CID;  // + " WHERE " + ChatBox_Recipient + "= \"" + recipient + "\"";
       // String selectwQuery = "SELECT * FROM " + Chat_History_Table + " WHERE " + ChatBox_Recipient + " = \"" + recipient + "\" AND " + ChatBox_Sender + " = \"" + sender + "\"";

        Log.i("ALL Query String to msg",selectQuery);

        SQLiteDatabase db  = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + CTable);
        // String[] columnNames = new String[] {ChatBox_ID,ChatBox_Sender, ChatBox_Recipient, ChatBox_WHICH, ChatBox_Message, ChatBox_Other_Thumb_IMG, ChatBox_My_Thumb_IMG,ChatBox_MsgType,ChatBox_UnixTimeStamp};
        //String whereClause = ChatBox_Recipient + "= \"" + recipient + "\"";
        db.execSQL(selectQuery);

        //return cursor;  //db.query(Chat_History_Table,columnNames,whereClause,null,null,null,null);

    }

    public void deleteMessagefromdb(int CID, String ContactTable) {

        String selectQuery = "DELETE FROM " + ContactTable + " WHERE " + ChatBox_ID + " = " + CID;  // + " WHERE " + ChatBox_Recipient + "= \"" + recipient + "\"";
        // String selectwQuery = "SELECT * FROM " + Chat_History_Table + " WHERE " + ChatBox_Recipient + " = \"" + recipient + "\" AND " + ChatBox_Sender + " = \"" + sender + "\"";

        Log.i("ALL Query String to msg",selectQuery);

        SQLiteDatabase db  = this.getWritableDatabase();
        //db.execSQL("DROP TABLE IF EXISTS " + CTable);
        // String[] columnNames = new String[] {ChatBox_ID,ChatBox_Sender, ChatBox_Recipient, ChatBox_WHICH, ChatBox_Message, ChatBox_Other_Thumb_IMG, ChatBox_My_Thumb_IMG,ChatBox_MsgType,ChatBox_UnixTimeStamp};
        //String whereClause = ChatBox_Recipient + "= \"" + recipient + "\"";
        db.execSQL(selectQuery);

        //return cursor;  //db.query(Chat_History_Table,columnNames,whereClause,null,null,null,null);

    }



    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue) {

        SQLiteDatabase db  = this.getReadableDatabase();

        String Query = "Select * from " + TableName + " where " + dbfield + " = \"" + fieldValue + "\"";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
