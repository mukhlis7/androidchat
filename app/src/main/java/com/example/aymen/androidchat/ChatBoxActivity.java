package com.example.aymen.androidchat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import needle.Needle;
import needle.UiRelatedTask;

import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_ID;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_Message;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_MsgType;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_UnixTimeStamp;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_WHICH;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_ChatTable;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Email;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Fullname;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Profile_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Thumb_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Token;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Username;

public class ChatBoxActivity extends AppCompatActivity {
    public ListView myListView ;
    public static String uniqueId;
    public static String Recipient_UniqueId;
    public static String fullname;
    public static String Recipient_Fullname;
    public static String email;
    public static String Recipient_Email;
    public static String profile_thumb_pic;
    public static String Recipient_profile_thumb_pic;
    public ListView messageslist ;
    public ChatBoxAdapter chatBoxAdapter;
    public static String Userconnected;
    public static boolean isnewUserconnected = false;
    public  EditText messagetxt ;
    public  ImageButton send ;
    public ImageButton additems ;
    private Toolbar toolbar;
    private Thread thread2;
    private boolean startTyping = false;
    private int time = 2;

    //declare socket object
    private Socket socket;

    private TextView toolbar_title;
    private TextView toolbar_subtitle;
    private CircleImageView toolbar_image;
    private BottomSheetDialog bottomSheetDialog;
    private ChatBoxDBHelper chatBoxDBHelper;
    private static final int PICK_IMAGE_REQUEST = 1001;
    public static String Nickname;
    public static String profile_pic;
    public String Recipient_Username;
    public  String Recipient_UserChatTable;
    private static final String SIOURL = "http://rt-chat07.herokuapp.com/";
    private static final String TranlationURL = "http://rt-chat07.herokuapp.com/translatetext";
    //private static final String SIOURL = "http://rt-chat07.herokuapp.com/";
   // private static final String SIOURL = "http://192.168.43.38/";
    //private static final String SIOURL = "http://192.168.12.1/";
    private ArrayList<Message> mArrayList;
    private String imageString;
    private String thumb_imageString;

    private Uri imgresultUri;
    String imgPath, fileName;


    Bitmap bitmap;
    Bitmap thumb_bitmap;

    private EditText edittxttranslated;
    private EditText edittxttranslate;
    Button btn_popup;

    String[] title;
    String[] title_short;
    String[] title_auto;
    String[] title_short_auto;
    private ProgressDialog progressDialog;
    String spinner_item;
    String selected_lang_short;

    int autotranslate = 0;
    SpinnerAdapter adapterfrom;
    SpinnerAdapter adapterto;


    @SuppressLint("HandlerLeak")
    Handler handler2=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //Log.i(TAG, "handleMessage: typing stopped " + startTyping);
            if(time == 0){
                toolbar_subtitle.setText(Recipient_Username);
                //Log.i(TAG, "handleMessage: typing stopped time is " + time);
                startTyping = false;
                time = 2;

                synchronized (handler2) {


                    try {
                        wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                    //Log.i(TAG, "run: typing " + time);

                //handler3.sendEmptyMessage(0);

            }

        }
    };


    @SuppressLint("HandlerLeak")
    Handler handler3=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //Log.i(TAG, "handleMessage: typing stopped " + startTyping);
            if(time == 0){
                toolbar_subtitle.setText("Android Chat");
                //Log.i(TAG, "handleMessage: typing stopped time is " + time);
                startTyping = false;
                time = 2;
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);


        //GetUsernameAlertDialog();

        messagetxt = (EditText) findViewById(R.id.message) ;
        send = (ImageButton)findViewById(R.id.send);
        additems = (ImageButton)findViewById(R.id.btn_add);

        messageslist = findViewById(R.id.messageslistView);


        if (messagetxt.getText().toString().trim().length() > 0) {
            send.setEnabled(true);
        } else {
            send.setEnabled(false);
        }


        // get the nickame of the user
        Intent intent = getIntent();
        //String id = intent.getStringExtra("id");
        Recipient_Username = intent.getStringExtra("username");
        Recipient_UniqueId = intent.getStringExtra("public_id");
        Recipient_Fullname = intent.getStringExtra("fullname");
        Recipient_Email = intent.getStringExtra("email");
        Recipient_UserChatTable = intent.getStringExtra("UserChatTable");
        Recipient_profile_thumb_pic = intent.getStringExtra("profile_thumb_pic");
        final String Recipient_profile_pic = intent.getStringExtra("profile_pic");
        //Nickname= (String) Objects.requireNonNull(getIntent().getExtras()).getString("username");

