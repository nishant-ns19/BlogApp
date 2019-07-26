package com.example.blogapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public PostViewHolder(View view)
    {
        super(view);
        mView=view;
    }


    public void setTitle(String title) {
        TextView postTitle=mView.findViewById(R.id.post_title);
        postTitle.setText(title);

    }

    public void setDesc(String desc) {
        TextView postDesc=mView.findViewById(R.id.post_desc);
        postDesc.setText(desc);
    }


    public void setImageUrl(String imageUrl) {
        ImageView postImage=mView.findViewById(R.id.image_post);
        Glide.with(postImage.getContext())
                .load(imageUrl)
                .into(postImage);
//        Log.i("url",imageUrl);

    }

    public void setUsername(String username) {
        TextView user=mView.findViewById(R.id.post_user);
        user.setText(username);

    }

}
