package com.example.aymen.androidchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Token;
import static com.example.aymen.androidchat.LoginActivity.SH_Fullname;
import static com.example.aymen.androidchat.LoginActivity.SH_Loggedin;
import static com.example.aymen.androidchat.LoginActivity.SH_Loggedin_Data;
import static com.example.aymen.androidchat.LoginActivity.SH_Profile_pic;
import static com.example.aymen.androidchat.LoginActivity.SH_UniqueId;

public class ContactsActivity extends AppCompatActivity {

    private static final String SURL = "http://rt-chat07.herokuapp.com/search_chatusers/";
    //private static final String SURL = "http://192.168.43.38/search_chatusers/";

    private Toolbar toolbar;
    MaterialSearchView searchView;
    private RecyclerView search_recycler;
    private RecyclerView chat_recycler;
    private RecyclerView.Adapter searchadapter;
    private RecyclerView.Adapter chatadapter;
    public List<UserSearchResultModel> userSearchResultModelList;
    private String UniqueId = "";
    private String logintoken;
    private ProgressBar search_chatusers_bar;
    private TextView datafromdbstatus;
    private ChatBoxDBHelper chatBoxDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        toolbar = findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.Matirial_main_search_view);
        search_recycler = findViewById(R.id.searchrecylerview);
        search_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        chat_recycler = findViewById(R.id.recyclerchat);
        userSearchResultModelList = new ArrayList<>();
        search_chatusers_bar = findViewById(R.id.search_chatusers_bar);
        datafromdbstatus = findViewById(R.id.datafromdbstatus);
        searchadapter = new UserSearchResultAdapter(userSearchResultModelList,getApplicationContext());
        search_recycler.setItemAnimator(new DefaultItemAnimator());
        search_recycler.setAdapter(searchadapter);

        chatBoxDBHelper = new ChatBoxDBHelper(getApplicationContext());

        chatBoxDBHelper.open();

        JSONArray jsonArray = chatBoxDBHelper.getLoggedinUserDetails();

        try {
            String logindata = jsonArray.getString(0);

            JSONObject jsonObject = new JSONObject(logindata);

            logintoken = jsonObject.getString(Loggedin_User_Token);

            Log.i("LOgged in user TOken : ",logintoken);

           // thumburl = jsonObject.getString("_Thumb_pic_URL");

          //current_user_fullname.setText(jsonArray.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }


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

                setlistString(newText);

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

            }
        });

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
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

                            if (records == 1){
                                Toast.makeText(getApplicationContext(),"Records : " + records,Toast.LENGTH_LONG).show();

                                JSONObject dataobject = new JSONObject(datafromdb);

                                userSearchResultModelList.clear();

                                UserSearchResultModel testModel = new UserSearchResultModel(
                                        dataobject.getString("fullname"),
                                        dataobject.getString("username"),
                                        dataobject.getString("profile_pic"),
                                        dataobject.getString("email"),
                                        //       dataobject.getString("public_id"),
                                        getThumbPicURL(dataobject.getString("profile_pic"))

                                );
                                userSearchResultModelList.add(testModel);

                                searchadapter.notifyDataSetChanged();

                            }else {

                                JSONObject dataobject = new JSONObject(datafromdb);

                                JSONArray usernames = dataobject.getJSONArray("username");


                                JSONArray fullnames = dataobject.getJSONArray("fullname");


                                JSONArray emails = dataobject.getJSONArray("email");


                                //JSONArray public_ids = dataobject.getJSONArray("public_id");


                                JSONArray profile_pics = dataobject.getJSONArray("profile_pic");

                                //Log.i("JSon Object From : ", usernames.toString());

                                userSearchResultModelList.clear();


                                for (int i=0; i<usernames.length(); i++) {

                                    UserSearchResultModel testModel = new UserSearchResultModel(
                                            fullnames.getString(i),
                                            usernames.getString(i),
                                            profile_pics.getString(i),
                                            emails.getString(i),
                                            // public_ids.getString(i),
                                            getThumbPicURL(profile_pics.getString(i))

                                    );
                                    userSearchResultModelList.add(testModel);
                                }

                                searchadapter.notifyDataSetChanged();

                            }

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



}
