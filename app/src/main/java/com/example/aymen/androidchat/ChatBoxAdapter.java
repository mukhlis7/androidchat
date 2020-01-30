package com.example.aymen.androidchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.RecyclerView;
import android.app.Activity;
import android.content.Context;
import android.icu.util.UniversalTimeScale;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


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
        if(!TextUtils.isEmpty(message.getUserJoined())){

            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.user_joined, parent, false);

            TextView messageText = convertView.findViewById(R.id.message_body);
            TextView fulltimeText = convertView.findViewById(R.id.fulltimestamp);

            //Log.i(MainActivity.TAG, "getView: is empty ");
            String userConnected = message.getUserJoined();
            long unixTime = message.getTimestamp();

            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getContext());


            messageText.setText(userConnected);
            fulltimeText.setText(inshort);
           // ChatBoxActivity.isnewUserconnected = false;

        }else if(message.getNickname().equals(ChatBoxActivity.Nickname)){
            //Log.i(MainActivity.TAG, "getView: " + message.getUniqueId() + " " + MainActivity.uniqueId);


            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_my_msg, parent, false);
            TextView messageText = convertView.findViewById(R.id.my_message_body);
            TextView mytimeText = convertView.findViewById(R.id.mytimestamp);

            messageText.setText(message.getMessage());
            long unixTime = message.getTimestamp();
            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getContext());

            mytimeText.setText(inshort);

        }else {
            //Log.i(MainActivity.TAG, "getView: is not empty");

            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item, parent, false);

            TextView messageText = convertView.findViewById(R.id.Othersmessage);
            TextView usernameText = (TextView) convertView.findViewById(R.id.Othernickname);
            TextView timeText = (TextView) convertView.findViewById(R.id.Othertimestamp);
            CircleImageView Othersprofile_image = convertView.findViewById(R.id.Othersprofile_image);


            messageText.setVisibility(View.VISIBLE);
            usernameText.setVisibility(View.VISIBLE);

            long unixTime = message.getTimestamp();
            String inshort = GetTimeFromStamp.getTimeAgo(unixTime,getContext());


            timeText.setText(inshort);
            messageText.setText(message.getMessage());
            usernameText.setText(message.getNickname());

            Picasso.get().load(message.getThumbimgurl()).placeholder(R.drawable.ic_male).into(Othersprofile_image);

        }

        return convertView;
    }
}