        chatBoxDBHelper = new ChatBoxDBHelper(getApplicationContext());

        chatBoxDBHelper.open();

        String UserChatTable = chatBoxDBHelper.addDataToChatContacts_Table(Recipient_Fullname,Recipient_Username,Recipient_Email,Recipient_UniqueId,Recipient_profile_thumb_pic,Recipient_profile_pic);

        Log.i("Chat bOx ACtivity","Contact Data Added to ChatContacts_Table");


        if (Recipient_UserChatTable == null) {
            Recipient_UserChatTable = UserChatTable;
        }

        Cursor cu = chatBoxDBHelper.getLoggedinUserDetails();


        if (cu.moveToFirst()){
            do{
                Nickname = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Username));
                uniqueId = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Token));
                profile_thumb_pic = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Thumb_pic_url));
                profile_pic = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Profile_pic_url));
                fullname = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Fullname));
                email = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Email));

            }while(cu.moveToNext());
        }
        cu.close();
        chatBoxDBHelper.close();



            // thumburl = jsonObject.getString("_Thumb_pic_URL");

            //current_user_fullname.setText(jsonArray.getString(1));


        //SharedPreferences prefs = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE);


        toolbar = findViewById(R.id.chat_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);



        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = inflater.inflate(R.layout.chatbox_custom_bar,null);

        actionBar.setCustomView(action_bar_view);

        toolbar_title = findViewById(R.id.chatbox_custom_Title);
        toolbar_subtitle = findViewById(R.id.chatbox_custom_subTitle);
        toolbar_image = findViewById(R.id.chatbox_custom_image);
        progressDialog = new ProgressDialog(ChatBoxActivity.this);

        toolbar_title.setText(Recipient_Fullname);
        toolbar_subtitle.setText(Recipient_Username);
        Picasso.get().load(Recipient_profile_thumb_pic).placeholder(R.drawable.ic_male).into(toolbar_image);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        onTypeButtonEnable();

        mArrayList = new ArrayList<Message>();
        //toolbar.setTitle("HElll");



        Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
            @Override
            protected Integer doWork() {

                chatBoxDBHelper.open();
                if (Recipient_UserChatTable!=null) {

                    Cursor chatDetailsoncreate = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);

                    if(chatDetailsoncreate != null && chatDetailsoncreate.getCount() > 0) {
                        mArrayList.clear();
                        for (chatDetailsoncreate.moveToFirst(); !chatDetailsoncreate.isAfterLast(); chatDetailsoncreate.moveToNext()) {
                            // The Cursor is now set to the right position
                            Message testModel = new Message(
                                    Nickname,
                                    Recipient_Username,
                                    chatDetailsoncreate.getString(chatDetailsoncreate.getColumnIndexOrThrow(ChatBox_Message)),
                                    profile_thumb_pic,
                                    Recipient_profile_thumb_pic,
                                    chatDetailsoncreate.getString(chatDetailsoncreate.getColumnIndexOrThrow(ChatBox_MsgType)),
                                    chatDetailsoncreate.getLong(chatDetailsoncreate.getColumnIndexOrThrow(ChatBox_UnixTimeStamp)),
                                    chatDetailsoncreate.getInt(chatDetailsoncreate.getColumnIndexOrThrow(ChatBox_ID)),
                                    chatDetailsoncreate.getInt(chatDetailsoncreate.getColumnIndexOrThrow(ChatBox_WHICH))


                            );
                            mArrayList.add(testModel);
                        }
                        chatDetailsoncreate.close();

                    }
                    Log.i("user Chat History : ", mArrayList.toString());
                }

                int result = 1+2;
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(Integer result) {
                //mSomeTextView.setText("result: " + result);

                chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this,R.id.my_msg_item_layout, mArrayList);

                // Attach cursor adapter to the ListView
                messageslist.setAdapter(chatBoxAdapter);
                messageslist.setSelection(chatBoxAdapter.getCount() - 1);
                chatBoxDBHelper.close();

            }
        });


        messagetxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean typing = true;
                //String username = Nickname;
                //String uniqueId = uniqueId;
                String data = "{\n"+
                        "   \"typing\": \"" + typing + "\",\n" +
                        "   \"sender\": \"" + Nickname + "\",\n" +
                        "   \"recipient\": \"" + Recipient_Username + "\",\n" +
                        "   \"sender_uniqueId\": \"" + uniqueId + "\"\n" +
                        "}";


                try {
                    JSONObject Jsonobj = new JSONObject(data);

                    socket.emit("on typingprivate", Jsonobj);

                    //Toast.makeText(getApplicationContext(),Jsonobj.toString(),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        try {
            socket = IO.socket(SIOURL);
            socket.connect();
            socket.emit("join", Nickname);
            socket.on("on typingprivate", onTyping);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        } catch (URISyntaxException e) {
            e.printStackTrace();

        }


        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                title = getResources().getStringArray(R.array.longlanguages);
                title_short = getResources().getStringArray(R.array.shortlanguages);

                title_auto = getResources().getStringArray(R.array.longlanguagesauto);
                title_short_auto = getResources().getStringArray(R.array.shortlanguagesauto);

                btn_popup = (Button) findViewById(R.id.button1);

                adapterfrom = new SpinnerAdapter(getApplicationContext(),title_auto);
                adapterto = new SpinnerAdapter(getApplicationContext(), title);

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChatBoxActivity.this);
                //builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Select Translation Mode:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChatBoxActivity.this, R.layout.chatbox_dialog_options);
                arrayAdapter.add("Manual Translation");
                arrayAdapter.add("OTW Translation");
               // arrayAdapter.add("Jignesh");
               // arrayAdapter.add("Umang");
               // arrayAdapter.add("Gatti");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        if (strName.equals("Manual Translation")) {

                            // TODO Auto-generated method stub
                            final Dialog dialogcustom = new Dialog(ChatBoxActivity.this);
                            dialogcustom.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogcustom.setContentView(R.layout.chatboxcustomdialogbox);
                            dialogcustom.setCancelable(false);

                            // set the custom dialog components - text, image and button
                            final Spinner spinner = (Spinner) dialogcustom.findViewById(R.id.spinner1);
                            final Spinner spinner2 = (Spinner) dialogcustom.findViewById(R.id.spinner12);
                            edittxttranslate = (EditText) dialogcustom.findViewById(R.id.editText1);
                            edittxttranslated = (EditText) dialogcustom.findViewById(R.id.editText12);
                            Button button = (Button) dialogcustom.findViewById(R.id.button1);
                            Button btntranslate = (Button) dialogcustom.findViewById(R.id.button2);
                            Button btnsend = (Button) dialogcustom.findViewById(R.id.button3);

                            spinner.setAdapter(adapterfrom);
                            spinner2.setAdapter(adapterto);

                            edittxttranslate.setText(messagetxt.getText().toString().trim());

                            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    selected_lang_short = title_short[i];

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // TODO Auto-generated method stub
                                    spinner_item = title_short_auto[position];

                                    if (spinner_item.equals("auto")){

                                        autotranslate = 1;

                                    }else {
                                        autotranslate = 0;

                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // TODO Auto-generated method stub

                                }
                            });

                            btntranslate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    progressDialog.setMessage("Translating, please wait...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    String texttotranslate = edittxttranslate.getText().toString().trim();

                                    if (texttotranslate.length() > 0){

                                        String data = "{\n"+
                                                "   \"auto\": \"" + autotranslate + "\",\n" +
                                                "   \"from_lang\": \"" + spinner_item + "\",\n" +
                                                "   \"to_lang\": \"" + selected_lang_short + "\",\n" +
                                                "   \"text\": \"" + texttotranslate + "\"\n" +
                                                "}";


                                        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();

                                        Log.i("Data from DialogBox", data);

                                        Submittext(data);

                                    }else {

                                        edittxttranslate.setError("Plz, Enter A text!");

                                    }

                                }
                            });

                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    dialogcustom.dismiss();
                                    //Toast.makeText(getApplicationContext(), spinner_item + " - " + edittxttranslate.getText().toString().trim(), Toast.LENGTH_LONG).show();
                                }
                            });


                            btnsend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                   // String msgtxt  = edittxttranslated.getText().toString().trim();

                                    if(!edittxttranslated.getText().toString().trim().isEmpty()){

                                        final String msg = edittxttranslated.getText().toString();

                                        final Cursor chatDetails = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);


                                        String data = "{\n"+
                                                "   \"msg\": \"" + msg + "\",\n" +
                                                "   \"sender\": \"" + Nickname + "\",\n" +
                                                "   \"msgType\": \"" + "text" + "\",\n" +
                                                "   \"recipient\": \"" + Recipient_Username + "\",\n" +
                                                "   \"sender_fullname\": \"" + fullname + "\",\n" +
                                                "   \"sender_profile_pic_url\": \"" + profile_pic + "\",\n" +
                                                "   \"sender_email\": \"" + email + "\",\n" +
                                                "   \"sender_uniqueId\": \"" + uniqueId + "\"\n" +
                                                "}";

                                        try {
                                            JSONObject Jsonobj = new JSONObject(data);
                                            //socket.emit("entity", obj);
                                            socket.emit("messagedetectionprivate",Jsonobj);
                                            //Toast.makeText(getApplicationContext(),Jsonobj.toString(),Toast.LENGTH_LONG).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        messagetxt.setText("");
                                        dialogcustom.dismiss();

                                        //String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getApplicationContext());

                                        Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
                                            @Override
                                            protected Integer doWork() {

                                                long unixTime = System.currentTimeMillis() / 1000L;

                                                ChatBoxDBHelper chatBoxDBHelperSmsg = new ChatBoxDBHelper(getApplicationContext());
                                                chatBoxDBHelper.open();
                                                chatBoxDBHelperSmsg.open();
                                                // make instance of message
                                                chatBoxDBHelperSmsg.SaveSentRecivedMSG(Recipient_UserChatTable, Nickname,Recipient_Username,msg,"text",Long.toString(unixTime),0,profile_thumb_pic,Recipient_profile_thumb_pic);

                                                Cursor chatDetails1 = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);

                                                //ArrayList<Message> mArrayList = new ArrayList<Message>();
                                                mArrayList.clear();

                                                for(chatDetails1.moveToFirst(); !chatDetails1.isAfterLast(); chatDetails1.moveToNext()) {
                                                    // The Cursor is now set to the right position
                                                    Message testModel = new Message(
                                                            Nickname,
                                                            Recipient_Username,
                                                            chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_Message)),
                                                            profile_thumb_pic,
                                                            Recipient_profile_thumb_pic,
                                                            chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_MsgType)),
                                                            chatDetails1.getLong(chatDetails1.getColumnIndexOrThrow(ChatBox_UnixTimeStamp)),
                                                            chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_ID)),
                                                            chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_WHICH))
                                                    );
                                                    mArrayList.add(testModel);
                                                }
                                                chatBoxDBHelper.close();
                                                chatBoxDBHelperSmsg.close();

                                                Log.i("user Chat History : ", mArrayList.toString());


                                                int result = 1+2;
                                                return result;
                                            }

                                            @Override
                                            protected void thenDoUiRelatedWork(Integer result) {


                                                chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this,R.id.my_msg_item_layout, mArrayList);

                                                // Attach cursor adapter to the ListView
                                                messageslist.setAdapter(chatBoxAdapter);
                                                messageslist.setSelection(chatBoxAdapter.getCount() - 1);
                                                //mSomeTextView.setText("result: " + result);
                                            }
                                        });


                                    }

                                }
                            });


                            dialogcustom.show();


                        }
                    }
                });
                builderSingle.show();

                return true;
            }
        });


        messageslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChatBoxActivity.this);
               // builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Select Option:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChatBoxActivity.this, R.layout.chatbox_dialog_options);
                arrayAdapter.add("Delete Message");
                arrayAdapter.add("Copy Message");
                arrayAdapter.add("ReSend Message");
               // arrayAdapter.add("Umang");
                //arrayAdapter.add("Gatti");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if (strName.equals("Delete Message")){

                            AlertDialog.Builder builderInner = new AlertDialog.Builder(ChatBoxActivity.this);
                            builderInner.setMessage(strName);
                            builderInner.setTitle("Your Selected Item is");
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {

                                    Message msg = mArrayList.get(position);

                                    int trg_msg_id = msg.getID();

                                    chatBoxDBHelper.open();
                                    chatBoxDBHelper.deleteMessagefromdb(trg_msg_id,Recipient_UserChatTable);


                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {

                                            Cursor chatDetails;

                                            if (Recipient_UserChatTable!=null) {
                                                chatDetails = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);
                                                mArrayList.clear();

                                                for(chatDetails.moveToFirst(); !chatDetails.isAfterLast(); chatDetails.moveToNext()) {
                                                    // The Cursor is now set to the right position
                                                    Message testModel = new Message(
                                                            Nickname,
                                                            Recipient_Username,
                                                            chatDetails.getString(chatDetails.getColumnIndexOrThrow(ChatBox_Message)),
                                                            profile_thumb_pic,
                                                            Recipient_profile_thumb_pic,
                                                            chatDetails.getString(chatDetails.getColumnIndexOrThrow(ChatBox_MsgType)),
                                                            chatDetails.getLong(chatDetails.getColumnIndexOrThrow(ChatBox_UnixTimeStamp)),
                                                            chatDetails.getInt(chatDetails.getColumnIndexOrThrow(ChatBox_ID)),
                                                            chatDetails.getInt(chatDetails.getColumnIndexOrThrow(ChatBox_WHICH))


                                                    );
                                                    mArrayList.add(testModel);
                                                }

                                                Log.i("user Chat History : ", mArrayList.toString());


                                                //Log.i("Cursor is not EMp:",chatDetails.toString());

                                                chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this,R.id.my_msg_item_layout, mArrayList);

                                                // Attach cursor adapter to the ListView
                                                messageslist.setAdapter(chatBoxAdapter);
                                                messageslist.setSelection(chatBoxAdapter.getCount() - 1);

                                            }

                                        }


                                    });

                                    chatBoxDBHelper.close();
                                }
                            });

                            builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }else if (strName.equals("Copy Message")){


                            Message msg = mArrayList.get(position);

                            String trg_msg = msg.getMessage();


                            setClipboard(getApplicationContext(),trg_msg);

                            Toast.makeText(getApplicationContext(),"Copied To Clipboard",Toast.LENGTH_LONG).show();

                        }

                    }
                });
                builderSingle.show();

                return true;
            }
        });

        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if(!messagetxt.getText().toString().trim().isEmpty()){

                    final String msg = messagetxt.getText().toString();

                    boolean fistchat = false;

                    final Cursor chatDetails = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);

                    if (chatDetails.getCount() == 0){

                        fistchat = true;


                    }


                    String data = "{\n"+
                            "   \"msg\": \"" + msg + "\",\n" +
                            "   \"sender\": \"" + Nickname + "\",\n" +
                            "   \"msgType\": \"" + "text" + "\",\n" +
                            "   \"recipient\": \"" + Recipient_Username + "\",\n" +
                            "   \"sender_fullname\": \"" + fullname + "\",\n" +
                            "   \"sender_profile_pic_url\": \"" + profile_pic + "\",\n" +
                            "   \"sender_email\": \"" + email + "\",\n" +
                            "   \"sender_uniqueId\": \"" + uniqueId + "\"\n" +
                            "}";

                    try {
                        JSONObject Jsonobj = new JSONObject(data);
                        //socket.emit("entity", obj);
                        socket.emit("messagedetectionprivate",Jsonobj);
                        //Toast.makeText(getApplicationContext(),Jsonobj.toString(),Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    messagetxt.setText("");




                    //String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getApplicationContext());



                    Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
                        @Override
                        protected Integer doWork() {

                            long unixTime = System.currentTimeMillis() / 1000L;

                            ChatBoxDBHelper chatBoxDBHelperSmsg = new ChatBoxDBHelper(getApplicationContext());
                            chatBoxDBHelper.open();
                            chatBoxDBHelperSmsg.open();
                            // make instance of message
                            chatBoxDBHelperSmsg.SaveSentRecivedMSG(Recipient_UserChatTable, Nickname,Recipient_Username,msg,"text",Long.toString(unixTime),0,profile_thumb_pic,Recipient_profile_thumb_pic);

                            Cursor chatDetails1 = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);

                            //ArrayList<Message> mArrayList = new ArrayList<Message>();
                            mArrayList.clear();

                            for(chatDetails1.moveToFirst(); !chatDetails1.isAfterLast(); chatDetails1.moveToNext()) {
                                // The Cursor is now set to the right position
                                Message testModel = new Message(
                                        Nickname,
                                        Recipient_Username,
                                        chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_Message)),
                                        profile_thumb_pic,
                                        Recipient_profile_thumb_pic,
                                        chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_MsgType)),
                                        chatDetails1.getLong(chatDetails1.getColumnIndexOrThrow(ChatBox_UnixTimeStamp)),
                                        chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_ID)),
                                        chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_WHICH))
                                );
                                mArrayList.add(testModel);
                            }
                            chatBoxDBHelper.close();
                            chatBoxDBHelperSmsg.close();

                            Log.i("user Chat History : ", mArrayList.toString());


                            int result = 1+2;
                            return result;
                        }

                        @Override
                        protected void thenDoUiRelatedWork(Integer result) {


                            chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this,R.id.my_msg_item_layout, mArrayList);

                            // Attach cursor adapter to the ListView
                            messageslist.setAdapter(chatBoxAdapter);
                            messageslist.setSelection(chatBoxAdapter.getCount() - 1);
                            //mSomeTextView.setText("result: " + result);
                        }
                    });


                }

            }
        });

        additems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog = new BottomSheetDialog(
                        ChatBoxActivity.this,R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.chatbox_bottom_sheet,
                (LinearLayout)findViewById(R.id.chatbox_bottom_container));

                bottomSheetView.findViewById(R.id.add_photo_from_gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // start picker to get image for cropping and then use the image in cropping activity
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ChatBoxActivity.this);
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.getDismissWithAnimation();

                bottomSheetDialog.show();

            }
        });



        //implementing socket listeners
        socket.on("userjoinedthechat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String data = (String) args[0];

                        isnewUserconnected = true;

                        //Userconnected = "";


//                        Message m = new Message("","","",0);


                       // MessageList.add(m);
                        //Userconnected = data;


                      //  chatBoxAdapter.notifyDataSetChanged();

                        //set the adapter for the recycler view

                        //myListView.setAdapter(chatBoxAdapter);

                        Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        socket.on("userdisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];

                        //Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });



        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];


                            Log.i("Data For MSG",data.toString());
                            //extract data from fired event

                            String susername = data.getString("sender");
                            String message = data.getString("msg");
                            String recipient = data.getString("recipient");
                            String fullname = data.getString("fullname");
                            String email = data.getString("email");
                            String profile_pic = data.getString("profile_pic_url");
                            String sender_uniqueId = data.getString("uniqueId");
                            String msgType = data.getString("msgType");

                            long unixTime = System.currentTimeMillis() / 1000L;

                            //Toast.makeText(getApplicationContext(),unixTime+"",Toast.LENGTH_LONG).show();

                            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getApplicationContext());

                           // Toast.makeText(getApplicationContext(),inshort+"",Toast.LENGTH_LONG).show();

                            final ChatBoxDBHelper chatBoxDBHelperRmsg = new ChatBoxDBHelper(getApplicationContext());
                            chatBoxDBHelperRmsg.open();

                            chatBoxDBHelperRmsg.SaveSentRecivedMSG(Recipient_UserChatTable, susername,Nickname,message,msgType,Long.toString(unixTime),1,profile_thumb_pic,Recipient_profile_thumb_pic);


                            Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
                                @Override
                                protected Integer doWork() {


                                    chatBoxDBHelper.open();
                                    Cursor chatDetails1 = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);

                                    //  ArrayList<Message> mArrayList = new ArrayList<Message>();
                                    mArrayList.clear();

                                    for(chatDetails1.moveToFirst(); !chatDetails1.isAfterLast(); chatDetails1.moveToNext()) {
                                        // The Cursor is now set to the right position
                                        Message testModel = new Message(
                                                Nickname,
                                                Recipient_Username,
                                                chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_Message)),
                                                profile_thumb_pic,
                                                Recipient_profile_thumb_pic,
                                                chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_MsgType)),
                                                chatDetails1.getLong(chatDetails1.getColumnIndexOrThrow(ChatBox_UnixTimeStamp)),
                                                chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_ID)),
                                                chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_WHICH))
                                        );
                                        mArrayList.add(testModel);
                                    }
                                    chatBoxDBHelperRmsg.open();
                                    chatBoxDBHelper.close();

                                    Log.i("user Chat History : ", mArrayList.toString());

                                    int result = 1+2;
                                    return result;

                                }

                                @Override
                                protected void thenDoUiRelatedWork(Integer result) {


                                    chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this,R.id.my_msg_item_layout, mArrayList);

                                    // Attach cursor adapter to the ListView
                                    messageslist.setAdapter(chatBoxAdapter);
                                    messageslist.setSelection(chatBoxAdapter.getCount() - 1);


                                }
                            });

