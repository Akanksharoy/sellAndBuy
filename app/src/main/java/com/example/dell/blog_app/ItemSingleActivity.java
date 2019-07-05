package com.example.dell.blog_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ItemSingleActivity extends AppCompatActivity {
        private  String mPost_key=null;
        private ImageView mItemImage;
        private TextView mItemTitle;
        private TextView mItemDesc;
        private TextView mUserId;
        private Button mSingleRemoveBtn;
        private Button mPurchaseBtn;
        private FirebaseAuth mAuth;
        String post_id;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_single);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("blog");
        mAuth=FirebaseAuth.getInstance();
        mItemImage=(ImageView)findViewById(R.id.singleItemImage);
        mItemTitle=(TextView)findViewById(R.id.singleItemTitle);
        mItemDesc=(TextView)findViewById(R.id.singleItemDesc);
        mUserId=(TextView)findViewById(R.id.user_name);
        mPurchaseBtn=(Button)findViewById(R.id.purchase);
        mSingleRemoveBtn=(Button)findViewById(R.id.singleRemoveBtn);

        mPost_key=getIntent().getExtras().getString("item_id");
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title=(String)dataSnapshot.child("title").getValue();
                String post_desc=(String)dataSnapshot.child("desc").getValue();
                String post_image=(String)dataSnapshot.child("images").getValue();
                String post_uid=(String)dataSnapshot.child("uid").getValue();
                post_id=(String)dataSnapshot.child("email").getValue();

                mItemTitle.setText(post_title);
                mItemDesc.setText(post_desc);
                mUserId.setText(post_id);
                Picasso.with(ItemSingleActivity.this).load(post_image).into(mItemImage);
                Picasso.with(ItemSingleActivity.this).load(post_image).into(mItemImage);
                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mSingleRemoveBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(mPost_key).removeValue();
                Intent mainIntent=new Intent(ItemSingleActivity.this,MainActivity.class);
                startActivity(mainIntent);

            }
        });
        mPurchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent chatintent=new Intent(ItemSingleActivity.this,ChatActivity.class);
                chatintent.putExtra("User_name",post_id);
                startActivity(chatintent);
                finish();
            }
        });
    }
}
