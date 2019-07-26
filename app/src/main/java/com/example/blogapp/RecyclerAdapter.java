package com.example.blogapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    List<Pair<Post,String>> list;
    Context context;
    public RecyclerAdapter(List<Pair<Post,String>> list, Context context)
    {
        this.list=list;
        this.context=context;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        MyHolder myHolder=new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        Post post=list.get(position).first;
        holder.name.setText(post.getUsername());
        holder.title.setText(post.getTitle());
        holder.desc.setText(post.getDesc());
        Glide.with(holder.image.getContext())
                .load(post.getImageUrl())
                .into(holder.image);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleActivity = new Intent(context, SinglePostActivity.class);
                singleActivity.putExtra("PostID",list.get(position).second);
                context.startActivity(singleActivity);

            }
        });


    }

    @Override
    public int getItemCount() {
        int arr = 0;


        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }
   //     Log.i("name", Integer.toString(arr));

        return arr;


    }


    public class MyHolder extends RecyclerView.ViewHolder {
        TextView name,title,desc;
        ImageView image;
        View mView;

        public MyHolder(View itemView) {
            super(itemView);

            mView=itemView;


            name = (TextView) itemView.findViewById(R.id.post_user);
            title= (TextView) itemView.findViewById(R.id.post_title);
            desc= (TextView) itemView.findViewById(R.id.post_desc);
            image=itemView.findViewById(R.id.image_post);

        }
    }
}
