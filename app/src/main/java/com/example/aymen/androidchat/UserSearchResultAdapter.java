package com.example.aymen.androidchat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchResultAdapter extends RecyclerView.Adapter<UserSearchResultAdapter.ViewHolder> {


    private List<UserSearchResultModel> searchResultModelList;
    private Context context;


    public UserSearchResultAdapter(List<UserSearchResultModel> searchResultModelList, Context context) {
        this.searchResultModelList = searchResultModelList;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final UserSearchResultModel searchResultModelItem = searchResultModelList.get(position);

        holder.fullname.setText(searchResultModelItem.getFullname());
        holder.username.setText(searchResultModelItem.getUsername());
        Picasso.get().load(searchResultModelItem.getProfile_Thumb_picurl()).placeholder(R.drawable.ic_male).into(holder.profile_pic_url);

        holder.user_Search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,searchResultModelItem.getProfile_Thumb_picurl(),Toast.LENGTH_LONG).show();


                Intent intent = new Intent(context,ChatBoxActivity.class);
                intent.putExtra("username", searchResultModelItem.getUsername());
                intent.putExtra("fullname", searchResultModelItem.getFullname());
                intent.putExtra("email", searchResultModelItem.getEmail());
                //intent.putExtra("public_id", searchResultModelItem.getPublic_id());
                intent.putExtra("profile_pic", searchResultModelItem.getProfile_picurl());
                intent.putExtra("profile_thumb_pic", searchResultModelItem.getProfile_Thumb_picurl());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return searchResultModelList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{


        public TextView fullname;
        public TextView username;
        public CircleImageView profile_pic_url;
        public RelativeLayout user_Search_layout;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname =  itemView.findViewById(R.id.searchlist_fullname);
            username =  itemView.findViewById(R.id.searchlist_username);
            profile_pic_url =  itemView.findViewById(R.id.searchlist_image);
            user_Search_layout =  itemView.findViewById(R.id.user_Search_layout);

        }
    }

}
