package com.example.dell.blog_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    EditText sub;
    EditText msg;
    TextView email;
    Button b;
    String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        email=(TextView) findViewById(R.id.email);


        sub=(EditText)findViewById(R.id.subject);
        msg=(EditText)findViewById(R.id.message);
        b=(Button)findViewById(R.id.send);
        name=getIntent().getExtras().getString("User_name");
        email.setText(name);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();


                //String id=email.getText().toString();

                String subject=sub.getText().toString();
                String message = msg.getText().toString();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL,new String[]{name});
                i.putExtra(Intent.EXTRA_SUBJECT,subject);
                i.putExtra(Intent.EXTRA_TEXT,message);
                i.setType("message/rfc822");
                startActivity(Intent.createChooser(i,"Select Email App"));
                finish();
            }
        });
    }


}







