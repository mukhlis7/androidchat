package com.example.aymen.androidchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SignupActivity extends AppCompatActivity {

    private EditText signupfullname, signupusername, e_mail, password1, repassword1;

    private Button btnloginhere,btnsignup;

    CircleImageView setting_profile_pic;
    private final String URL="http://rt-chat07.herokuapp.com/register";
    private final String ImgURL="https://7scripts.000webhostapp.com/uploadimg.php";
   // private final String URL="http://192.168.43.119/register";
    private Toolbar toolbar;

    private static int RESULT_LOAD_IMG = 1;

    private String fullname, username, e__mail, password;
    //RequestParams params = new RequestParams();
    private ProgressDialog progressDialog;
    private String uniqueId = "";
    private String imageString;
    private String thumb_imageString;

    private Uri imgresultUri;
    String imgPath, fileName;


    Bitmap bitmap;
    Bitmap thumb_bitmap;

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = findViewById(R.id.signup_activity_toolbar);
        setSupportActionBar(toolbar);


        signupfullname = findViewById(R.id.signupfullname);
        signupusername = findViewById(R.id.signupusername);
        e_mail = findViewById(R.id.e_mail);
        password1 = findViewById(R.id.password1);
        repassword1 = findViewById(R.id.repassword1);

        setting_profile_pic = findViewById(R.id.setting_profile_pic);


        btnloginhere = findViewById(R.id.btnloginhere);
        btnsignup = findViewById(R.id.btnsignup);

        uniqueId = UUID.randomUUID().toString();



        setting_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setMinCropWindowSize(500,500)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignupActivity.this);

            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fullname = signupfullname.getText().toString().trim();

                    if (!TextUtils.isEmpty(fullname)) {

                        int fullnamesize = fullname.length();

                        if (fullnamesize >= 5) {

                        username = signupusername.getText().toString().trim();

                            if (!TextUtils.isEmpty(username)) {

                                int usernamesize = signupusername.length();

                                if (usernamesize >= 6) {

                                e__mail = e_mail.getText().toString().trim();

                                if (!TextUtils.isEmpty(e__mail)) {

                                    if(EMAIL_ADDRESS_PATTERN.matcher(e__mail).matches()) {


                                        password = password1.getText().toString().trim();

                                        if (!TextUtils.isEmpty(password)) {

                                            int password1size = password.length();

                                            if (password1size >= 6) {

                                                String repassword = repassword1.getText().toString().trim();

                                                if (!TextUtils.isEmpty(repassword)) {

                                                    int repasswordsize = repassword.length();

                                                    if (repasswordsize >= 6) {


                                                        if(password.equals(repassword)) {



                                                            //Toast.makeText(getApplicationContext(),"UUID: " + uniqueId, Toast.LENGTH_LONG).show();



                                                            //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();

                                                            if (haveNetworkConnection()) {


                                                                if (haveInternetConnection()) {

                                                                    btnsignup.setEnabled(false);

                                                                    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                                    // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                    progressDialog = new ProgressDialog(SignupActivity.this);
                                                                    progressDialog.setMessage("Signing up, please wait...");
                                                                    progressDialog.setCancelable(false);
                                                                    progressDialog.setCanceledOnTouchOutside(false);
                                                                    progressDialog.show();

                                                                    String imgfilename = uniqueId + ".jpg";
                                                                    String thumb_imgfilename = "_"+uniqueId + ".jpg";

                                                                    Uploadimg(imageString,imgfilename, thumb_imageString,thumb_imgfilename);

                                                                    //Submit(data);

                                                                } else {

                                                                    Toast.makeText(getApplicationContext(), "Connected! But Internet not Available!", Toast.LENGTH_LONG).show();

                                                                }

                                                            } else {

                                                                Toast.makeText(getApplicationContext(), "Not Connected!", Toast.LENGTH_LONG).show();
                                                            }
                                                        }else {

                                                            Toast.makeText(SignupActivity.this,"Password Not matching",Toast.LENGTH_SHORT).show();

                                                        }


                                                    } else {

                                                        repassword1.setError("Minimum six characters Required!");

                                                    }

                                                } else {

                                                    repassword1.setError("Password is Required!");

                                                }

                                            } else {

                                                password1.setError("Minimum six characters Required!");

                                            }

                                        } else {

                                            password1.setError("Password is Required!");
                                        }

                                    }else {

                                        e_mail.setError("Invalid E-mail!");

                                    }

                                } else {

                                    signupfullname.setError("E-mail is Required!");
                                }

                            } else {

                                signupusername.setError("Minimum six characters Required!");

                            }

                        } else {

                            signupusername.setError("Username is Required!");
                        }

                    }else {

                        signupfullname.setError("Minimum five characters Required!");

                    }

                }else {

                    signupfullname.setError("Fullname is Required!");

                }

            }
        });


        btnloginhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        btnsignup.setEnabled(true);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
/*
    public boolean validate() {
        boolean temp = true;
        String pass = password1.getText().toString().trim();
        String cpass = repassword1.getText().toString().trim();
        else if(!pass.equals(cpass)){
            Toast.makeText(SignupActivity.this,"Password Not matching",Toast.LENGTH_SHORT).show();
            temp=false;
        }
        return temp;
    }


 */





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
        } catch (IOException | InterruptedException e)          { e.printStackTrace(); }
        return false;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imgresultUri = result.getUri();

                File thumb_pic_file = new File(imgresultUri.getPath());

                try {

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


                    setting_profile_pic.setImageBitmap(bitmap);

                    //converting image to base64 string
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);



                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void Uploadimg(final String data,final String filename, final String thumb_data,final String thumb_filename)
    {

        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, ImgURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject objres = new JSONObject(response);

                    String imgpath = objres.getString("imgpath");
                    String seccuss = objres.getString("seccuss");

                    if (seccuss.equals("true")){

                        String data = "{\n" +
                                "   \"fullname\": \"" + fullname + "\",\n" +
                                "   \"username\": \"" + username + "\",\n" +
                                "   \"email\": \"" + e__mail + "\",\n" +
                                "   \"password\": \"" + password + "\",\n" +
                                "   \"uniqueId\": \"" + uniqueId + "\",\n" +
                                "   \"profile_pic\": \"" + imgpath + "\"\n" +
                                "}";


                        Submit(data);

                    }else {

                        Toast.makeText(getApplicationContext(),"Failed to Upload Image!",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Failed to Parse JSON!",Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SignupActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", data);
                parameters.put("thumbimage", thumb_data);
                parameters.put("imgfilename", filename);
                parameters.put("thumbimgfilename", thumb_filename);
                return parameters;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue rQueue = Volley.newRequestQueue(SignupActivity.this);
        rQueue.add(request);
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
                btnsignup.setEnabled(true);

                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                try {
                    JSONObject objres = new JSONObject(response);

                    boolean isregistered = objres.getBoolean("registered");
                    String message = objres.getString("message");


                    if (isregistered){

                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }else {

                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Unable to parse Json",Toast.LENGTH_LONG).show();
                    btnsignup.setEnabled(true);
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
                btnsignup.setEnabled(true);
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

}


