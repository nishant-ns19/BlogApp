package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

import static java.lang.Math.floor;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle;
    private EditText textDesc;
    private Button postBtn;

    private ProgressBar progressBar;

    private Post newPost;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private DatabaseReference mDatabaseUsers;
    private StorageReference storage;
    private DatabaseReference databaseRef;


    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        // initializing objects
        postBtn=findViewById(R.id.post);
        imageBtn=findViewById(R.id.imageButton);
        textDesc=findViewById(R.id.desc);
        textTitle=findViewById(R.id.textTitle);
        storage=FirebaseStorage.getInstance().getReference();
        database=FirebaseDatabase.getInstance();


        databaseRef=database.getReference().child("BlogApp");


        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        progressBar=findViewById(R.id.progressBar);


       ScrollView scrollView= findViewById(R.id.scrollView);
       scrollView.setOnClickListener(this);

       LinearLayout linearLayout=findViewById(R.id.ll);
       linearLayout.setOnClickListener(this);

        newPost=new Post();
        if(mCurrentUser!=null)
        {
            mDatabaseUsers=database.getReference().child("Users").child(mCurrentUser.getUid());
//set uid here
            newPost.setUid(mCurrentUser.getUid());
            newPost.setUsername(mCurrentUser.getDisplayName());

        }


        //picking image from gallery
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(galleryIntent,"Complete Action Using"),GALLERY_REQUEST_CODE);
            }
        });

        // posting to Firebase
        postBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String Title=textTitle.getText().toString().trim();
            String desc=textDesc.getText().toString().trim();

            //check for blanks
            if(TextUtils.isEmpty(Title)||uri==null) {
                if(TextUtils.isEmpty(Title))
                Snackbar.make(findViewById(R.id.scrollView),"Please Enter Title",Snackbar.LENGTH_LONG).show();
                else if(uri==null)
                    Snackbar.make(findViewById(R.id.scrollView),"Please Upload Image",Snackbar.LENGTH_LONG).show();

            }
            else {
                postBtn.setEnabled(false);
                imageBtn.setClickable(false);
                newPost.setTitle(Title);
                newPost.setDesc(desc);
                Toast.makeText(PostActivity.this,"POSTING...",Toast.LENGTH_LONG).show();
                final StorageReference filePath=storage.child("post_images").child(uri.getLastPathSegment());
                UploadTask uploadTask=filePath.putFile(uri);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                progressBar.setProgress(0);

                   Task<Uri> urlTask= uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                           Log.i("progress",Double.toString((taskSnapshot.getBytesTransferred()*100.0)/taskSnapshot.getTotalByteCount()));
                           int p=(int)((taskSnapshot.getBytesTransferred()*100.0)/(taskSnapshot.getTotalByteCount()));
                           Log.i("progress",Integer.toString(p));
                           progressBar.setProgress(p);
                           Log.i("progress",Integer.toString(progressBar.getProgress()));

                       }
                   }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                       @Override
                       public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                           if(!task.isSuccessful()) {
                               imageBtn.setClickable(true);
                               progressBar.setVisibility(View.INVISIBLE);
                               postBtn.setEnabled(true);
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
                               Toast.makeText(getApplicationContext(),"Successfully Uploaded",Toast.LENGTH_SHORT).show();

                               newPost.setImageUrl(downloadUri.toString());

                               databaseRef.push().setValue(newPost, new DatabaseReference.CompletionListener() {
                                   @Override
                                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                       if(databaseError==null)
                                       {
                                           imageBtn.setClickable(true);
                                           postBtn.setEnabled(true);
                                           progressBar.setVisibility(View.INVISIBLE);
                                           Log.i("Upload","Successful");
                                           Intent home=new Intent(PostActivity.this,MainActivity.class);
                                           home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                           startActivity(home);
                                       }
                                       else
                                       {
                                           imageBtn.setClickable(true);
                                           postBtn.setEnabled(true);
                                           progressBar.setVisibility(View.INVISIBLE);
                                           Log.i("Upload","Unsuccessful");
                                           //TODO Delete uploaded storage
                                       }
                                   }
                               });



                           }
                           else {
                               postBtn.setEnabled(true);
                               progressBar.setVisibility(View.INVISIBLE);

                               Log.i("Storage Update","UnSuccessful");

                           //TODO Add failure intent
                           }

                       }
                   });

            }


        }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE&&resultCode==RESULT_OK) {
            uri=data.getData();
            imageBtn.setImageURI(uri);

        }
    }

    @Override
    public void onClick(View v) {
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);


    }
}





