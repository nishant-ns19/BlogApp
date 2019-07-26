package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePostActivity extends AppCompatActivity {
    private ImageView singleImage;
    private TextView singleTitle, singleDesc, singleUsername;
    String post_key = null;
    private DatabaseReference mDatabase;
    private Button deleteBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        singleImage = (ImageView) findViewById(R.id.singleImageview);
        singleTitle = (TextView) findViewById(R.id.singleTitle);
        singleDesc = (TextView) findViewById(R.id.singleDesc);
        singleUsername=findViewById(R.id.singleUsername);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("BlogApp");
        post_key = getIntent().getExtras().getString("PostID");
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        mAuth = FirebaseAuth.getInstance();
        deleteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(post_key).removeValue();
                Intent mainintent = new Intent(SinglePostActivity.this, MainActivity.class);
                mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainintent);
            }
        });
        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title= (String) dataSnapshot.child("title").getValue();
                String description=(String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("imageUrl").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();
                Glide.with(singleImage.getContext())
                        .load(post_image)
                        .into(singleImage);
                singleTitle.setText(title);
                singleDesc.setText(description);
                singleUsername.setText(mAuth.getCurrentUser().getDisplayName());
                if(mAuth.getCurrentUser().getUid().equals(post_uid))
                {
                    deleteBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

