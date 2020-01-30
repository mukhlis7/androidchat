package com.example.aymen.androidchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.example.aymen.androidchat.LoginActivity.SH_Email;
import static com.example.aymen.androidchat.LoginActivity.SH_Fullname;
import static com.example.aymen.androidchat.LoginActivity.SH_Loggedin_Data;
import static com.example.aymen.androidchat.LoginActivity.SH_Profile_pic;
import static com.example.aymen.androidchat.LoginActivity.SH_Thumb_Profile_pic;
import static com.example.aymen.androidchat.LoginActivity.SH_Username;

public class CurrentUserActivity extends AppCompatActivity {

    ImageView setting_profilepic;

    private static final String  URL = "";
    private ProgressDialog progressDialog;

    private Toolbar toolbar;

    private ChatBoxDBHelper chatBoxDBHelper;

    private String imageurl;
    private String thumburl;


    TextView current_user_fullname,current_user_username,current_user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user);

        toolbar = findViewById(R.id.currentuser_activity_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Account Settings");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //toolbar.setTitle("Profile");

        setting_profilepic = findViewById(R.id.setting_profilepic);

        current_user_fullname = findViewById(R.id.current_user_fullname);
        current_user_username = findViewById(R.id.current_user_username);
        current_user_email = findViewById(R.id.current_user_email);
        chatBoxDBHelper = new ChatBoxDBHelper(getApplicationContext());
        chatBoxDBHelper.open();

        SharedPreferences prefs = getSharedPreferences(SH_Loggedin_Data, MODE_PRIVATE);


        progressDialog = new ProgressDialog(CurrentUserActivity.this);
        progressDialog.setMessage("Fetching User Data, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        JSONArray jsonArray = chatBoxDBHelper.getLoggedinUserDetails();

        try {
            String logindata = jsonArray.getString(0);

            JSONObject jsonObject = new JSONObject(logindata);

            imageurl = jsonObject.getString("_Profile_pic_URL");
            thumburl = jsonObject.getString("_Thumb_pic_URL");

            current_user_username.setText(jsonObject.getString("_Username"));
            current_user_fullname.setText(jsonObject.getString("_Fullname"));
            current_user_email.setText(jsonObject.getString("_Email"));
            Picasso.get().load(thumburl).placeholder(R.drawable.ic_male).into(setting_profilepic);

            //current_user_fullname.setText(jsonArray.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Data From LocalDAtaBAse",chatBoxDBHelper.getLoggedinUserDetails().toString());


        progressDialog.dismiss();

        setting_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent fullImageIntent = new Intent(CurrentUserActivity.this, FullScreenImage.class);
                fullImageIntent.putExtra("image_url",imageurl);
                fullImageIntent.putExtra("thumb_image_url",thumburl);
                startActivity(fullImageIntent);

            }
        });


    }

    private void getProfileData(String data)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String finalSavedata = data;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();

                }
                //btnsignup.setEnabled(true);

                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                try {
                    JSONObject objres = new JSONObject(response);

                    boolean isregistered = objres.getBoolean("registered");
                    String message = objres.getString("message");


                    if (isregistered){

                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();


                    }else {

                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Unable to parse Json",Toast.LENGTH_LONG).show();

                    //Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();

                }
                //Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

}
