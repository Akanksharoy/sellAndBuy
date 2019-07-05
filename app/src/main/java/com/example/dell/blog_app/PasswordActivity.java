package com.example.dell.blog_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {
    private EditText Email;
    private Button reset;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);


        Email=(EditText)findViewById(R.id.editText);
        reset=(Button) findViewById(R.id.buttonPwdReset);
        mAuth=FirebaseAuth.getInstance();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String useremail=Email.getText().toString().trim();
                if(useremail.equals("")){
                    Toast.makeText(PasswordActivity.this,"Please enter your registred email id",Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordActivity.this,"Password reset emai sent",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordActivity.this,LoginActivity.class));
                            }else {
                                Toast.makeText(PasswordActivity.this,"Error in sending email",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }


            }


        });
    }
}
