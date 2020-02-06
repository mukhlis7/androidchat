package com.example.aymen.androidchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_ChatTable;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Email;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Fullname;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Profile_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Thumb_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Username;

public class ContactsAdapter extends CursorAdapter {


    public ContactsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contact_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        CircleImageView thumbprofileimageView = view.findViewById(R.id.Contact_profile_image);
        TextView UserFullname = view.findViewById(R.id.Contact_Fullname);
        TextView UserUsername = view.findViewById(R.id.Contact_Username);
        TextView Contact_TimeView = view.findViewById(R.id.Contact_TimeView);

        String fullname = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Fullname));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Username));

        String thumb_pic_url = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Thumb_pic_url));


        UserFullname.setText(fullname);
        UserUsername.setText(username);

        Picasso.get().load(thumb_pic_url).placeholder(R.drawable.ic_male).into(thumbprofileimageView);


    }
}
