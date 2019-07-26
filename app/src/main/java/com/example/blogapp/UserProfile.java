package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView name;
    private List<Pair<Post,String>> mypost;
    private static final int GALLERY_REQUEST_CODE = 2;
    private ImageView profilePicture;
    private boolean dpStatus=false;
    private  String dp;
    DatabaseReference cu_db;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        mAuth=FirebaseAuth.getInstance();
        recyclerView=findViewById(R.id.recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        name=findViewById(R.id.Name);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("BlogApp");
        profilePicture=findViewById(R.id.profileImage);
        cu_db=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        profilePicture.setClickable(false);

        cu_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dp=dataSnapshot.child("Images").getValue().toString();
                if(dp.equals("Default"))
                {
                    profilePicture.setClickable(true);
                }
                else
                {
                    profilePicture.setClickable(false);
                    Glide.with(profilePicture.getContext())
                            .load(dp)
                            .into(profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DP","Select");

                AlertDialog.Builder builder=new AlertDialog.Builder(UserProfile.this);
                builder.setMessage("You can change you profile picture only once. Please proceed only when you are sure.Press Yes to continue and No to cancel");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Start Activity for results

                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing

                        profilePicture.setClickable(false);
                        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                        startActivityForResult(Intent.createChooser(galleryIntent,"Complete Action Using"),GALLERY_REQUEST_CODE);

                    }
                });
                builder.show();


            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(UserProfile.this));



        mypost=new ArrayList<Pair<Post,String>>();

        adapter=new RecyclerAdapter(mypost,UserProfile.this);



        recyclerView.setAdapter(adapter);

        name.setText(mAuth.getCurrentUser().getDisplayName());

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Post current=dataSnapshot.getValue(Post.class);
                if(current.getUid().equals(mAuth.getCurrentUser().getUid())) {

                    mypost.add(new Pair<Post, String>(current, dataSnapshot.getKey()));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addPost) {

            startActivity(new Intent(UserProfile.this,PostActivity.class));
        }else if(id==R.id.action_logout) {
            mAuth.signOut();
            Intent logoutIntent = new Intent(UserProfile.this, RegisterActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);

        }
        else if(id==R.id.myProfile)
        {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePicture.setClickable(false);
        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            Log.i("Selected","Image");
            Uri uri=data.getData();
            Toast.makeText(getApplicationContext(),"Uploading, Please Wait..",Toast.LENGTH_SHORT).show();
            final StorageReference filePath= FirebaseStorage.getInstance().getReference().child("profile_pictures").child(mAuth.getCurrentUser().getUid());
            UploadTask uploadTask=filePath.putFile(uri);



            Task<Uri> urlTask= uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("progress",Double.toString((taskSnapshot.getBytesTransferred()*100.0)/taskSnapshot.getTotalByteCount()));

                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()) {
                        profilePicture.setClickable(true);
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        final Uri downloadUri=task.getResult();
                        Log.i("Storage Upload","Successful");
                        //TODO: add img to user profile

                        try
                        {
                            profilePicture.setClickable(false);
                            cu_db.child("Images").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                                    Glide.with(profilePicture.getContext())
                                            .load(downloadUri)
                                            .into(profilePicture);
                                    profilePicture.setClickable(true);

                                }
                            });


                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }








                    }
                    else {
                        profilePicture.setClickable(true);

                        Log.i("Storage Update","UnSuccessful");

                        //TODO Add failure intent
                    }

                }
            });

        }


    }
}



