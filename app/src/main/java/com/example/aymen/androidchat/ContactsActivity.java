package com.example.aymen.androidchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.database.DatabaseUtils;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.aymen.androidchat.ChatBoxDBHelper.AlphaNumaricString;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_Message;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_ChatTable;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Email;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Fullname;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_ID;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Profile_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Thumb_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Username;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Token;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Username;
import static com.example.aymen.androidchat.LoginActivity.SH_Fullname;
import static com.example.aymen.androidchat.LoginActivity.SH_Loggedin;
import static com.example.aymen.androidchat.LoginActivity.SH_Loggedin_Data;
import static com.example.aymen.androidchat.LoginActivity.SH_Profile_pic;
import static com.example.aymen.androidchat.LoginActivity.SH_UniqueId;

public class ContactsActivity extends AppCompatActivity {

    private static final String SURL = "http://rt-chat07.herokuapp.com/search_chatusers/";
    //private static final String SURL = "http://192.168.43.38/search_chatusers/";
   // private static final String SURL = "http://192.168.12.1/search_chatusers/";

    private static final String SIOURL = "http://rt-chat07.herokuapp.com/";
    // private static final String SIOURL = "http://192.168.12.1/";


    private Toolbar toolbar;
    MaterialSearchView searchView;
    private RecyclerView search_recycler;
    private ListView chat_recycler;
    private RecyclerView.Adapter searchadapter;
    private RecyclerView.Adapter chatadapter;
    public List<UserSearchResultModel> userSearchResultModelList;
    private String UniqueId = "";
    private String logintoken;
    private String LoggedinUsername;
    //private String logintoken;
    private ProgressBar search_chatusers_bar;
    private TextView datafromdbstatus;
    private ChatBoxDBHelper chatBoxDBHelper;

    private ContactsAdapter contactsAdapter;


    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        toolbar = findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.Matirial_main_search_view);
        search_recycler = findViewById(R.id.searchrecylerview);
        search_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        chat_recycler = findViewById(R.id.Contacts_ListView);
        userSearchResultModelList = new ArrayList<>();
        search_chatusers_bar = findViewById(R.id.search_chatusers_bar);
        datafromdbstatus = findViewById(R.id.datafromdbstatus);



        searchadapter = new UserSearchResultAdapter(userSearchResultModelList,getApplicationContext());
        search_recycler.setItemAnimator(new DefaultItemAnimator());
        search_recycler.setAdapter(searchadapter);

        chatBoxDBHelper = new ChatBoxDBHelper(getApplicationContext());

        chatBoxDBHelper.open();

        Cursor cu = chatBoxDBHelper.getLoggedinUserDetails();


        //try {
            //String logindata = jsonArray.getString(0);

           // JSONObject jsonObject = new JSONObject(logindata);
        Log.i("LOgged in user TOken : ", DatabaseUtils.dumpCursorToString(cu));



        if (cu.moveToFirst()){
            do{
                logintoken = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Token));

                LoggedinUsername = cu.getString(cu.getColumnIndexOrThrow(Loggedin_User_Username));
                Log.i("LOgged in user TOken : ",logintoken);
            }while(cu.moveToNext());
        }
        cu.close();



           // thumburl = jsonObject.getString("_Thumb_pic_URL");

          //current_user_fullname.setText(jsonArray.getString(1));


        Cursor userDetails = chatBoxDBHelper.getUserContactsDetails();

       DatabaseUtils.dumpCursorToString(userDetails);

        Log.i("user Contacts : ",DatabaseUtils.dumpCursorToString(userDetails));

        if (userDetails != null && userDetails.getCount() >0){

            contactsAdapter = new ContactsAdapter(this, userDetails,false);

            // Attach cursor adapter to the ListView
            chat_recycler.setAdapter(contactsAdapter);

        }

