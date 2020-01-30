package com.example.aymen.androidchat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Email;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Fullname;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Thumb_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Token;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Username;
import static com.example.aymen.androidchat.LoginActivity.SH_Email;
import static com.example.aymen.androidchat.LoginActivity.SH_Fullname;
import static com.example.aymen.androidchat.LoginActivity.SH_Loggedin_Data;
import static com.example.aymen.androidchat.LoginActivity.SH_Thumb_Profile_pic;
import static com.example.aymen.androidchat.LoginActivity.SH_UniqueId;
import static com.example.aymen.androidchat.LoginActivity.SH_Username;

public class ChatBoxActivity extends AppCompatActivity {
    public ListView myListView ;
    public static String uniqueId;
    public static String TouniqueId;
    public static String fullname;
    public static String Tofullname;
    public static String email;
    public static String Toemail;
    public static String profile_thumb_pic;
    public static String Toprofile_thumb_pic;
    public List<Message> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public static String Userconnected;
    public static boolean isnewUserconnected = false;
    public  EditText messagetxt ;
    public  Button send ;
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

    public static String Nickname;
    public static String ToNickname;
    private static final String SIOURL = "http://rt-chat07.herokuapp.com/";
    //private static final String SIOURL = "http://rt-chat07.herokuapp.com/";
   // private static final String SIOURL = "http://192.168.43.38/";



