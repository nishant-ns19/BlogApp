package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText emailField, nameField, passwordField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginTxtView;

    public  void register()
    {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        String name,email,pswd;
        name=nameField.getText().toString().trim();
        email=emailField.getText().toString().trim();
        pswd=passwordField.getText().toString().trim();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(pswd)) {
            nameField.getText().clear();
            emailField.getText().clear();
            passwordField.getText().clear();

            Snackbar.make(findViewById(R.id.rl),"Please Enter All the fields...",Snackbar.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(RegisterActivity.this,"Loading...",Toast.LENGTH_LONG).show();

            mAuth.createUserWithEmailAndPassword(emailField.getText().toString(),passwordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        final FirebaseUser  cu=mAuth.getCurrentUser();
                        String uid=cu.getUid();
                        final DatabaseReference cu_db=mDatabase.child(uid);
                        cu.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nameField.getText().toString()).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i("User added","Success");
                                cu_db.child("Name").setValue(cu.getDisplayName());
                                cu_db.child("Images").setValue("Default");
                                cu_db.child("Email").setValue(cu.getEmail());
                                Toast.makeText(RegisterActivity.this,"Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity((new Intent(RegisterActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
                                //TODO: start profile activity





                            }
                        });

                    }
                    else
                    {
                        Log.i( "createUser:failure", task.getException().toString());
                        Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }


                }
            });



        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerBtn=findViewById(R.id.registerBtn);
        emailField=findViewById(R.id.emailField);
        passwordField=findViewById(R.id.passwordField);
        nameField=findViewById(R.id.nameField);
        loginTxtView=findViewById(R.id.loginTxtView);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//TODO:set intent
                startActivity(intent);
            }
        });
        passwordField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER&&event.getAction()==KeyEvent.ACTION_DOWN)
                {
                    register();
                    return true;
                }
                return false;
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


