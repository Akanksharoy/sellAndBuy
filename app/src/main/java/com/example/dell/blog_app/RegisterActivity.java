package com.example.dell.blog_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private Button regiterbtn;
    private FirebaseAuth mAuth;
    private TextView Backtologin;

    private ProgressDialog mprogress;
    private DatabaseReference mDatatbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatatbase = FirebaseDatabase.getInstance().getReference().child("users");


        mprogress=new ProgressDialog(this);
        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        regiterbtn = (Button) findViewById(R.id.regiterbtn);
        Backtologin=(TextView) findViewById(R.id.textViewLogin);

        Backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

            }
        });


        regiterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        final String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();


        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mprogress.setMessage("signingUp..");
            mprogress.show();


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendEmail();/*Original pos*//*
                        /*String user_id = mAuth.getCurrentUser().getUid();
                        mDatatbase.child(user_id);
                        DatabaseReference current_user_db = mDatatbase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");
                        Intent mainIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);*/
                    }
                }
            });
        }


    }

    private void sendEmail() {
        final String name = nameField.getText().toString().trim();
        {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),""+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                            //String user_id = mAuth.getCurrentUser().getUid();
                            //mDatatbase.child(user_id);
                            //DatabaseReference current_user_db = mDatatbase.child(user_id);
                            //current_user_db.child("name").setValue(name);
                            //current_user_db.child("image").setValue("default");
                            Toast.makeText(RegisterActivity.this, "Successfully Registered, verification mail sent", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            finish();
                            /*copied from above*/


                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Verification mail hasnt been send", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }

    }
}