    @SuppressLint("HandlerLeak")
    Handler handler2=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            //Log.i(TAG, "handleMessage: typing stopped " + startTyping);
            if(time == 0){
                toolbar_subtitle.setText(ToNickname);
                //Log.i(TAG, "handleMessage: typing stopped time is " + time);
                startTyping = false;
                time = 2;

                try {
                    wait(4000);
                    //Log.i(TAG, "run: typing " + time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler3.sendEmptyMessage(0);

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
        send = (Button)findViewById(R.id.send);
        additems = (ImageButton)findViewById(R.id.btn_add);
        if (messagetxt.getText().toString().trim().length() > 0) {
            send.setEnabled(true);
        } else {
            send.setEnabled(false);
        }


        // get the nickame of the user
        Intent intent = getIntent();
        //String id = intent.getStringExtra("id");
        ToNickname = intent.getStringExtra("username");
        TouniqueId = intent.getStringExtra("public_id");
        Tofullname = intent.getStringExtra("fullname");
        Toemail = intent.getStringExtra("email");
        Toprofile_thumb_pic = intent.getStringExtra("profile_thumb_pic");
        //Nickname= (String) Objects.requireNonNull(getIntent().getExtras()).getString("username");

        chatBoxDBHelper = new ChatBoxDBHelper(getApplicationContext());

        chatBoxDBHelper.open();

        JSONArray jsonArray = chatBoxDBHelper.getLoggedinUserDetails();

        try {
            String logindata = jsonArray.getString(0);

            JSONObject jsonObject = new JSONObject(logindata);

            Nickname = jsonObject.getString(Loggedin_User_Username);
            uniqueId = jsonObject.getString(Loggedin_User_Token);
            profile_thumb_pic = jsonObject.getString(Loggedin_User_Thumb_pic_url);
            fullname = jsonObject.getString(Loggedin_User_Fullname);
            email = jsonObject.getString(Loggedin_User_Email);

            ;

            // thumburl = jsonObject.getString("_Thumb_pic_URL");

            //current_user_fullname.setText(jsonArray.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        toolbar_title.setText(Tofullname);
        toolbar_subtitle.setText(ToNickname);
        Picasso.get().load(Toprofile_thumb_pic).placeholder(R.drawable.ic_male).into(toolbar_image);


        onTypeButtonEnable();

        toolbar.setTitle("HElll");

        messagetxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                boolean typing = true;
                //String username = Nickname;
                //String uniqueId = uniqueId;
                String data = "{\n"+
                        "   \"typing\": \"" + typing + "\",\n" +
                        "   \"fromuser\": \"" + Nickname + "\",\n" +
                        "   \"touser\": \"" + ToNickname + "\",\n" +
                        "   \"fromuser_uniqueId\": \"" + uniqueId + "\"\n" +
                        "}";


                try {
                    JSONObject Jsonobj = new JSONObject(data);

                    socket.emit("on typingprivate", Jsonobj);

                    //Toast.makeText(getApplicationContext(),Jsonobj.toString(),Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
        //setting up listview
        MessageList = new ArrayList<>();

        myListView =  findViewById(R.id.messagelist);

        chatBoxAdapter = new ChatBoxAdapter(this, R.layout.item, MessageList);

        myListView.setAdapter(chatBoxAdapter);



        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if(!messagetxt.getText().toString().trim().isEmpty()){

                    String msg = messagetxt.getText().toString();

                    String data = "{\n"+
                            "   \"msg\": \"" + msg + "\",\n" +
                            "   \"fromuser\": \"" + Nickname + "\",\n" +
                            "   \"touser\": \"" + ToNickname + "\",\n" +
                            "   \"fromuser_uniqueId\": \"" + uniqueId + "\"\n" +
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

                    long unixTime = System.currentTimeMillis() / 1000L;

                    Toast.makeText(getApplicationContext(),unixTime+"",Toast.LENGTH_LONG).show();

                    String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getApplicationContext());

                    Toast.makeText(getApplicationContext(),inshort+"",Toast.LENGTH_LONG).show();


                    // make instance of message
                    Message m = new Message(Nickname,msg,uniqueId,"","",unixTime);


                    MessageList.add(m);

                    chatBoxAdapter.notifyDataSetChanged();


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

                        CropImage.activity()
                               // .setAspectRatio(1,1)
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


                        Message m = new Message("","","","",data,0);


                        MessageList.add(m);
                        //Userconnected = data;


                        chatBoxAdapter.notifyDataSetChanged();

                        //set the adapter for the recycler view

                        myListView.setAdapter(chatBoxAdapter);

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

                            //extract data from fired event

                            String nickname = data.getString("senderNickname");
                            String message = data.getString("message");
                            String uniqueId = data.getString("uniqueId");

                            long unixTime = System.currentTimeMillis() / 1000L;

                            //Toast.makeText(getApplicationContext(),unixTime+"",Toast.LENGTH_LONG).show();

                            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getApplicationContext());

                           // Toast.makeText(getApplicationContext(),inshort+"",Toast.LENGTH_LONG).show();

                            Message m = new Message(nickname,message,uniqueId,"",Toprofile_thumb_pic,unixTime);


                            //add the message to the messageList

                            MessageList.add(m);

                            // add the new updated list to the dapter
                            //chatBoxAdapter = new ChatBoxAdapter(MessageList);

                            // notify the adapter to update the recycler view

                            chatBoxAdapter.notifyDataSetChanged();

                            //set the adapter for the recycler view

                            //myListView.setAdapter(chatBoxAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bottomSheetDialog.isShowing()){
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
                    // Log.i(TAG, "run: " + args[0]);
                    try {
                        Boolean typingOrNot = data.getBoolean("typing");
                        String userName = data.getString("fromuser") + " is Typing...";
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
                    Toast.makeText(getApplicationContext(), "Failed to connect to Sever!", Toast.LENGTH_LONG).show();
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

               Uri imgresultUri = result.getUri();

                //File thumb_pic_file = new File(imgresultUri.getPath());
/*
                try {
/*
                    thumb_bitmap = new Compressor(SignupActivity.this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_pic_file);




                    //converting image to base64 string
                    ByteArrayOutputStream baos_thumb = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos_thumb);
                    byte[] thumb_imageBytes = baos_thumb.toByteArray();
                    thumb_imageString = Base64.encodeToString(thumb_imageBytes, Base64.DEFAULT);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgresultUri);


                    //setting_profile_pic.setImageBitmap(bitmap);

                    //converting image to base64 string
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);



                } catch (IOException e) {
                    e.printStackTrace();
                }

                */

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