/*
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {



                                }
                            });


 */

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }

    private void Submittext(String data)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String finalSavedata = data;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TranlationURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();

                }
               // btnlogin.setEnabled(true);
                // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Log.i("Data from login",response);

                try {
                    JSONObject objres = new JSONObject(response);

                    boolean isregistered = objres.getBoolean("result");
                    //String message = objres.getString("message");

                    if (isregistered) {

                        String translatedtextfromweb = objres.getString("text");
                        edittxttranslated.setText(translatedtextfromweb);

                    }else {

                        Toast.makeText(getApplicationContext(),"Unable to Translate",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    //btnlogin.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Unable to parse Json",Toast.LENGTH_LONG).show();

                    //Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //btnlogin.setEnabled(true);
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Server Error!", Toast.LENGTH_SHORT).show();

                //Log.v("VOLLEY", error.toString());
            }
        }) {


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return finalSavedata == null ? null : finalSavedata.getBytes(StandardCharsets.UTF_8);
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()){
            bottomSheetDialog.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off("on typingprivate", onTyping);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
    }


    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    public void GetTranslationAlertDialog(){



    }


    public void GetUsernameAlertDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatBoxActivity.this);
        alertDialog.setTitle("Enter Username");
        //alertDialog.setMessage("Enter Username");

        final EditText input = new EditText(ChatBoxActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        // alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Nickname = "";
                        Nickname = input.getText().toString().trim();
                        socket.emit("join", Nickname);
                        socket.on("on typingprivate", onTyping);
                        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }


    public void onTypeButtonEnable(){


        messagetxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() > 0) {
                    send.setEnabled(true);
                } else {
                    send.setEnabled(false);
                }

