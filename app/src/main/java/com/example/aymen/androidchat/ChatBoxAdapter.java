package com.example.aymen.androidchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.RecyclerView;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.icu.util.UniversalTimeScale;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_Message;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_MsgType;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_My_Thumb_IMG;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_Other_Thumb_IMG;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_Recipient;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_Sender;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_UnixTimeStamp;
import static com.example.aymen.androidchat.ChatBoxDBHelper.ChatBox_WHICH;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Fullname;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Thumb_pic_url;
import static com.example.aymen.androidchat.ChatBoxDBHelper.Loggedin_User_Contact_Username;




public class ChatBoxAdapter  extends ArrayAdapter<Message> {

    private List<Message> MessageList;


    public ChatBoxAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Message message = getItem(position);

                assert message != null;
            if (message.getSorr() == 0) {
                //Log.i(MainActivity.TAG, "getView: " + message.getUniqueId() + " " + MainActivity.uniqueId);

                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_my_msg, parent, false);

                TextView mymessageview = convertView.findViewById(R.id.my_message_body);

                ImageView myimage = convertView.findViewById(R.id.my_msg_image_view);

                CircleImageView myprofilepic = convertView.findViewById(R.id.My_MSGprofile_image);

                TextView mytimeStamp = convertView.findViewById(R.id.mytimestamp);



                if (message.getMsgType().equals("image")) {

                    mymessageview.setVisibility(View.GONE);

                    byte[] decodedString = Base64.decode(message.getMessage(), Base64.DEFAULT);
                    Bitmap decodedimage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                    myimage.setImageBitmap(decodedimage);


                    Picasso.get().load(message.getMyThumbimgurl()).placeholder(R.drawable.ic_male).into(myprofilepic);

                    long unixTime = message.getTimestamp();

                    String inshort = GetTimeFromStamp.getTimeAgo(unixTime, getContext());

                    mytimeStamp.setText(inshort);

                } else {


                    myimage.setVisibility(View.GONE);

                    mymessageview.setText(message.getMessage());

                    Picasso.get().load(message.getMyThumbimgurl()).placeholder(R.drawable.ic_male).into(myprofilepic);

                    long unixTime = message.getTimestamp();

                    String inshort = GetTimeFromStamp.getTimeAgo(unixTime, getContext());

                    mytimeStamp.setText(inshort);
                }

            } else {
                //Log.i(MainActivity.TAG, "getView: is not empty");


                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_other_msg, parent, false);

                TextView othermessageview = convertView.findViewById(R.id.Othersmessage);
                TextView otherusername = (TextView) convertView.findViewById(R.id.Otherusername);
                TextView othertimeStamp = (TextView) convertView.findViewById(R.id.Othertimestamp);
                ImageView otherimage = convertView.findViewById(R.id.other_msg_image_view);
                CircleImageView otherprofilepic = convertView.findViewById(R.id.Others_MSG_profile_image);


                if (message.getMsgType().equals("image")) {

                    othermessageview.setVisibility(View.GONE);

                    long unixTime = message.getTimestamp();
                    String inshort = GetTimeFromStamp.getTimeAgo(unixTime, getContext());


                    byte[] decodedString = Base64.decode(message.getMessage(), Base64.DEFAULT);
                    Bitmap decodedimage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                    otherimage.setImageBitmap(decodedimage);

                    othertimeStamp.setText(inshort);
                    otherusername.setText(message.getOtherUsername());

                    Picasso.get().load(message.getOtherThumbimgurl()).placeholder(R.drawable.ic_male).into(otherprofilepic);

                } else {


                    otherimage.setVisibility(View.GONE);


                    long unixTime = message.getTimestamp();
                    String inshort = GetTimeFromStamp.getTimeAgo(unixTime, getContext());

                    othertimeStamp.setText(inshort);
                    othermessageview.setText(message.getMessage());
                    otherusername.setText(message.getOtherUsername());

                    Picasso.get().load(message.getOtherThumbimgurl()).placeholder(R.drawable.ic_male).into(otherprofilepic);

                }
            }


            return convertView;
        }

}