/*
        try {
            socket = IO.socket(SIOURL);
            socket.connect();
            socket.emit("join", LoggedinUsername);
           // socket.on("on typingprivate", onTyping);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        } catch (URISyntaxException e) {
            e.printStackTrace();

        }



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

                            String sendername = data.getString("sender");
                            String message = data.getString("msg");
                            String recipient = data.getString("recipient");
                            String email = data.getString("email");
                            String profile_pic = data.getString("profile_pic_url");
                            String fullname = data.getString("fullname");
                            String profile_thumb_pic = getThumbPicURL(profile_pic);
                            String sender_uniqueId = data.getString("uniqueId");
                            String msgType = data.getString("msgType");

                            long unixTime = System.currentTimeMillis() / 1000L;

                            //Toast.makeText(getApplicationContext(),unixTime+"",Toast.LENGTH_LONG).show();

                            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getApplicationContext());
                            chatBoxDBHelper.open();

                            // Toast.makeText(getApplicationContext(),inshort+"",Toast.LENGTH_LONG).show();

                            String UserChatTable = chatBoxDBHelper.addDataToChatContacts_Table(fullname,sendername,email,sender_uniqueId,profile_thumb_pic,profile_pic);


                            chatBoxDBHelper.SaveSentRecivedMSG(UserChatTable, sendername, recipient,message,"text",Long.toString(unixTime),1,profile_thumb_pic,profile_thumb_pic);

                            Cursor userconDetails = chatBoxDBHelper.getUserContactsDetails();

                            //DatabaseUtils.dumpCursorToString(userconDetails);

                            Log.i("user Contacts : ",DatabaseUtils.dumpCursorToString(userconDetails));

                            if (userconDetails != null && userconDetails.getCount() >0){

                                contactsAdapter = new ContactsAdapter(ContactsActivity.this, userconDetails,false);

                                // Attach cursor adapter to the ListView
                                chat_recycler.setAdapter(contactsAdapter);

                            }

                            chatBoxDBHelper.close();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });


 */

        chat_recycler.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Cursor cursor = (Cursor) chat_recycler.getItemAtPosition(pos);
                final int contact_id = cursor.getInt(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_ID));
                final String ContactChatTable = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_ChatTable));


                AlertDialog.Builder builder1 = new AlertDialog.Builder(ContactsActivity.this);
                builder1.setTitle("Delete Contact");
                builder1.setMessage("Are You Sure You Want To Delete this Contact?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                chatBoxDBHelper.open();
                                chatBoxDBHelper.deleteContactfromdb(contact_id,ContactChatTable);

                                // String UserChatTable = chatBoxDBHelper.addDataToChatContacts_Table(fullname,sendername,email,sender_uniqueId,profile_thumb_pic,profile_pic);


                                //chatBoxDBHelper.SaveSentRecivedMSG(UserChatTable, sendername, recipient,message,"text",Long.toString(unixTime),1,profile_thumb_pic,profile_thumb_pic);

                                Cursor userconDetails = chatBoxDBHelper.getUserContactsDetails();

                                //DatabaseUtils.dumpCursorToString(userconDetails);

                                Log.i("user Contacts : ",DatabaseUtils.dumpCursorToString(userconDetails));

                                if (userconDetails != null && userconDetails.getCount() >0){

                                    contactsAdapter = new ContactsAdapter(ContactsActivity.this, userconDetails,false);

                                    // Attach cursor adapter to the ListView
                                    chat_recycler.setAdapter(contactsAdapter);

                                }

                                chatBoxDBHelper.close();

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });


        chat_recycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) chat_recycler.getItemAtPosition(position);

                Log.i("Current COntact",DatabaseUtils.dumpCursorToString(cursor));

                String username = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Username));
                String Fullname = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Fullname));
                String Thumb_pic_URL = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Thumb_pic_url));
                String UserChatTable = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_ChatTable));
                String profilepic = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Profile_pic_url));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(Loggedin_User_Contact_Email));

                Toast.makeText(getApplicationContext(),UserChatTable, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(),ChatBoxActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("fullname", Fullname);
                intent.putExtra("email", email);
                intent.putExtra("UserChatTable", UserChatTable);
                //intent.putExtra("public_id", searchResultModelItem.getPublic_id());
                intent.putExtra("profile_pic", profilepic);
                intent.putExtra("profile_thumb_pic", Thumb_pic_URL);
                startActivity(intent);


            }
        });

        chatBoxDBHelper.close();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_manu, menu);

        MenuItem item = menu.findItem(R.id.menu_main_action_search);
        searchView.setMenuItem(item);


        searchView.setVoiceSearch(false);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //SharedPreferences prefs = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE);
                //String public_id = prefs.getString(SH_Profile_pic, "No Public_id defined");

                search_chatusers_bar.setVisibility(View.VISIBLE);
                userSearchResultModelList.clear();
                searchadapter.notifyDataSetChanged();

                String data = "{\n"+
                        "   \"query\": \"" + query + "\"\n" +
                       // "   \"public_id\": \"" + UniqueId + "\"\n" +
                       // "   \"password\": \"" + password + "\"\n" +
                        "}";

                SearchUsers(data);

               // setlist(Integer.parseInt(query));

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                datafromdbstatus.setVisibility(View.GONE);
                search_chatusers_bar.setVisibility(View.GONE);

                //setlistString(newText);

                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                chat_recycler.setVisibility(View.GONE);
                search_recycler.setVisibility(View.VISIBLE);

                //setlist(12);
            }

            @Override
            public void onSearchViewClosed() {

                chat_recycler.setVisibility(View.VISIBLE);
                search_recycler.setVisibility(View.GONE);

                Cursor userDetails = chatBoxDBHelper.getUserContactsDetails();

                DatabaseUtils.dumpCursorToString(userDetails);

                Log.i("user Contacts : ",DatabaseUtils.dumpCursorToString(userDetails));

                if (userDetails != null && userDetails.getCount() >0){

                    contactsAdapter = new ContactsAdapter(ContactsActivity.this, userDetails,false);

                    // Attach cursor adapter to the ListView
                    chat_recycler.setAdapter(contactsAdapter);

                }

            }
        });

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_other_msg clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.account_settings:
                accountSettings();
                return true;

            case R.id.menu_main_action_search:


            case R.id.account_logout:

                LogOUT();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void LogOUT() {

        chatBoxDBHelper.logoutdb();
        chatBoxDBHelper.close();

        SharedPreferences.Editor editor = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE).edit();
        editor.putBoolean(SH_Loggedin, false);
        editor.apply();

        Intent intent = new Intent(ContactsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ContactsActivity.this.finish();

    }

    public void setlist(int max){

        userSearchResultModelList.clear();

        for (int i = 0; i<=max;i++) {
            UserSearchResultModel testModel = new UserSearchResultModel(
                    "Mukhlis : " + i,
                    "mukhlis: " + i,
                    "jkdjdccjdjdgdj",
                    "jkdjdccjdjdgdj",
                    "jkdjdccjdjdgdj"
                  //  "jkdjdccjdjdgdj"

            );
            userSearchResultModelList.add(testModel);
        }

        searchadapter.notifyDataSetChanged();


    }

    public String getThumbPicURL(String imgurl){

        String[] pic_filepath_parts = imgurl.split("(?=/)");

        String[] pic_path_without_filename = Arrays.copyOf(pic_filepath_parts, pic_filepath_parts.length-1);


        StringBuilder builder = new StringBuilder();
        for(String s : pic_path_without_filename) {
            builder.append(s);
        }
        String pic_baseurl = builder.toString();

        //String pic_baseurl = Arrays.toString(pic_path_without_filename);

        String pic_fileName = imgurl.substring(imgurl.lastIndexOf('/') + 1);

        String thumb_filename = "/_"+pic_fileName;

        return pic_baseurl + thumb_filename;

    }

    public void setlistString(String  max){

        userSearchResultModelList.clear();

        for (int i = 0; i<=10;i++) {
            UserSearchResultModel testModel = new UserSearchResultModel(
                    max+" : " + i,
                    max+" : " + i,
                    "jkdjdccjdjdgdj",
                   // "jkdjdccjdjdgdj",
                    "jkdjdccjdjdgdj",
                    "jkdjdccjdjdgdj"

            );
            userSearchResultModelList.add(testModel);
        }

        searchadapter.notifyDataSetChanged();


    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            if (contactsAdapter != null){
                contactsAdapter.notifyDataSetChanged();


            }
        } else {
            super.onBackPressed();
        }
    }

    private void accountSettings() {

        Intent intent = new Intent(ContactsActivity.this,CurrentUserActivity.class);
        startActivity(intent);
    }

    private void SearchUsers(String data)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String finalSavedata = data;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                search_chatusers_bar.setVisibility(View.GONE);

                //btnlogin.setEnabled(true);
                // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Log.i("Data from Search: ",response);

                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();


                try {


                    JSONObject objres = new JSONObject(response);

                    boolean loggedin = objres.getBoolean("loggedin");
                    String  message = objres.getString("message");

                    Toast.makeText(getApplicationContext(),"User loggedin! = " + loggedin,Toast.LENGTH_SHORT).show();

                    if (loggedin){

                        String datafromdb = objres.getString("datafromdb");

                        boolean haveuser = objres.getBoolean("user");
                        int records = objres.getInt("records");

                        Toast.makeText(getApplicationContext(),"User exists! = " + haveuser,Toast.LENGTH_SHORT).show();


                        if (haveuser){


                                Toast.makeText(getApplicationContext(),"Records : " + records,Toast.LENGTH_LONG).show();

                                /*

                                JSONObject dataobject = new JSONObject(datafromdb);

                            Log.i("Data from db: ",dataobject.toString());


                            userSearchResultModelList.clear();


                            for (int i=0; i<datafromdb.length(); i++) {

                                UserSearchResultModel testModel = new UserSearchResultModel(
                                        dataobject.getString("fullname"),
                                        dataobject.getString("username"),
                                        dataobject.getString("profile_pic"),
                                        dataobject.getString("email"),
                                        //       dataobject.getString("public_id"),
                                        getThumbPicURL(dataobject.getString("profile_pic"))

                                );
                                userSearchResultModelList.add(testModel);

                            }

                                searchadapter.notifyDataSetChanged();


                                 */


                                 JSONArray usersarray = new JSONArray(datafromdb);


                                //Log.i("JSon Object From : ", usernames.toString());

                                userSearchResultModelList.clear();


                                for (int i=0; i<usersarray.length(); i++) {

                                    JSONObject Userobj = new JSONObject(String.valueOf(usersarray.getJSONObject(i)));



                                        UserSearchResultModel testModel = new UserSearchResultModel(
                                                Userobj.getString("fullname"),
                                                Userobj.getString("username"),
                                                Userobj.getString("profile_pic"),
                                                Userobj.getString("email"),
                                                getThumbPicURL(Userobj.getString("profile_pic"))


                                        );
                                        userSearchResultModelList.add(testModel);

                                }

                                searchadapter.notifyDataSetChanged();



                        }else {

                            datafromdbstatus.setVisibility(View.VISIBLE);

                        }


                    }else {

                        chatBoxDBHelper.logoutdb();
                        chatBoxDBHelper.close();

                        SharedPreferences.Editor editor = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE).edit();
                        editor.putBoolean(SH_Loggedin, false);
                        editor.apply();

                        Intent intent = new Intent(ContactsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }



                } catch (JSONException e) {
                   // btnlogin.setEnabled(true);
                    //progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Unable to parse Json",Toast.LENGTH_LONG).show();
                    search_chatusers_bar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // btnlogin.setEnabled(true);
               // progressDialog.dismiss();
                search_chatusers_bar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Server Error!", Toast.LENGTH_SHORT).show();

                //Log.v("VOLLEY", error.toString());
            }
        }) {


            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("x-access-token", logintoken);
                return params;
            }



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

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean haveInternetConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


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

}