/*
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // my_button.setBackgroundResource(R.drawable.defaultcard);
                    }
                }, 500);

 */


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }


    Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                     Log.i("ChatBoxActivity", "run: " + args[0]);
                    try {
                        Boolean typingOrNot = data.getBoolean("typing");
                        String userName = data.getString("sender");
                        userName = userName  + " is Typing...";
                        String id = data.getString("uniqueId");

                        if(id.equals(uniqueId)){
                            typingOrNot = false;
                        }else {
                            toolbar_subtitle.setText(userName);
                        }

                        if(typingOrNot){

                            if(!startTyping){
                                startTyping = true;
                                thread2=new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                while(time > 0) {
                                                    synchronized (this){
                                                        try {
                                                            wait(1000);
                                                            //Log.i(TAG, "run: typing " + time);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        time--;
                                                    }
                                                    handler2.sendEmptyMessage(0);
                                                }

                                            }
                                        }
                                );
                                thread2.start();
                            }else {
                                time = 2;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };



    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.e(TAG, "Error connecting");
                   // Toast.makeText(getApplicationContext(), "Failed to connect to Sever!", Toast.LENGTH_LONG).show();
                }
            });
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imgresultUri = result.getUri();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgresultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final File imagefile = new File(String.valueOf(imgresultUri));


                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            bitmap = new Compressor(ChatBoxActivity.this)

                                    .setQuality(50)
                                    .compressToBitmap(imagefile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });


                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();

                //long imagesize = imageBytes.length;

                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                Log.i("Image Base64 String: ", imageString);

                chatBoxDBHelper.open();

                long unixTime = System.currentTimeMillis() / 1000L;

                chatBoxDBHelper.SaveSentRecivedMSG(Recipient_UserChatTable, Nickname, Recipient_Username, imageString, "image", Long.toString(unixTime), 0, profile_thumb_pic, Recipient_profile_thumb_pic);

                String imgdata = "{\n"+
                        "   \"msg\": \"" + imageString + "\",\n" +
                        "   \"sender\": \"" + Nickname + "\",\n" +
                        "   \"msgType\": \"" + "image" + "\",\n" +
                        "   \"recipient\": \"" + Recipient_Username + "\",\n" +
                        "   \"sender_fullname\": \"" + fullname + "\",\n" +
                        "   \"sender_profile_pic_url\": \"" + profile_pic + "\",\n" +
                        "   \"sender_email\": \"" + email + "\",\n" +
                        "   \"sender_uniqueId\": \"" + uniqueId + "\"\n" +
                        "}";

                try {
                    JSONObject Jsonobj = new JSONObject(imgdata);
                    //socket.emit("entity", obj);
                    socket.emit("messagedetectionprivate",Jsonobj);
                    //Toast.makeText(getApplicationContext(),Jsonobj.toString(),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
                    @Override
                    protected Integer doWork() {

                        Cursor chatDetails1 = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);

                        // ArrayList<Message> mArrayList = new ArrayList<Message>();
                        mArrayList.clear();

                        for(chatDetails1.moveToFirst(); !chatDetails1.isAfterLast(); chatDetails1.moveToNext()) {
                            // The Cursor is now set to the right position
                            Message testModel = new Message(
                                    Nickname,
                                    Recipient_Username,
                                    chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_Message)),
                                    profile_thumb_pic,
                                    Recipient_profile_thumb_pic,
                                    chatDetails1.getString(chatDetails1.getColumnIndexOrThrow(ChatBox_MsgType)),
                                    chatDetails1.getLong(chatDetails1.getColumnIndexOrThrow(ChatBox_UnixTimeStamp)),
                                    chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_ID)),
                                    chatDetails1.getInt(chatDetails1.getColumnIndexOrThrow(ChatBox_WHICH))
                            );
                            mArrayList.add(testModel);
                        }
                        chatBoxDBHelper.close();

                        Log.i("user Chat History : ", mArrayList.toString());

                        int result = 1+2;
                        return result;
                    }

                    @Override
                    protected void thenDoUiRelatedWork(Integer result) {


                        chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this,R.id.my_msg_item_layout, mArrayList);

                        // Attach cursor adapter to the ListView
                        messageslist.setAdapter(chatBoxAdapter);
                        messageslist.setSelection(chatBoxAdapter.getCount() - 1);


                        //mSomeTextView.setText("result: " + result);
                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
