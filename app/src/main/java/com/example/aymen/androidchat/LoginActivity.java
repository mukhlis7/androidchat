package com.example.aymen.androidchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    private EditText loginusername, loginpassword;
    private String username;
    private Button btnsignuphere,btnlogin;
    boolean loginemail = false;

    public static final String SH_Fullname = "SH_FullName";
    public static final String SH_Username = "SH_UserName";
    public static final String SH_UniqueId = "SH_UniqueId";
    public static final String SH_Email = "SH_Email";
    public static final String SH_Profile_pic = "Profile_Pic";
    public static final String SH_Thumb_Profile_pic = "Thumb_Profile_pic";
    public static final String SH_Loggedin_Data = "SH_Loggedin_Data";

    public static final String  SH_Loggedin = "LoggedIn?";

    private final String URL="http://rt-chat07.herokuapp.com/login";
   // private final String URL="http://192.168.43.38/login";


    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    private ChatBoxDBHelper chatBoxDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.login_activity_toolbar);
        setSupportActionBar(toolbar);

        loginusername = findViewById(R.id.loginusername);

        loginpassword = findViewById(R.id.loginpassword);


        btnlogin = findViewById(R.id.btnlogin);
        btnsignuphere = findViewById(R.id.btnsignuphere);

        chatBoxDBHelper = new ChatBoxDBHelper(getApplicationContext());
        chatBoxDBHelper.open();

        progressDialog = new ProgressDialog(LoginActivity.this);



        String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {

                Toast.makeText(getApplicationContext(),"Permissions Granted!",Toast.LENGTH_LONG).show();
                // do your task.

                SharedPreferences prefs = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE);
                boolean looggedin  = prefs.getBoolean(SH_Loggedin,false);

                if (looggedin){

                    Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    LoginActivity.this.finish();

                }

            }
        });




        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressDialog.setMessage("Signing in, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                username = loginusername.getText().toString().trim();

                    if (!TextUtils.isEmpty(username)) {

                        int size=username.length();

                        if (size >= 6){

                            loginemail = EMAIL_ADDRESS_PATTERN.matcher(username).matches();

                        String password = loginpassword.getText().toString().trim();

                            if (!TextUtils.isEmpty(password)) {

                                int sizepasswd=password.length();

                                if (sizepasswd >= 6){



                                   //kali Toast.makeText(getApplicationContext(),"Email = "+loginemail, Toast.LENGTH_LONG).show();

                                String data = "{\n"+
                                        "   \"username\": \"" + username + "\",\n" +
                                        "   \"email\": \"" + loginemail + "\",\n" +
                                        "   \"password\": \"" + password + "\"\n" +
                                        "}";

                                if (haveNetworkConnection()){

                                    btnlogin.setEnabled(false);

                                    if (haveInternetConnection()){

                                        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        //      WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        Submit(data);


                                    }else {
                                        progressDialog.dismiss();
                                        btnlogin.setEnabled(true);

                                        Toast.makeText(getApplicationContext(), "Connected, But internet not available!", Toast.LENGTH_LONG).show();

                                    }

                                }else {
                                    progressDialog.dismiss();
                                    btnlogin.setEnabled(true);

                                    Toast.makeText(getApplicationContext(),"Not Connected!",Toast.LENGTH_LONG).show();
                                }


                            }else {

                                loginpassword.setError("Minimum six characters required!");

                            }

                        }else {

                            loginpassword.setError("Password is required!");

                        }

                        }else {

                        loginusername.setError("Minimum six chars required!");

                        }

                }else {

                    loginusername.setError("Username is required!");

                }

                }
        });


        btnsignuphere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        btnlogin.setEnabled(true);
        if (progressDialog.isShowing()){
            progressDialog.dismiss();

        }
    }

    private void Submit(String data)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String finalSavedata = data;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();

                }
                    btnlogin.setEnabled(true);
                   // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Log.i("Data from login",response);

                try {
                    JSONObject objres = new JSONObject(response);

                    boolean isregistered = objres.getBoolean("loggedin");
                    String message = objres.getString("message");

                    if (isregistered){

                        boolean emailv = objres.getBoolean("emailv");

                    if (emailv){
                        String username = objres.getString("username");
                        String email = objres.getString("email");
                        String fullname = objres.getString("fullname");
                        String token = objres.getString("token");
                        String profile_pic = objres.getString("profile_pic");


                        //Toast.makeText(getApplicationContext(),getThumbPicURL(profile_pic),Toast.LENGTH_LONG).show();



                        chatBoxDBHelper.addDataToLoggedin_User_Table(fullname,username,email,token,profile_pic,getThumbPicURL(profile_pic));


                        SharedPreferences.Editor editor = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE).edit();
                        editor.putBoolean(SH_Loggedin, true);
                        editor.apply();


                       // Toast.makeText(getApplicationContext(),uniqueId,Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this,ContactsActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);

                    }else {

                        Toast.makeText(getApplicationContext(),"Email not verified!",Toast.LENGTH_LONG).show();
                    }

                    }else {

                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    btnlogin.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Unable to parse Json",Toast.LENGTH_LONG).show();

                    //Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnlogin.setEnabled(true);
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

}