/*


public class ChatBoxAdapter  extends CursorAdapter {

    private Cursor  cursor11;


    public ChatBoxAdapter(Context context, Cursor c) {

        super(context, c);

      cursor11=c;


    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
               
        int inwhich = cursor11.getInt(cursor11.getColumnIndexOrThrow(ChatBox_WHICH));



        if (inwhich == 1){

            String msgType = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_MsgType));

            if (msgType.equals("image")){

                String sender = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Sender));
                String recipient = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Recipient));
                String message = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Message));
                String unixTimeStamp = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_UnixTimeStamp));
                String my_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_My_Thumb_IMG));
                String other_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Other_Thumb_IMG));

                CircleImageView Otherthumbprofilepic = view.findViewById(R.id.Others_MSG_profile_image);
                TextView otherUsername = view.findViewById(R.id.Otherusername);
                TextView OtherMessage = view.findViewById(R.id.Othersmessage);
                ImageView OthersImageView = view.findViewById(R.id.other_msg_image_view);
                TextView Othertimestamp = view.findViewById(R.id.Othertimestamp);

                OtherMessage.setVisibility(View.GONE);

                byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
                Bitmap decodedimage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                OthersImageView.setImageBitmap(decodedimage);

                if (Otherthumbprofilepic != null) {
                    Picasso.get().load(other_thumb_pic_url).placeholder(R.drawable.ic_male).into(Otherthumbprofilepic);

                }
                otherUsername.setText(recipient);
                OtherMessage.setText(message);

                String inshort = GetTimeFromStamp.getTimeAgo(Long.parseLong(unixTimeStamp), context);

                Othertimestamp.setText(inshort);


            }else {


                String sender = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Sender));
                String recipient = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Recipient));
                String message = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Message));
                String unixTimeStamp = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_UnixTimeStamp));
                String my_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_My_Thumb_IMG));
                String other_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Other_Thumb_IMG));


                CircleImageView Otherthumbprofilepic = view.findViewById(R.id.Others_MSG_profile_image);
                TextView otherUsername = view.findViewById(R.id.Otherusername);
                ImageView OthersImageView = view.findViewById(R.id.other_msg_image_view);
                TextView OtherMessage = view.findViewById(R.id.Othersmessage);
                TextView Othertimestamp = view.findViewById(R.id.Othertimestamp);

                if (OthersImageView != null) {
                    OthersImageView.setVisibility(View.GONE);


                }


                if (Otherthumbprofilepic != null) {
                    Picasso.get().load(other_thumb_pic_url).placeholder(R.drawable.ic_male).into(Otherthumbprofilepic);

                    otherUsername.setText(recipient);
                    OtherMessage.setText(message);

                    String inshort = GetTimeFromStamp.getTimeAgo(Long.parseLong(unixTimeStamp), context);

                    Othertimestamp.setText(inshort);

                }



            }

        }else {

            String msgType = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_MsgType));


            if (msgType.equals("image")){


                String sender = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Sender));
                String recipient = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Recipient));
                String message = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Message));
                String unixTimeStamp = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_UnixTimeStamp));
                String my_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_My_Thumb_IMG));
                String other_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Other_Thumb_IMG));


                CircleImageView MYMSGthumbprofilepic = view.findViewById(R.id.My_MSGprofile_image);
                TextView MyMessage = view.findViewById(R.id.my_message_body);
                ImageView MyImageView = view.findViewById(R.id.my_msg_image_view);
                TextView mytimestamp = view.findViewById(R.id.mytimestamp);



                if (MYMSGthumbprofilepic != null) {
                    Picasso.get().load(my_thumb_pic_url).placeholder(R.drawable.ic_male).into(MYMSGthumbprofilepic);
                    //MyMessage.setText(message);
                    MyMessage.setVisibility(View.GONE);
                    // String inshort = GetTimeFromStamp.getTimeAgo(unixTimeStamp,view.getContext());

                    byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
                    Bitmap decodedimage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                    MyImageView.setImageBitmap(getRoundedCornerBitmap(decodedimage));

                    String inshort = GetTimeFromStamp.getTimeAgo(Long.parseLong(unixTimeStamp), context);


                    mytimestamp.setText(inshort);

                }

            }else {


                String sender = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Sender));
                String recipient = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Recipient));
                String message = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Message));
                String unixTimeStamp = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_UnixTimeStamp));
                String my_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_My_Thumb_IMG));
                String other_thumb_pic_url = cursor11.getString(cursor11.getColumnIndexOrThrow(ChatBox_Other_Thumb_IMG));


                CircleImageView MYMSGthumbprofilepic = view.findViewById(R.id.My_MSGprofile_image);
                TextView MyMessage = view.findViewById(R.id.my_message_body);
                TextView mytimestamp = view.findViewById(R.id.mytimestamp);
                ImageView MyImageView = view.findViewById(R.id.my_msg_image_view);



                if (MYMSGthumbprofilepic != null) {

                    MyImageView.setVisibility(View.GONE);
                    Picasso.get().load(my_thumb_pic_url).placeholder(R.drawable.ic_male).into(MYMSGthumbprofilepic);
                    //MyMessage.setText(message);
                }
                // String inshort = GetTimeFromStamp.getTimeAgo(unixTimeStamp,view.getContext());

                if (MyMessage != null) {
                    MyMessage.setText(message);

                    String inshort = GetTimeFromStamp.getTimeAgo(Long.parseLong(unixTimeStamp),context);


                    mytimestamp.setText(inshort);
                }

            }

        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int which = cursor11.getInt(cursor11.getColumnIndexOrThrow(ChatBox_WHICH));

        Log.i("ChatBoxAdapter which", which + "");


        if (which == 1){

            return LayoutInflater.from(context).inflate(R.layout.item_other_msg,parent,false);


        }else {

            return LayoutInflater.from(context).inflate(R.layout.item_my_msg,parent,false);


        }


    }





    /*

    private List<Message> MessageList;

    public ChatBoxAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Message message = getItem(position);

        assert message != null;
        if(message.getNickname().equals(ChatBoxActivity.Nickname)){

            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_my_msg, parent, false);
            TextView messageText = convertView.findViewById(R.id.my_message_body);

            TextView mytimeText = convertView.findViewById(R.id.mytimestamp);

            messageText.setText(message.getMessage());

            long unixTime = message.getTimestamp();

            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getContext());

            mytimeText.setText(inshort);

        }else {
            //Log.i(MainActivity.TAG, "getView: is not empty");

            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_other_msg, parent, false);

            TextView messageText = convertView.findViewById(R.id.Othersmessage);
            TextView usernameText = (TextView) convertView.findViewById(R.id.Othernickname);
            TextView timeText = (TextView) convertView.findViewById(R.id.Othertimestamp);
            CircleImageView Othersprofile_image = convertView.findViewById(R.id.Othersprofile_image);

            long unixTime = message.getTimestamp();
            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getContext());


            timeText.setText(inshort);
            messageText.setText(message.getMessage());
            usernameText.setText(message.getNickname());

            Picasso.get().load(message.getThumbimgurl()).placeholder(R.drawable.ic_male).into(Othersprofile_image);

        }

        return convertView;
    }



     */
/*

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 32;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


}

 */