/*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                Uri filePath = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File imagefile = new File(String.valueOf(filePath));

                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();

                //long imagesize = imageBytes.length;

                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                Log.i("Image Base64 String: ", imageString);


                long unixTime = System.currentTimeMillis() / 1000L;

                chatBoxDBHelper.SaveSentRecivedMSG(Recipient_UserChatTable, Nickname,Recipient_Username,imageString,"image",Long.toString(unixTime),0,profile_thumb_pic,Recipient_profile_thumb_pic);

                Cursor chatDetails = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);
                //Cursor allUserchatDetails = chatBoxDBHelper.getallUserChatHistory();

                Log.i("user Chat History : ",DatabaseUtils.dumpCursorToString(chatDetails));
                // Log.i("All user Chat History",DatabaseUtils.dumpCursorToString(allUserchatDetails));

                if (chatDetails != null && chatDetails.getCount() > 0){

                    chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this, chatDetails,false);

                    // Attach cursor adapter to the ListView
                    messageslist.setAdapter(chatBoxAdapter);

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }





        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File imagefile = new File(String.valueOf(filePath));

            //converting image to base64 string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();

            //long imagesize = imageBytes.length;

            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Log.i("Image Base64 String: ", imageString);


            long unixTime = System.currentTimeMillis() / 1000L;

            chatBoxDBHelper.SaveSentRecivedMSG(Recipient_UserChatTable, Nickname,Recipient_Username,imageString,"image",Long.toString(unixTime),0,profile_thumb_pic,Recipient_profile_thumb_pic);

            Cursor chatDetails = chatBoxDBHelper.getUserChatHistory(Recipient_UserChatTable);
            //Cursor allUserchatDetails = chatBoxDBHelper.getallUserChatHistory();

            Log.i("user Chat History : ",DatabaseUtils.dumpCursorToString(chatDetails));
            // Log.i("All user Chat History",DatabaseUtils.dumpCursorToString(allUserchatDetails));

            if (chatDetails != null && chatDetails.getCount() > 0){

                chatBoxAdapter = new ChatBoxAdapter(ChatBoxActivity.this, chatDetails,false);

                // Attach cursor adapter to the ListView
                messageslist.setAdapter(chatBoxAdapter);

            }


        }


 */

    public class SpinnerAdapter extends BaseAdapter {
        Context context;
        String[] Strings;
        private LayoutInflater mInflater;

        public SpinnerAdapter(Context context, String[] strings) {
            this.context = context;
            this.Strings = strings;
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ListContent holder;
            View v = convertView;
            if (v == null) {
                mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.chatboxcustomdialogtextview, null);
                holder = new ListContent();
                holder.text = (TextView) v.findViewById(R.id.textcustomdialogchatbox);
                v.setTag(holder);
            } else {
                holder = (ListContent) v.getTag();
            }

            holder.text.setText(Strings[position]);

            return v;
        }
    }

    static class ListContent {
        TextView text;
    }


}
